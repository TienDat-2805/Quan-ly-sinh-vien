package HD.educaze.dto;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;

public final class Views {
    private Views() {}
    public record GradeView(Long id, Long studentId, String studentName, Long courseId, String courseName, String courseCode, int credits, String lecturer, String semester, BigDecimal score, String letter, double points) {}
    public record StudentView(Long id, String code, String name, String gender, LocalDate dob, String email, String phone, String address, String status, Long classId, String classCode, String className, String faculty, int enrollmentYear, Double gpa, int creditsEarned, List<GradeView> courses) {}
    public record ClassView(Long id, String classInfoId, String code, String name, String faculty, String advisor, int cohort, int capacity, long studentCount, Double averageGpa, Long lecturerId, Long courseId, Integer credits) {}
    public record CourseView(Long id, String code, String name, int credits, String faculty, Long lecturerId, String lecturer) {}
    public record LecturerView(Long id, String name, String email, String phone, String faculty, String specialty, String status, long classCount) {}
    public record FacultyCount(String name, long count) {}
    public record Dashboard(long students, long classes, long lecturers, long courses, List<FacultyCount> faculties, List<StudentView> recentStudents, List<ClassView> recentClasses, List<HD.educaze.model.ActivityLog> activities) {}
    public record PageView<T>(List<T> content, long totalElements, int totalPages, int page, int size) {}
    public record UserView(String username, String displayName, String role) {}
}
