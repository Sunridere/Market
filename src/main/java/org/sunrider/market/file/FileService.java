package org.sunrider.market.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.sunrider.market.exception.BadRequestException;
import org.sunrider.market.exception.InternalServerException;

@Service
public class FileService {

    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    public String save(MultipartFile file) {

        if (!ALLOWED_TYPES.contains(file.getContentType())) throw new BadRequestException("Неверный тип файла");
        if (file.getSize() > MAX_SIZE) throw new BadRequestException("Файл слишком большой");

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
