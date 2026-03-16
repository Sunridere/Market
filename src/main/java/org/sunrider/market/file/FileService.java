package org.sunrider.market.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.sunrider.market.exception.InternalServerException;

@Service
public class FileService {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    public String save(MultipartFile file) {
        String extension = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + extension;
        Path targetPath = Paths.get(uploadDir, "products", filename);

        try {
            Files.createDirectories(targetPath.getParent());
            file.transferTo(targetPath.toAbsolutePath());
        } catch (IOException e) {
            throw new InternalServerException("Не удалось сохранить файл: " + e.getMessage());
        }

        return "/uploads/products/" + filename;
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(dot) : "";
    }
}
