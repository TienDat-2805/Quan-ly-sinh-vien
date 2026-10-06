package HD.educaze.rest;
import HD.educaze.dto.Requests.*;
import HD.educaze.dto.Views.*;
import HD.educaze.service.ManagementService;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api") @PreAuthorize("hasAnyRole('ADMIN','LECTURER')")
public class ManagementController {
    private final ManagementService service;
    public ManagementController(ManagementService service) { this.service = service; }
    @GetMapping("/dashboard") public Dashboard dashboard() { return service.dashboard(); }
    @GetMapping("/students") public PageView<StudentView> students(@RequestParam(required = false) String search, @RequestParam(required = false) String faculty, @RequestParam(required = false) Long classId, @RequestParam(required = false) String status, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size, @RequestParam(defaultValue = "createdAt") String sort, @RequestParam(defaultValue = "DESC") String direction) { return service.students(search, faculty, classId, status, page, size, sort, direction); }
    @GetMapping("/students/{id}") public StudentView student(@PathVariable Long id) { return service.student(id); }
    @PostMapping("/students") @ResponseStatus(HttpStatus.CREATED) public StudentView createStudent(@Valid @RequestBody StudentInput input) { return service.saveStudent(null, input); }
    @PutMapping("/students/{id}") public StudentView updateStudent(@PathVariable Long id, @Valid @RequestBody StudentInput input) { return service.saveStudent(id, input); }
    @DeleteMapping("/students/{id}") @PreAuthorize("hasRole('ADMIN')") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteStudent(@PathVariable Long id) { service.deleteStudent(id); }
    @GetMapping("/classes") public List<ClassView> classes() { return service.classes(); }
    @GetMapping("/classes/{id}") public Map<String, Object> academicClass(@PathVariable Long id) { return service.academicClass(id); }
    @PostMapping("/classes") @ResponseStatus(HttpStatus.CREATED) public ClassView createClass(@Valid @RequestBody ClassInput input) { return service.saveClass(null, input); }
    @PutMapping("/classes/{id}") public ClassView updateClass(@PathVariable Long id, @Valid @RequestBody ClassInput input) { return service.saveClass(id, input); }
    @DeleteMapping("/classes/{id}") @PreAuthorize("hasRole('ADMIN')") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteClass(@PathVariable Long id) { service.deleteClass(id); }
    @GetMapping("/courses") public List<CourseView> courses() { return service.courses(); }
    @PostMapping("/courses") @ResponseStatus(HttpStatus.CREATED) public CourseView createCourse(@Valid @RequestBody SubjectInput input) { return service.saveCourse(null, input); }
    @PutMapping("/courses/{id}") public CourseView updateCourse(@PathVariable Long id, @Valid @RequestBody SubjectInput input) { return service.saveCourse(id, input); }
    @DeleteMapping("/courses/{id}") @PreAuthorize("hasRole('ADMIN')") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteCourse(@PathVariable Long id) { service.deleteCourse(id); }
    @GetMapping("/lecturers") public List<LecturerView> lecturers() { return service.lecturers(); }
    @PostMapping("/lecturers") @PreAuthorize("hasRole('ADMIN')") @ResponseStatus(HttpStatus.CREATED) public LecturerView createLecturer(@Valid @RequestBody LecturerInput input) { return service.saveLecturer(null, input); }
    @PutMapping("/lecturers/{id}") @PreAuthorize("hasRole('ADMIN')") public LecturerView updateLecturer(@PathVariable Long id, @Valid @RequestBody LecturerInput input) { return service.saveLecturer(id, input); }
    @DeleteMapping("/lecturers/{id}") @PreAuthorize("hasRole('ADMIN')") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteLecturer(@PathVariable Long id) { service.deleteLecturer(id); }
    @GetMapping("/grades") public List<GradeView> grades() { return service.grades(); }
    @PostMapping("/grades") @ResponseStatus(HttpStatus.CREATED) public GradeView createGrade(@Valid @RequestBody GradeInput input) { return service.saveGrade(null, input); }
    @PutMapping("/grades/{id}") public GradeView updateGrade(@PathVariable Long id, @Valid @RequestBody GradeInput input) { return service.saveGrade(id, input); }
    @DeleteMapping("/grades/{id}") @PreAuthorize("hasRole('ADMIN')") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteGrade(@PathVariable Long id) { service.deleteGrade(id); }
}
