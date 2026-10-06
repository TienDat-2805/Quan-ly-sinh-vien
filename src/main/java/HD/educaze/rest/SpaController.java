package HD.educaze.rest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class SpaController {
    @GetMapping({"/", "/login", "/dashboard", "/students", "/students/{id}", "/classes", "/classes/{id}", "/lecturers", "/courses", "/settings"})
    public String app() { return "forward:/index.html"; }
}
