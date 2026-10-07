package HD.educaze.config;

import io.minio.MinioClient;
import java.time.Duration;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
public class MinioConfig {
    @Bean public MinioClient minioClient(
            @Value("${app.storage.minio.endpoint}") String endpoint,
            @Value("${app.storage.minio.access-key}") String accessKey,
            @Value("${app.storage.minio.secret-key}") String secretKey) {
        var builder = MinioClient.builder().endpoint(endpoint).httpClient(new OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(3)).readTimeout(Duration.ofSeconds(30))
            .writeTimeout(Duration.ofSeconds(30)).build());
        // Do not make storage availability a prerequisite for existing CRUD/auth.
        if (!accessKey.isBlank() && !secretKey.isBlank()) builder.credentials(accessKey, secretKey);
        return builder.build();
    }
}
