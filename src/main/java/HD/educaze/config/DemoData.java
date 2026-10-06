package HD.educaze.config;

import HD.educaze.dto.Requests.*;
import HD.educaze.dto.Views.*;
import HD.educaze.model.Account;
import HD.educaze.repository.*;
import HD.educaze.service.ManagementService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DemoData implements ApplicationRunner {
    private final AccountRepository accounts;
    private final AcademicClassRepository classes;
    private final ActivityLogRepository activities;
    private final ManagementService service;
    private final PasswordEncoder encoder;
    @Value("${app.demo.enabled}") private boolean enabled;
    @Value("${app.admin.username}") private String adminUsername;
    @Value("${app.admin.password}") private String adminPassword;
    public DemoData(AccountRepository accounts, AcademicClassRepository classes, ActivityLogRepository activities, ManagementService service, PasswordEncoder encoder) {
        this.accounts = accounts; this.classes = classes; this.activities = activities; this.service = service; this.encoder = encoder;
    }
    @Override @Transactional public void run(ApplicationArguments args) {
        String username = adminUsername.strip().toLowerCase(Locale.ROOT);
        if (accounts.findByUsername(username).isEmpty()) {
            Account admin = new Account(); admin.setUsername(username); admin.setPassword(encoder.encode(adminPassword));
            admin.setDisplayName("Nguyen Tien Dat"); admin.setRole("ADMIN"); accounts.save(admin);
        }
        if (!enabled || classes.count() > 0 || activities.count() > 0) return;
        String[] names = {"Nguyen Van A", "Tran Van B", "Le Van C", "Pham Thi D", "Le Thu Trang", "Vo Minh Duc"};
        String[] courseNames = {"Web Development", "Cloud Computing", "Computer Networks", "Database Systems", "UI/UX Fundamentals", "Business Analytics", "Information Security", "Software Engineering"};
        String[] codes = {"CSE3032", "CSE3051", "CSE3024", "CSE3018", "DES2011", "BUS3040", "CSE3044", "CSE3010"};
        String[] faculties = {"Information Technology", "Information Technology", "Information Technology", "Information Technology", "Design", "Business"};
        List<LecturerView> lecturers = new ArrayList<>();
        for (int i = 0; i < names.length; i++) lecturers.add(service.saveLecturer(null, new LecturerInput(names[i], "lecturer" + (i + 1) + "@example.edu.vn", "090100000" + i, faculties[i], courseNames[i], "ACTIVE")));
        List<CourseView> courses = new ArrayList<>();
        for (int i = 0; i < courseNames.length; i++) {
            int l = i < 6 ? i : i - 6;
            courses.add(service.saveCourse(null, new SubjectInput(codes[i], courseNames[i], i == 4 ? 2 : 3, faculties[l], lecturers.get(l).id())));
        }
        String[] classCodes = {"22DTH1", "22DTH2", "22DTH3", "22DTH4", "22TK1", "22QTKD1"};
        List<ClassView> classViews = new ArrayList<>();
        for (int i = 0; i < classCodes.length; i++) classViews.add(service.saveClass(null, new ClassInput(classCodes[i], courseNames[i], faculties[i], names[i], 2022, 45, lecturers.get(i).id(), courses.get(i).id())));
        String[] studentNames = {"Nguyen Minh Anh", "Tran Gia Bao", "Le Thu Ha", "Pham Duc Long", "Vo Ngoc Linh", "Do Hoang Nam", "Bui Khanh Vy", "Nguyen Quoc Huy", "Tran Hoai An", "Le Minh Khang", "Phan Bao Ngoc", "Hoang Thanh Tung", "Dang Thao Nhi", "Vu Anh Tuan", "Truong Mai Chi", "Ngo Duc Hieu"};
        String[] studentCodes = {"22110081", "22110124", "22110312", "22110403", "22110555", "22110610", "22110721", "22110833", "22110901", "22110902", "22110903", "22110904", "22110905", "22110906", "22110907", "22110908"};
        int[] classIndexes = {0, 1, 4, 0, 2, 5, 4, 1, 2, 3, 5, 3, 0, 1, 4, 5};
        for (int i = 0; i < studentNames.length; i++) {
            String status = i == 2 || i == 7 ? "PENDING" : i == 4 ? "INACTIVE" : "ACTIVE";
            StudentView s = service.saveStudent(null, new StudentInput(studentCodes[i], studentNames[i], i % 3 == 0 ? "FEMALE" : "MALE", LocalDate.of(2004, 1 + i % 12, 1 + i % 27), studentCodes[i] + "@student.example.edu.vn", "09012345" + String.format("%02d", i), "Ninh Kieu, Can Tho", status, classViews.get(classIndexes[i]).id()));
            for (int j = 0; j < 4; j++) {
                CourseView c = courses.get((classIndexes[i] + j) % courses.size());
                double score = 5.5 + ((i * 3 + j * 7) % 40) / 10.0;
                service.saveGrade(null, new GradeInput(s.id(), c.id(), "2026-1", BigDecimal.valueOf(score)));
            }
        }
        service.log("SYSTEM", "Sample university records initialized.");
    }
}
