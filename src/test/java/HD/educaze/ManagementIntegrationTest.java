package HD.educaze;

import com.fasterxml.jackson.databind.*;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test") @Transactional
class ManagementIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    private JsonNode create(String path, Object body) throws Exception {
        return json.readTree(mvc.perform(post(path).with(user("admin").roles("ADMIN")).with(csrf()).contentType("application/json").content(json.writeValueAsString(body))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }
    private long academicClass(int capacity) throws Exception {
        return create("/api/classes", Map.of("code", "TEST01", "name", "Integration class", "faculty", "IT", "advisor", "Test lecturer", "cohort", 2026, "capacity", capacity)).get("id").asLong();
    }
    private Map<String, Object> student(String code, long classId) {
        return Map.of("code", code, "name", "Test Student", "gender", "MALE", "dob", "2004-08-15", "email", code.toLowerCase() + "@example.edu.vn", "phone", "0901234567", "address", "Can Tho", "status", "ACTIVE", "classId", classId);
    }
    @Test void authenticationAndCsrfAreRequired() throws Exception {
        mvc.perform(get("/api/students")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/students").with(user("admin").roles("ADMIN")).contentType("application/json").content("{}")).andExpect(status().isForbidden());
    }
    @Test void loginUsesRealAccountAndPersistsSession() throws Exception {
        MvcResult tokenResult = mvc.perform(get("/api/auth/csrf")).andExpect(status().isOk()).andReturn();
        MockHttpSession session = (MockHttpSession) tokenResult.getRequest().getSession();
        JsonNode token = json.readTree(tokenResult.getResponse().getContentAsString());
        mvc.perform(post("/api/auth/login").session(session).header(token.get("headerName").asText(), token.get("token").asText()).param("username", "admin@educare.edu.vn").param("password", "admin123"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.role").value("ADMIN"));
        mvc.perform(get("/api/auth/me").session(session)).andExpect(status().isOk()).andExpect(jsonPath("$.username").value("admin@educare.edu.vn"));
        mvc.perform(post("/api/auth/login").with(csrf()).param("username", "admin@educare.edu.vn").param("password", "incorrect")).andExpect(status().isUnauthorized());
    }
    @Test void studentLifecyclePersistsAndEnforcesUniqueness() throws Exception {
        long classId = academicClass(45);
        long id = create("/api/students", student("SV0001", classId)).get("id").asLong();
        mvc.perform(get("/api/students/{id}", id).with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andExpect(jsonPath("$.code").value("SV0001"));
        mvc.perform(post("/api/students").with(user("admin").roles("ADMIN")).with(csrf()).contentType("application/json").content(json.writeValueAsString(student("SV0001", classId)))).andExpect(status().isConflict());
        Map<String, Object> update = new java.util.HashMap<>(student("SV0001", classId)); update.put("name", "Updated Student");
        mvc.perform(put("/api/students/{id}", id).with(user("admin").roles("ADMIN")).with(csrf()).contentType("application/json").content(json.writeValueAsString(update))).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Updated Student"));
        mvc.perform(get("/api/students").with(user("admin").roles("ADMIN")).param("search", "Updated").param("size", "8")).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(delete("/api/students/{id}", id).with(user("admin").roles("ADMIN")).with(csrf())).andExpect(status().isNoContent());
        mvc.perform(get("/api/students/{id}", id).with(user("admin").roles("ADMIN"))).andExpect(status().isNotFound());
    }
    @Test void classCapacityAndLinkedDeletionAreEnforced() throws Exception {
        long classId = academicClass(1);
        create("/api/students", student("SV0001", classId));
        mvc.perform(post("/api/students").with(user("admin").roles("ADMIN")).with(csrf()).contentType("application/json").content(json.writeValueAsString(student("SV0002", classId)))).andExpect(status().isConflict());
        mvc.perform(delete("/api/classes/{id}", classId).with(user("admin").roles("ADMIN")).with(csrf())).andExpect(status().isConflict());
        mvc.perform(delete("/api/classes/{id}", classId).with(user("lecturer").roles("LECTURER")).with(csrf())).andExpect(status().isForbidden());
    }
    @Test void gradesUseWeightedGpaAndRejectInvalidScores() throws Exception {
        long classId = academicClass(45);
        long studentId = create("/api/students", student("SV0001", classId)).get("id").asLong();
        long course1 = create("/api/courses", Map.of("code", "C001", "name", "Course one", "faculty", "IT", "credits", 3)).get("id").asLong();
        long course2 = create("/api/courses", Map.of("code", "C002", "name", "Course two", "faculty", "IT", "credits", 1)).get("id").asLong();
        create("/api/grades", Map.of("studentId", studentId, "subjectId", course1, "semester", "2026-1", "score", 9));
        create("/api/grades", Map.of("studentId", studentId, "subjectId", course2, "semester", "2026-1", "score", 7));
        JsonNode result = json.readTree(mvc.perform(get("/api/students/{id}", studentId).with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertThat(result.get("gpa").asDouble()).isEqualTo(3.75);
        assertThat(result.get("creditsEarned").asInt()).isEqualTo(4);
        mvc.perform(post("/api/grades").with(user("admin").roles("ADMIN")).with(csrf()).contentType("application/json").content(json.writeValueAsString(Map.of("studentId", studentId, "subjectId", course1, "semester", "2026-2", "score", 11)))).andExpect(status().isBadRequest());
        create("/api/grades", Map.of("studentId", studentId, "subjectId", course1, "semester", "2026-2", "score", 7));
        mvc.perform(get("/api/students/{id}", studentId).with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andExpect(jsonPath("$.gpa").value(3.0)).andExpect(jsonPath("$.creditsEarned").value(4));
        mvc.perform(delete("/api/courses/{id}", course1).with(user("admin").roles("ADMIN")).with(csrf())).andExpect(status().isConflict());
    }
    @Test void originalClassInfoApiRemainsAvailableAndSearchDoesNotRecurse() throws Exception {
        create("/api/classes", Map.of("code", "LEGACY", "name", "Web Development", "faculty", "IT", "advisor", "Test lecturer", "cohort", 2026, "capacity", 45));
        mvc.perform(get("/api/class-info/list").with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("Web Development"));
        mvc.perform(get("/api/class-info/search").param("query", "Web").with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andExpect(jsonPath("$[0].domain").value("IT"));
        mvc.perform(get("/api/class-info/count").param("query", "Missing").with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andExpect(content().string("0"));
        JsonNode legacyList = json.readTree(mvc.perform(get("/api/class-info/list").with(user("admin").roles("ADMIN"))).andReturn().getResponse().getContentAsString());
        mvc.perform(delete("/api/class-info/{id}", legacyList.get(0).get("id").asText()).with(user("admin").roles("ADMIN")).with(csrf())).andExpect(status().isConflict());
    }
}
