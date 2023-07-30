package kr.ac.cbnu.tux.utility;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Objects;

public class FileHandler {

    public static void saveAttactment(String prefix, String id, MultipartFile file) throws java.io.IOException, IllegalStateException {
        String directoryPath = System.getProperty("user.dir") +
                "/src/main/resources/static/file/" + prefix + "/" + id;
        if (!new File(directoryPath).exists()) {
            new File(directoryPath).mkdirs();
        }

        String filePath = directoryPath + "/" + Objects.requireNonNull(file.getOriginalFilename())
                .replaceAll("[\\\\/:*?\"<>| ]", "_");

        File destFile = new File(filePath);
        file.transferTo(destFile);


    }
}
