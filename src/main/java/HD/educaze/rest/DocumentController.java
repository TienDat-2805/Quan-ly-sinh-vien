package HD.educaze.rest;

import HD.educaze.dto.Views.DocumentView;
import HD.educaze.service.DocumentService;
import java.security.Principal;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController @RequestMapping("/api") @PreAuthorize("hasAnyRole('ADMIN','LECTURER')")
public class DocumentController {
    private final DocumentService service;
    public DocumentController(DocumentService service) { this.service = service; }
    @GetMapping("/students/{studentId}/documents") public List<DocumentView> list(@PathVariable Long studentId) { return service.list(studentId); }
    @PostMapping(value = "/students/{studentId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public List<DocumentView> create(@PathVariable Long studentId, @RequestPart("files") List<MultipartFile> files, Principal principal) { return service.create(files, studentId, principal.getName()); }
    @PutMapping(value = "/documents/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentView replace(@PathVariable String id, @RequestPart("file") MultipartFile file, Principal principal) { return service.replace(id, file, principal.getName()); }
    @GetMapping("/documents/{id}/download") public ResponseEntity<byte[]> download(@PathVariable String id) { return service.download(id); }
    @DeleteMapping("/documents/{id}") @PreAuthorize("hasRole('ADMIN')") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) { service.delete(Map.of("ID", id)); }
}
