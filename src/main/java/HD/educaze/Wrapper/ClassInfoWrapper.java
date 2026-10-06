package HD.educaze.Wrapper;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ClassInfoWrapper {
    private String id;
    @Size(max = 255) private String appId;
    @NotBlank @Size(max = 255) private String name;
    @Size(max = 255) private String softwareType;
    @Size(max = 255) private String domain;
    @Size(max = 255) private String targetOperator;
    private Double review;
    private int reviewCount;
    private int installationCount;
    @Size(max = 2048) private String iconPath;
    @Size(max = 255) private String templateDetailId;
}
