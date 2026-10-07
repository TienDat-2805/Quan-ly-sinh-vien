package HD.educaze.service;

import HD.educaze.dto.Views.DocumentView;
import HD.educaze.model.Document;
import HD.educaze.model.Student;
import HD.educaze.repository.DocumentRepository;
import HD.educaze.repository.StudentRepository;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.*;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service @Transactional
public class DocumentService {
    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("PDF", "XLSX", "XLS", "DOC", "DOCX", "JPG", "JPEG");
    private static final Map<String, String> TYPES = Map.of(
        "PDF", "application/pdf", "XLS", "application/vnd.ms-excel",
        "XLSX", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "DOC", "application/msword", "DOCX", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "JPG", "image/jpeg", "JPEG", "image/jpeg");
    private final DocumentRepository documents;
    private final StudentRepository students;
    private final DocumentStorageService storage;
    private final ManagementService management;
    private final long maxFileBytes;
    private final long maxRequestBytes;
    public DocumentService(DocumentRepository documents, StudentRepository students, DocumentStorageService storage,
            ManagementService management, @Value("${spring.servlet.multipart.max-file-size:20MB}") String maxFile,
            @Value("${spring.servlet.multipart.max-request-size:50MB}") String maxRequest) {
        this.documents = documents; this.students = students; this.storage = storage; this.management = management;
        maxFileBytes = DataSize.parse(maxFile).toBytes(); maxRequestBytes = DataSize.parse(maxRequest).toBytes();
    }
    private ResponseStatusException invalid(String message) { return new ResponseStatusException(HttpStatus.BAD_REQUEST, message); }
    private record ValidatedFile(MultipartFile file, String name, String type) {}
    private ValidatedFile validateFileExtension(MultipartFile file) {
        if (file == null || file.isEmpty()) throw invalid("The uploaded file is empty.");
        if (file.getSize() > maxFileBytes) throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "The file exceeds the upload size limit.");
        String original = Objects.requireNonNullElse(file.getOriginalFilename(), "").replace('\\', '/');
        String name = original.substring(original.lastIndexOf('/') + 1).strip();
        if (name.isBlank() || name.length() > 255 || name.codePoints().anyMatch(Character::isISOControl)) throw invalid("Invalid filename.");
        int dot = name.lastIndexOf('.');
        String extension = dot < 1 ? "" : name.substring(dot + 1).toUpperCase(Locale.ROOT);
        log.info("Document extension from multipart: {}", extension);
        if (!ALLOWED_EXTENSIONS.contains(extension)) throw invalid("Unsupported file type. Allowed: PDF, XLSX, XLS, DOC, DOCX, JPG, JPEG.");
        return new ValidatedFile(file, name, TYPES.get(extension));
    }
    private String objectKey(Long studentId, String documentId, String name) {
        String extension = name.substring(name.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        // Keep key segments short on both Windows and Linux; retain the original
        // filename in SQL and Content-Disposition, not in the filesystem key.
        return "students/" + studentId + "/" + documentId + "/" + UUID.randomUUID() + "." + extension;
    }
    private void store(String key, ValidatedFile file) {
        try (InputStream input = file.file().getInputStream()) {
            storage.store(key, input, file.file().getSize(), file.type());
        } catch (IOException error) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to read uploaded file.", error); }
    }
    private void cleanup(String path) {
        try { storage.remove(path); }
        catch (RuntimeException error) { log.warn("Document object cleanup must be retried: {} ({})", path, error.getClass().getSimpleName()); }
    }
    private void coordinateStorage(List<String> newPaths, String oldPath) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) newPaths.forEach(DocumentService.this::cleanup);
                else if (oldPath != null) cleanup(oldPath);
            }
        });
    }
    private DocumentView view(Document d) {
        return new DocumentView(d.getId(), d.getStudent().getId(), d.getName(), d.getOwner(), d.getVersion(),
            d.getCreationTime(), d.getModificationTime(), d.getActive(), d.getContentType(), d.getSizeBytes());
    }
    @Transactional(readOnly = true) public List<DocumentView> list(Long studentId) {
        if (!students.existsById(studentId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
        return documents.findByStudentIdAndDeletedFalseAndActiveTrueOrderByModificationTimeDescIdAsc(studentId).stream().map(this::view).toList();
    }
    public List<DocumentView> create(List<MultipartFile> files, Long appId, String owner) {
        Student student = students.findById(appId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found."));
        if (files == null || files.isEmpty() || files.size() > 20) throw invalid("Choose between 1 and 20 files.");
        List<ValidatedFile> validated = files.stream().map(this::validateFileExtension).toList();
        long total = validated.stream().mapToLong(f -> f.file().getSize()).sum();
        if (total > maxRequestBytes) throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "The upload exceeds the request size limit.");
        List<String> paths = new ArrayList<>();
        coordinateStorage(paths, null);
        List<Document> created = new ArrayList<>();
        for (ValidatedFile file : validated) {
            Document document = new Document(); document.setId(UUID.randomUUID().toString()); document.setStudent(student);
            String path = objectKey(appId, document.getId(), file.name());
            paths.add(path); // Include uncertain partial writes in rollback cleanup too.
            store(path, file);
            document.setName(file.name()); document.setPath(path); document.setOwner(owner);
            document.setContentType(file.type()); document.setSizeBytes(file.file().getSize());
            created.add(documents.save(document));
            log.info("Upload document student={} document={} name={} version=1.0", appId, document.getId(), file.name());
        }
        documents.flush(); management.log("DOCUMENT", "Uploaded " + created.size() + " document(s) for " + student.getCode());
        return created.stream().map(this::view).toList();
    }
    public Document fetchAndValidateDocumentById(Map<String, String> id) {
        String documentId = id == null ? null : id.get("ID");
        if (documentId == null || documentId.isBlank()) throw invalid("Invalid document ID.");
        return documents.findLiveForUpdate(documentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found."));
    }
    public DocumentView replace(String id, MultipartFile file, String owner) {
        Document document = fetchAndValidateDocumentById(Map.of("ID", id));
        ValidatedFile validated = validateFileExtension(file);
        String newPath = objectKey(document.getStudent().getId(), id, validated.name());
        coordinateStorage(List.of(newPath), document.getPath());
        store(newPath, validated);
        document.setName(validated.name()); document.setPath(newPath); document.setOwner(owner);
        document.setVersion(document.getVersion().add(BigDecimal.ONE));
        document.setContentType(validated.type()); document.setSizeBytes(file.getSize());
        documents.saveAndFlush(document);
        management.log("DOCUMENT", "Replaced document " + document.getName());
        log.info("Replace document student={} document={} name={} version={}", document.getStudent().getId(), id, document.getName(), document.getVersion());
        return view(document);
    }
    public ResponseEntity<byte[]> download(String id) {
        Document document = fetchAndValidateDocumentById(Map.of("ID", id));
        byte[] content = storage.download(document.getPath());
        log.info("Download document student={} document={} version={}", document.getStudent().getId(), id, document.getVersion());
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(document.getContentType())).contentLength(content.length)
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(document.getName(), StandardCharsets.UTF_8).build().toString())
            .header("X-Content-Type-Options", "nosniff").body(content);
    }
    public void delete(Map<String, String> id) {
        Document document = fetchAndValidateDocumentById(id);
        document.setDeleted(true); document.setActive(false); documents.saveAndFlush(document);
        management.log("DOCUMENT", "Deleted document " + document.getName());
        log.info("Soft delete document student={} document={} name={}", document.getStudent().getId(), document.getId(), document.getName());
    }
}
