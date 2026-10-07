package HD.educaze.service.Impl;

import HD.educaze.service.DocumentStorageService;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import java.io.InputStream;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MinioDocumentStorageService implements DocumentStorageService {
    private static final Logger log = LoggerFactory.getLogger(MinioDocumentStorageService.class);
    private final MinioClient client;
    private final String bucket;
    private final boolean configured;
    private volatile boolean bucketReady;
    public MinioDocumentStorageService(MinioClient client,
            @Value("${app.storage.minio.bucket}") String bucket,
            @Value("${app.storage.minio.access-key}") String accessKey,
            @Value("${app.storage.minio.secret-key}") String secretKey) {
        this.client = client; this.bucket = bucket;
        configured = !accessKey.isBlank() && !secretKey.isBlank();
    }
    private ResponseStatusException unavailable(Exception cause) {
        log.warn("Document storage operation failed ({})", cause.getClass().getSimpleName());
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "File storage is temporarily unavailable.", cause);
    }
    private void requireConfiguration() {
        if (!configured) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "File storage is temporarily unavailable.");
    }
    private synchronized void ensureBucket() throws Exception {
        if (bucketReady) return;
        if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            try { client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build()); }
            catch (ErrorResponseException error) {
                if (!"BucketAlreadyOwnedByYou".equals(error.errorResponse().code())) throw error;
            }
        }
        bucketReady = true;
    }
    @Override public void store(String path, InputStream content, long size, String contentType) {
        requireConfiguration();
        try {
            ensureBucket();
            client.putObject(PutObjectArgs.builder().bucket(bucket).object(path)
                .stream(content, size, -1).contentType(contentType).build());
        } catch (Exception error) { throw unavailable(error); }
    }
    @Override public byte[] download(String path) {
        requireConfiguration();
        try (InputStream input = client.getObject(GetObjectArgs.builder().bucket(bucket).object(path).build())) {
            return input.readAllBytes();
        } catch (Exception error) { throw unavailable(error); }
    }
    @Override public void remove(String path) {
        requireConfiguration();
        try { client.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(path).build()); }
        catch (Exception error) { throw unavailable(error); }
    }
    @Override public boolean exists(String path) {
        requireConfiguration();
        try { client.statObject(StatObjectArgs.builder().bucket(bucket).object(path).build()); return true; }
        catch (ErrorResponseException error) {
            if (java.util.Set.of("NoSuchKey", "NoSuchObject", "NoSuchBucket").contains(error.errorResponse().code())) return false;
            throw unavailable(error);
        } catch (Exception error) { throw unavailable(error); }
    }
}
