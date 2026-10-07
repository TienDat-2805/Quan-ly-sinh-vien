package HD.educaze;

import HD.educaze.dto.Requests.*;
import HD.educaze.model.Document;
import HD.educaze.repository.*;
import HD.educaze.service.*;
import com.fasterxml.jackson.databind.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.*;
import org.springframework.web.server.ResponseStatusException;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:document-tests;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc @ActiveProfiles("test")
class DocumentIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired DocumentRepository documents;
    @Autowired StudentRepository students;
    @Autowired AcademicClassRepository classes;
    @Autowired GradeRepository grades;
    @Autowired ManagementService management;
    @MockitoBean DocumentStorageService storage;
    final Map<String, byte[]> objects = new ConcurrentHashMap<>();
    long studentId;
    @BeforeEach void setup() throws Exception {
        documents.deleteAll(); grades.deleteAll(); students.deleteAll(); classes.deleteAll(); objects.clear();
        var academicClass = management.saveClass(null, new ClassInput("DOC01", "Document test class", "IT", "Test advisor", 2026, 45, null, null));
        studentId = management.saveStudent(null, new StudentInput("DOCST01", "Document Student", "OTHER", LocalDate.of(2004, 1, 1), "document@student.example.edu.vn", "0901234567", "Can Tho", "ACTIVE", academicClass.id())).id();
        doAnswer(call -> { objects.put(call.getArgument(0), ((java.io.InputStream) call.getArgument(1)).readAllBytes()); return null; }).when(storage).store(anyString(), any(), anyLong(), anyString());
        when(storage.download(anyString())).thenAnswer(call -> objects.get(call.getArgument(0)));
        doAnswer(call -> { objects.remove(call.getArgument(0)); return null; }).when(storage).remove(anyString());
    }
    private MockMultipartFile file(String name) { return new MockMultipartFile("files", name, "application/octet-stream", ("sample bytes: " + name).getBytes(StandardCharsets.UTF_8)); }
    private JsonNode upload(MockMultipartFile... files) throws Exception {
        var request = multipart("/api/students/{id}/documents", studentId);
        for (var file : files) request.file(file);
        return json.readTree(mvc.perform(request.with(user("admin@educare.edu.vn").roles("ADMIN")).with(csrf())).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }
    private JsonNode replace(String id, String name) throws Exception {
        return json.readTree(mvc.perform(multipart(HttpMethod.PUT, "/api/documents/{id}", id).file(new MockMultipartFile("file", name, "application/octet-stream", ("replacement " + name).getBytes(StandardCharsets.UTF_8))).with(user("lecturer@example.edu.vn").roles("LECTURER")).with(csrf())).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
    }
    @Test void multiUploadPersistsStudentForeignKeyAndMetadata() throws Exception {
        JsonNode result = upload(file("transcript.PdF"), file("enrollment.docx"));
        assertThat(result.size()).isEqualTo(2);
        assertThat(documents.count()).isEqualTo(2);
        assertThat(objects).hasSize(2);
        for (Document document : documents.findAll()) {
            assertThat(document.getStudent().getId()).isEqualTo(studentId);
            assertThat(document.getOwner()).isEqualTo("admin@educare.edu.vn");
            assertThat(document.getVersion()).isEqualByComparingTo("1.00");
            assertThat(document.getDeleted()).isFalse(); assertThat(document.getActive()).isTrue();
            assertThat(document.getPath()).startsWith("students/" + studentId + "/" + document.getId() + "/");
            assertThat(document.getCreationTime()).isNotNull();
        }
        mvc.perform(get("/api/students/{id}/documents", studentId).with(user("lecturer").roles("LECTURER"))).andExpect(status().isOk()).andExpect(jsonPath("$[0].path").doesNotExist());
    }
    @Test void allAllowedExtensionsAcceptMixedCaseAndNamesCannotTraverseStorage() throws Exception {
        for (String extension : List.of("pdf", "XLSX", "xls", "Doc", "docx", "JpG", "jpeg")) upload(file("../../folder/sample." + extension));
        assertThat(documents.count()).isEqualTo(7);
        assertThat(documents.findAll()).allSatisfy(document -> {
            assertThat(document.getName()).doesNotContain("/", "\\");
            assertThat(document.getPath()).doesNotContain("../");
        });
    }
    @Test void rejectsWholeBatchBeforeStorageWhenAnyExtensionIsInvalid() throws Exception {
        mvc.perform(multipart("/api/students/{id}/documents", studentId).file(file("allowed.pdf")).file(file("blocked.exe")).with(user("admin").roles("ADMIN")).with(csrf())).andExpect(status().isBadRequest());
        verify(storage, never()).store(anyString(), any(), anyLong(), anyString());
        assertThat(documents.count()).isZero();
    }
    @Test void missingStudentAndEmptyFilesAreRejected() throws Exception {
        mvc.perform(multipart("/api/students/{id}/documents", Long.MAX_VALUE).file(file("valid.pdf")).with(user("admin").roles("ADMIN")).with(csrf())).andExpect(status().isNotFound());
        mvc.perform(multipart("/api/students/{id}/documents", studentId).file(new MockMultipartFile("files", "empty.pdf", "application/pdf", new byte[0])).with(user("admin").roles("ADMIN")).with(csrf())).andExpect(status().isBadRequest());
        mvc.perform(get("/api/students/{id}/documents", Long.MAX_VALUE).with(user("admin").roles("ADMIN"))).andExpect(status().isNotFound());
    }
    @Test void databaseRejectsAnInvalidStudentForeignKey() {
        Document document = new Document(); document.setStudent(students.getReferenceById(Long.MAX_VALUE));
        document.setName("invalid.pdf"); document.setPath("test/invalid.pdf"); document.setOwner("admin"); document.setContentType("application/pdf"); document.setSizeBytes(1L);
        assertThatThrownBy(() -> documents.saveAndFlush(document)).isInstanceOf(DataIntegrityViolationException.class);
        assertThat(documents.count()).isZero();
    }
    @Test void replaceIncrementsVersionAndOnlyThenRemovesOldObject() throws Exception {
        String id = upload(file("original.pdf")).get(0).get("id").asText();
        Document before = documents.findById(id).orElseThrow();
        String oldPath = before.getPath();
        JsonNode replaced = replace(id, "updated.xlsx");
        Document after = documents.findById(id).orElseThrow();
        assertThat(replaced.get("version").decimalValue()).isEqualByComparingTo(new BigDecimal("2.00"));
        assertThat(after.getModificationTime()).isAfter(before.getModificationTime());
        assertThat(after.getCreationTime()).isEqualTo(before.getCreationTime());
        assertThat(after.getOwner()).isEqualTo("lecturer@example.edu.vn");
        assertThat(after.getPath()).isNotEqualTo(oldPath);
        assertThat(objects).doesNotContainKey(oldPath).containsKey(after.getPath());
    }
    @Test void invalidReplacementPreservesOldMetadataAndBinary() throws Exception {
        String id = upload(file("original.pdf")).get(0).get("id").asText();
        Document before = documents.findById(id).orElseThrow();
        mvc.perform(multipart(HttpMethod.PUT, "/api/documents/{id}", id).file(new MockMultipartFile("file", "bad.exe", "application/pdf", new byte[]{1})).with(user("admin").roles("ADMIN")).with(csrf())).andExpect(status().isBadRequest());
        Document after = documents.findById(id).orElseThrow();
        assertThat(after.getPath()).isEqualTo(before.getPath()); assertThat(after.getVersion()).isEqualByComparingTo(before.getVersion());
        assertThat(objects).hasSize(1).containsKey(before.getPath());
    }
    @Test void longFilenamePreservesDownloadNameWithoutBreakingKeysOrAuditLogs() throws Exception {
        String name = "t".repeat(250) + ".pdf";
        String id = upload(file(name)).get(0).get("id").asText();
        replace(id, name);
        Document document = documents.findById(id).orElseThrow();
        assertThat(document.getName()).isEqualTo(name);
        assertThat(document.getPath().substring(document.getPath().lastIndexOf('/') + 1)).hasSizeLessThan(100);
        mvc.perform(delete("/api/documents/{id}", id).with(user("admin").roles("ADMIN")).with(csrf())).andExpect(status().isNoContent());
    }
    @Test void downloadReturnsBinaryAndAttachmentHeaders() throws Exception {
        MockMultipartFile uploaded = file("transcript.pdf"); String id = upload(uploaded).get(0).get("id").asText();
        mvc.perform(get("/api/documents/{id}/download", id).with(user("lecturer").roles("LECTURER"))).andExpect(status().isOk()).andExpect(content().bytes(uploaded.getBytes())).andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/pdf")).andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment"))).andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("transcript.pdf"))).andExpect(header().string(HttpHeaders.CONTENT_LENGTH, String.valueOf(uploaded.getSize())));
    }
    @Test void softDeleteRetainsRowsAndHidesDownloadAndReplace() throws Exception {
        String id = upload(file("archive.pdf")).get(0).get("id").asText();
        mvc.perform(delete("/api/documents/{id}", id).with(user("admin").roles("ADMIN")).with(csrf())).andExpect(status().isNoContent());
        Document document = documents.findById(id).orElseThrow();
        assertThat(document.getDeleted()).isTrue(); assertThat(document.getActive()).isFalse();
        assertThat(documents.count()).isEqualTo(1); assertThat(objects).containsKey(document.getPath());
        mvc.perform(get("/api/students/{id}/documents", studentId).with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andExpect(content().json("[]"));
        mvc.perform(get("/api/documents/{id}/download", id).with(user("admin").roles("ADMIN"))).andExpect(status().isNotFound());
        mvc.perform(multipart(HttpMethod.PUT, "/api/documents/{id}", id).file(new MockMultipartFile("file", "another.pdf", "application/pdf", new byte[]{1})).with(user("admin").roles("ADMIN")).with(csrf())).andExpect(status().isNotFound());
        mvc.perform(delete("/api/students/{id}", studentId).with(user("admin").roles("ADMIN")).with(csrf())).andExpect(status().isConflict());
    }
    @Test void roleAndCsrfChecksProtectMultipartAndDelete() throws Exception {
        String id = upload(file("permission.pdf")).get(0).get("id").asText();
        mvc.perform(delete("/api/documents/{id}", id).with(user("lecturer").roles("LECTURER")).with(csrf())).andExpect(status().isForbidden());
        mvc.perform(get("/api/documents/{id}/download", id).with(user("student").roles("STUDENT"))).andExpect(status().isForbidden());
        mvc.perform(multipart("/api/students/{id}/documents", studentId).file(file("csrf.pdf")).with(user("admin").roles("ADMIN"))).andExpect(status().isForbidden());
    }
    @Test void storageFailureRollsBackMetadataAndPartialUploads() throws Exception {
        doAnswer(call -> { objects.put(call.getArgument(0), new byte[]{1}); throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "File storage is temporarily unavailable."); }).when(storage).store(anyString(), any(), anyLong(), anyString());
        mvc.perform(multipart("/api/students/{id}/documents", studentId).file(file("failure.pdf")).with(user("admin").roles("ADMIN")).with(csrf())).andExpect(status().isServiceUnavailable());
        assertThat(documents.count()).isZero(); assertThat(objects).isEmpty();
    }
    @Test void databaseFailureCleansNewObjectAndPreservesPriorVersion() throws Exception {
        String id = upload(file("original.pdf")).get(0).get("id").asText();
        String oldPath = documents.findById(id).orElseThrow().getPath();
        // An oversized principal forces a genuine SQL column-length failure after upload.
        mvc.perform(multipart(HttpMethod.PUT, "/api/documents/{id}", id).file(new MockMultipartFile("file", "update.pdf", "application/pdf", new byte[]{1})).with(user("x".repeat(200)).roles("ADMIN")).with(csrf())).andExpect(status().isConflict());
        Document document = documents.findById(id).orElseThrow();
        assertThat(document.getPath()).isEqualTo(oldPath); assertThat(document.getVersion()).isEqualByComparingTo("1.00");
        assertThat(objects).hasSize(1).containsKey(oldPath);
    }
    @Test void databaseFailureDuringCreateRemovesUploadedObjects() throws Exception {
        mvc.perform(multipart("/api/students/{id}/documents", studentId).file(file("db-failure.pdf")).with(user("x".repeat(200)).roles("ADMIN")).with(csrf())).andExpect(status().isConflict());
        assertThat(documents.count()).isZero(); assertThat(objects).isEmpty();
    }
}
