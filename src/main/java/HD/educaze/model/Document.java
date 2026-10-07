package HD.educaze.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity @Table(name = "documents") @Getter @Setter
public class Document {
    @Id @Column(name = "ID", nullable = false, updatable = false, length = 36)
    private String id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "APP_DETAIL_FK", nullable = false)
    private Student student;
    @Column(name = "NAME", nullable = false, length = 255) private String name;
    @Column(name = "PATH", nullable = false, length = 1024) private String path;
    @Column(name = "OWNER", nullable = false, length = 150) private String owner;
    @Column(name = "VERSION", nullable = false, precision = 10, scale = 2) private BigDecimal version;
    @Column(name = "DELETED", nullable = false) private Boolean deleted;
    @Column(name = "ACTIVE", nullable = false) private Boolean active;
    @Column(name = "CREATED_TIME", nullable = false) private LocalDateTime creationTime;
    @Column(name = "MODIFIED_TIME", nullable = false) private LocalDateTime modificationTime;
    @Column(name = "CONTENT_TYPE", nullable = false, length = 120) private String contentType;
    @Column(name = "SIZE_BYTES", nullable = false) private Long sizeBytes;

    @PrePersist void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        creationTime = modificationTime = LocalDateTime.now();
        if (version == null) version = new BigDecimal("1.00");
        deleted = false; active = true;
    }
    @PreUpdate void onUpdate() { modificationTime = LocalDateTime.now(); }
}
