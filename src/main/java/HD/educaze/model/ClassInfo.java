package HD.educaze.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity @Table(name = "class_info") @Getter @Setter
public class ClassInfo {
    @Id @Column(length = 36) private String id;
    private String appId;
    @Column(nullable = false) private String name;
    private String softwareType;
    private String domain;
    private String targetOperator;
    private Double review;
    @Column(nullable = false) private int reviewCount;
    @Column(nullable = false) private int installationCount;
    @Column(length = 2048) private String iconPath;
    private String templateDetailId;
    @Column(nullable = false) private LocalDateTime createdTime;
    @PrePersist void initialize() {
        if (id == null) id = java.util.UUID.randomUUID().toString();
        if (createdTime == null) createdTime = LocalDateTime.now();
    }
}
