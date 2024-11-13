package kr.ac.cbnu.tux.utility;

import kr.ac.cbnu.tux.domain.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class FileHandler {

    public static void saveAttactment(String prefix, String id, MultipartFile file) throws java.io.IOException, IllegalStateException {
        String directoryPath = System.getProperty("user.dir") +
                "/upload/file/" + prefix + "/" + id;
        if (!new File(directoryPath).exists()) {
            new File(directoryPath).mkdirs();
        }

        String filePath = directoryPath + "/" + Objects.requireNonNull(file.getOriginalFilename())
                .replaceAll("[\\\\/:*?\"<>| ]", "_");

        File destFile = new File(filePath);
        file.transferTo(destFile);
    }

    public static void deleteAttactment(String prefix, String id, Attachment file) throws IOException {
        String directoryPath = System.getProperty("user.dir") +
                "/upload/file/" + prefix + "/" + id;
        String filePath = directoryPath + "/" + file.getFilename().replaceAll("[\\\\/:*?\"<>| ]", "_");

        Files.deleteIfExists(Paths.get(filePath));
    }
}
