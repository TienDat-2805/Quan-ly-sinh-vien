package HD.educaze.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class Requests {
    private Requests() {}
    public record StudentInput(
        @NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{3,30}") String code,
        @NotBlank @Size(max = 100) String name,
        @NotNull @Pattern(regexp = "MALE|FEMALE|OTHER") String gender,
        @NotNull @Past LocalDate dob,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Pattern(regexp = "[+0-9 ()-]{8,20}") String phone,
        @Size(max = 255) String address,
        @NotNull @Pattern(regexp = "ACTIVE|PENDING|INACTIVE") String status,
        @NotNull Long classId) {}
    public record ClassInput(
        @NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{2,30}") String code,
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 120) String faculty,
        @NotBlank @Size(max = 100) String advisor,
        @NotNull @Min(2000) @Max(2100) Integer cohort,
        @NotNull @Min(1) @Max(500) Integer capacity,
        Long lecturerId, Long courseId) {}
    public record SubjectInput(
        @NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{2,30}") String code,
        @NotBlank @Size(max = 120) String name,
        @NotNull @Min(1) @Max(10) Integer credits,
        @NotBlank @Size(max = 120) String faculty, Long lecturerId) {}
    public record LecturerInput(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Pattern(regexp = "[+0-9 ()-]{8,20}") String phone,
        @NotBlank @Size(max = 120) String faculty,
        @NotBlank @Size(max = 120) String specialty,
        @NotNull @Pattern(regexp = "ACTIVE|PENDING|INACTIVE") String status) {}
    public record GradeInput(
        @NotNull Long studentId, @NotNull Long subjectId,
        @NotBlank @Pattern(regexp = "[0-9]{4}-[12]") String semester,
        @NotNull @DecimalMin("0.00") @DecimalMax("10.00") @Digits(integer = 2, fraction = 2) BigDecimal score) {}
}
