package HD.educaze.service;
import java.io.InputStream;

public interface DocumentStorageService {
    void store(String path, InputStream content, long size, String contentType);
    byte[] download(String path);
    void remove(String path);
    boolean exists(String path);
}
