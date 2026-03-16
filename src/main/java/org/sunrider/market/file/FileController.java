package org.sunrider.market.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "Загрузка файлов")
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileService fileService;

    @Operation(summary = "Загрузка изображения (Админ). Возвращает URL для сохранения в ProductImageDto")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload")
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) {
        String url = fileService.save(file);
        return Map.of("url", url);
    }
}
