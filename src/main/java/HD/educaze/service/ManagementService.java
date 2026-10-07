package HD.educaze.service;

import HD.educaze.dto.Requests.*;
import HD.educaze.dto.Views.*;
import HD.educaze.model.*;
import HD.educaze.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service @Transactional
public class ManagementService {
    private final StudentRepository students;
    private final AcademicClassRepository classes;
    private final LecturerRepository lecturers;
    private final SubjectRepository subjects;
    private final GradeRepository grades;
    private final ActivityLogRepository activities;
    private final EntityManager em;
    private final DocumentRepository documents;
    public ManagementService(StudentRepository students, AcademicClassRepository classes, LecturerRepository lecturers,
                             SubjectRepository subjects, GradeRepository grades, ActivityLogRepository activities, EntityManager em, DocumentRepository documents) {
        this.students = students; this.classes = classes; this.lecturers = lecturers; this.subjects = subjects;
        this.grades = grades; this.activities = activities; this.em = em;
        this.documents = documents;
    }
    private ResponseStatusException missing(String entity) { return new ResponseStatusException(HttpStatus.NOT_FOUND, entity + " not found."); }
    private void conflict(String message) { throw new ResponseStatusException(HttpStatus.CONFLICT, message); }
    public void log(String type, String message) { ActivityLog a = new ActivityLog(); a.setType(type); int characters = message.codePointCount(0, message.length()); a.setMessage(characters > 255 ? message.substring(0, message.offsetByCodePoints(0, 255)) : message); activities.save(a); }
    public static double points(double score) {
        return score >= 8.5 ? 4 : score >= 8 ? 3.5 : score >= 7 ? 3 : score >= 6.5 ? 2.5 : score >= 5.5 ? 2 : score >= 5 ? 1.5 : score >= 4 ? 1 : 0;
    }
    public static String letter(double score) { return score >= 8.5 ? "A" : score >= 8 ? "B+" : score >= 7 ? "B" : score >= 6.5 ? "C+" : score >= 5.5 ? "C" : score >= 5 ? "D+" : score >= 4 ? "D" : "F"; }
    public GradeView gradeView(Grade g) {
        Subject c = g.getSubject();
        return new GradeView(g.getId(), g.getStudent().getId(), g.getStudent().getName(), c.getId(), c.getName(), c.getCode(), c.getCredits(), c.getLecturer() == null ? null : c.getLecturer().getName(), g.getSemester(), g.getScore(), letter(g.getScore().doubleValue()), points(g.getScore().doubleValue()));
    }
    public StudentView studentView(Student s) {
        List<GradeView> courseGrades = grades.findByStudentId(s.getId()).stream().map(this::gradeView).toList();
        // Retakes count once: use the most recent record per course for cumulative GPA.
        Map<Long, GradeView> latest = new HashMap<>();
        courseGrades.stream().sorted(Comparator.comparing(GradeView::semester).thenComparing(GradeView::id)).forEach(g -> latest.put(g.courseId(), g));
        int totalCredits = latest.values().stream().mapToInt(GradeView::credits).sum();
        Double gpa = totalCredits == 0 ? null : Math.round(latest.values().stream().mapToDouble(g -> g.points() * g.credits()).sum() / totalCredits * 100.0) / 100.0;
        int earned = latest.values().stream().filter(g -> g.score().doubleValue() >= 4).mapToInt(GradeView::credits).sum();
        AcademicClass c = s.getAcademicClass();
        return new StudentView(s.getId(), s.getCode(), s.getName(), s.getGender(), s.getDob(), s.getEmail(), s.getPhone(), s.getAddress(), s.getStatus(), c.getId(), c.getCode(), c.getName(), c.getFaculty(), c.getCohort(), gpa, earned, courseGrades);
    }
    @Transactional(readOnly = true)
    public PageView<StudentView> students(String search, String faculty, Long classId, String status, int page, int size, String sort, String direction) {
        String field = Set.of("name", "code", "createdAt", "email").contains(sort) ? sort : "createdAt";
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, Math.min(size, 100)), Sort.by("ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC, field).and(Sort.by("id")));
        Page<Student> result = students.findAll((root, query, cb) -> {
            List<Predicate> filters = new ArrayList<>();
            if (search != null && !search.isBlank()) {
                String term = "%" + search.strip().toLowerCase(Locale.ROOT) + "%";
                filters.add(cb.or(cb.like(cb.lower(root.get("name")), term), cb.like(cb.lower(root.get("code")), term), cb.like(cb.lower(root.get("email")), term)));
            }
            if (faculty != null && !faculty.isBlank()) filters.add(cb.equal(root.get("academicClass").get("faculty"), faculty));
            if (classId != null) filters.add(cb.equal(root.get("academicClass").get("id"), classId));
            if (status != null && !status.isBlank()) filters.add(cb.equal(root.get("status"), status));
            return cb.and(filters.toArray(Predicate[]::new));
        }, pageable);
        return new PageView<>(result.getContent().stream().map(this::studentView).toList(), result.getTotalElements(), result.getTotalPages(), result.getNumber(), result.getSize());
    }
    @Transactional(readOnly = true) public StudentView student(Long id) { return studentView(students.findById(id).orElseThrow(() -> missing("Student"))); }
    public StudentView saveStudent(Long id, StudentInput input) {
        Student s = id == null ? new Student() : students.findById(id).orElseThrow(() -> missing("Student"));
        String code = input.code().strip().toUpperCase(Locale.ROOT);
        String email = input.email().strip().toLowerCase(Locale.ROOT);
        if ((id == null || !code.equalsIgnoreCase(s.getCode())) && students.existsByCodeIgnoreCase(code)) conflict("Student ID already exists.");
        if ((id == null || !email.equalsIgnoreCase(s.getEmail())) && students.existsByEmailIgnoreCase(email)) conflict("Student email already exists.");
        AcademicClass c = classes.findById(input.classId()).orElseThrow(() -> missing("Class"));
        if ((id == null || !s.getAcademicClass().getId().equals(c.getId())) && students.countByAcademicClassId(c.getId()) >= c.getCapacity()) conflict("This class has reached its capacity.");
        s.setCode(code); s.setName(input.name().strip()); s.setGender(input.gender()); s.setDob(input.dob()); s.setEmail(email);
        s.setPhone(input.phone().strip()); s.setAddress(input.address()); s.setStatus(input.status()); s.setAcademicClass(c);
        students.saveAndFlush(s); log("STUDENT", (id == null ? "Added student " : "Updated student ") + s.getName()); return studentView(s);
    }
    public void deleteStudent(Long id) {
        Student s = students.findById(id).orElseThrow(() -> missing("Student"));
        if (documents.existsByStudentId(id)) conflict("This student has document history and cannot be deleted.");
        grades.deleteByStudentId(id); grades.flush(); students.delete(s); log("STUDENT", "Deleted student " + s.getName());
    }
    private List<StudentView> roster(Long id) { return students.findAll((root, query, cb) -> cb.equal(root.get("academicClass").get("id"), id)).stream().map(this::studentView).toList(); }
    public ClassView classView(AcademicClass c) {
        List<StudentView> roster = roster(c.getId());
        Double average = roster.stream().filter(s -> s.gpa() != null).mapToDouble(StudentView::gpa).average().stream().boxed().findFirst().orElse(null);
        if (average != null) average = Math.round(average * 100.0) / 100.0;
        return new ClassView(c.getId(), c.getClassInfoId(), c.getCode(), c.getName(), c.getFaculty(), c.getAdvisor(), c.getCohort(), c.getCapacity(), roster.size(), average, c.getLecturer() == null ? null : c.getLecturer().getId(), c.getSubject() == null ? null : c.getSubject().getId(), c.getSubject() == null ? null : c.getSubject().getCredits());
    }
    @Transactional(readOnly = true) public List<ClassView> classes() { return classes.findAll(Sort.by(Sort.Direction.DESC, "id")).stream().map(this::classView).toList(); }
    @Transactional(readOnly = true) public Map<String, Object> academicClass(Long id) { return Map.of("info", classView(classes.findById(id).orElseThrow(() -> missing("Class"))), "students", roster(id)); }
    public ClassView saveClass(Long id, ClassInput input) {
        AcademicClass c = id == null ? new AcademicClass() : classes.findById(id).orElseThrow(() -> missing("Class"));
        String code = input.code().strip().toUpperCase(Locale.ROOT);
        if ((id == null || !code.equalsIgnoreCase(c.getCode())) && classes.existsByCodeIgnoreCase(code)) conflict("Class code already exists.");
        if (id != null && students.countByAcademicClassId(id) > input.capacity()) conflict("Capacity cannot be below the current student count.");
        Lecturer lecturer = input.lecturerId() == null ? null : lecturers.findById(input.lecturerId()).orElseThrow(() -> missing("Lecturer"));
        Subject subject = input.courseId() == null ? null : subjects.findById(input.courseId()).orElseThrow(() -> missing("Course"));
        c.setCode(code); c.setName(input.name().strip()); c.setFaculty(input.faculty().strip()); c.setAdvisor(lecturer == null ? input.advisor().strip() : lecturer.getName());
        c.setCohort(input.cohort()); c.setCapacity(input.capacity()); c.setLecturer(lecturer); c.setSubject(subject);
        ClassInfo legacy = c.getClassInfoId() == null ? new ClassInfo() : em.find(ClassInfo.class, c.getClassInfoId());
        if (legacy == null) legacy = new ClassInfo();
        legacy.setName(c.getName()); legacy.setDomain(c.getFaculty()); legacy.setTargetOperator(c.getAdvisor());
        if (legacy.getId() == null) em.persist(legacy);
        c.setClassInfoId(legacy.getId()); classes.saveAndFlush(c);
        log("CLASS", (id == null ? "Created class " : "Updated class ") + c.getName()); return classView(c);
    }
    public void deleteClass(Long id) {
        AcademicClass c = classes.findById(id).orElseThrow(() -> missing("Class"));
        if (students.countByAcademicClassId(id) > 0) conflict("Move or remove students before deleting this class.");
        classes.delete(c); classes.flush();
        if (c.getClassInfoId() != null) { ClassInfo legacy = em.find(ClassInfo.class, c.getClassInfoId()); if (legacy != null) em.remove(legacy); }
        log("CLASS", "Deleted class " + c.getName());
    }
    public CourseView courseView(Subject c) { return new CourseView(c.getId(), c.getCode(), c.getName(), c.getCredits(), c.getFaculty(), c.getLecturer() == null ? null : c.getLecturer().getId(), c.getLecturer() == null ? null : c.getLecturer().getName()); }
    @Transactional(readOnly = true) public List<CourseView> courses() { return subjects.findAll(Sort.by("code")).stream().map(this::courseView).toList(); }
    public CourseView saveCourse(Long id, SubjectInput input) {
        Subject c = id == null ? new Subject() : subjects.findById(id).orElseThrow(() -> missing("Course"));
        String code = input.code().strip().toUpperCase(Locale.ROOT);
        if ((id == null || !code.equalsIgnoreCase(c.getCode())) && subjects.existsByCodeIgnoreCase(code)) conflict("Course code already exists.");
        c.setCode(code); c.setName(input.name().strip()); c.setFaculty(input.faculty().strip()); c.setCredits(input.credits());
        c.setLecturer(input.lecturerId() == null ? null : lecturers.findById(input.lecturerId()).orElseThrow(() -> missing("Lecturer")));
        subjects.saveAndFlush(c); log("COURSE", (id == null ? "Created course " : "Updated course ") + c.getName()); return courseView(c);
    }
    public void deleteCourse(Long id) {
        Subject c = subjects.findById(id).orElseThrow(() -> missing("Course"));
        if (grades.existsBySubjectId(id) || classes.findAll().stream().anyMatch(k -> k.getSubject() != null && k.getSubject().getId().equals(id))) conflict("This course is linked to classes or grades and cannot be deleted.");
        subjects.delete(c); log("COURSE", "Deleted course " + c.getName());
    }
    public LecturerView lecturerView(Lecturer l) { return new LecturerView(l.getId(), l.getName(), l.getEmail(), l.getPhone(), l.getFaculty(), l.getSpecialty(), l.getStatus(), classes.findAll().stream().filter(c -> c.getLecturer() != null && c.getLecturer().getId().equals(l.getId())).count()); }
    @Transactional(readOnly = true) public List<LecturerView> lecturers() { return lecturers.findAll(Sort.by("name")).stream().map(this::lecturerView).toList(); }
    public LecturerView saveLecturer(Long id, LecturerInput input) {
        Lecturer l = id == null ? new Lecturer() : lecturers.findById(id).orElseThrow(() -> missing("Lecturer"));
        String email = input.email().strip().toLowerCase(Locale.ROOT);
        if ((id == null || !email.equalsIgnoreCase(l.getEmail())) && lecturers.existsByEmailIgnoreCase(email)) conflict("Lecturer email already exists.");
        l.setName(input.name().strip()); l.setEmail(email); l.setPhone(input.phone().strip()); l.setFaculty(input.faculty().strip()); l.setSpecialty(input.specialty().strip()); l.setStatus(input.status());
        lecturers.saveAndFlush(l);
        if (id != null) classes.findAll().stream().filter(c -> c.getLecturer() != null && c.getLecturer().getId().equals(id)).forEach(c -> {
            c.setAdvisor(l.getName());
            ClassInfo legacy = c.getClassInfoId() == null ? null : em.find(ClassInfo.class, c.getClassInfoId());
            if (legacy != null) legacy.setTargetOperator(l.getName());
        });
        log("LECTURER", (id == null ? "Added lecturer " : "Updated lecturer ") + l.getName()); return lecturerView(l);
    }
    public void deleteLecturer(Long id) {
        Lecturer l = lecturers.findById(id).orElseThrow(() -> missing("Lecturer"));
        boolean linked = classes.findAll().stream().anyMatch(c -> c.getLecturer() != null && c.getLecturer().getId().equals(id)) || subjects.findAll().stream().anyMatch(c -> c.getLecturer() != null && c.getLecturer().getId().equals(id));
        if (linked) conflict("Reassign this lecturer's classes and courses before deleting.");
        lecturers.delete(l); log("LECTURER", "Deleted lecturer " + l.getName());
    }
    @Transactional(readOnly = true) public List<GradeView> grades() { return grades.findAllByOrderByIdDesc().stream().map(this::gradeView).toList(); }
    public GradeView saveGrade(Long id, GradeInput input) {
        Grade g = id == null ? new Grade() : grades.findById(id).orElseThrow(() -> missing("Grade"));
        String semester = input.semester().strip();
        boolean changedKey = id == null || !g.getStudent().getId().equals(input.studentId()) || !g.getSubject().getId().equals(input.subjectId()) || !g.getSemester().equals(semester);
        if (changedKey && grades.existsByStudentIdAndSubjectIdAndSemester(input.studentId(), input.subjectId(), semester)) conflict("A grade for this student, course and semester already exists.");
        g.setStudent(students.findById(input.studentId()).orElseThrow(() -> missing("Student"))); g.setSubject(subjects.findById(input.subjectId()).orElseThrow(() -> missing("Course")));
        g.setSemester(semester); g.setScore(input.score()); grades.saveAndFlush(g); log("GRADE", "Updated grade for " + g.getStudent().getName()); return gradeView(g);
    }
    public void deleteGrade(Long id) { Grade g = grades.findById(id).orElseThrow(() -> missing("Grade")); grades.delete(g); log("GRADE", "Removed grade for " + g.getStudent().getName()); }
    @Transactional(readOnly = true) public Dashboard dashboard() {
        Map<String, Long> facultyCounts = students.findAll().stream().collect(Collectors.groupingBy(s -> s.getAcademicClass().getFaculty(), TreeMap::new, Collectors.counting()));
        List<StudentView> recent = students.findAll(PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "createdAt", "id"))).getContent().stream().map(this::studentView).toList();
        return new Dashboard(students.count(), classes.count(), lecturers.count(), subjects.count(), facultyCounts.entrySet().stream().map(e -> new FacultyCount(e.getKey(), e.getValue())).toList(), recent, classes().stream().limit(3).toList(), activities.findTop8ByOrderByCreatedAtDescIdDesc());
    }
}
