package HD.educaze.rest;
import java.util.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<?> validation(MethodArgumentNotValidException error) {
        Map<String, String> fields = new LinkedHashMap<>();
        error.getBindingResult().getFieldErrors().forEach(e -> fields.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("message", "Please check the highlighted fields.", "fields", fields));
    }
    @ExceptionHandler(ResponseStatusException.class) public ResponseEntity<?> status(ResponseStatusException error) { return ResponseEntity.status(error.getStatusCode()).body(Map.of("message", Objects.requireNonNullElse(error.getReason(), "Request failed."))); }
    @ExceptionHandler(DataIntegrityViolationException.class) public ResponseEntity<?> duplicate(DataIntegrityViolationException error) { return ResponseEntity.status(409).body(Map.of("message", "This record already exists or is linked to another record.")); }
    @ExceptionHandler({HttpMessageNotReadableException.class, org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class}) public ResponseEntity<?> invalid(Exception error) { return ResponseEntity.badRequest().body(Map.of("message", "Invalid request format.")); }
}
