package org.sunrider.market.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto (

    @Schema(description = "Id пользователя")
    UUID id,

    @Schema(description = "Никнейм пользователя", example = "Test")
    @Size(min = 5, max = 50, message = "Никнейм пользователя должен содержать от 5 до 50 символов")
    @NotBlank(message = "Никнейм пользователя не может быть пустыми")
    String username,

    @Schema(description = "Адрес электронной почты", example = "test@gmail.com")
    @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
    @NotBlank(message = "Адрес электронной почты не может быть пустыми")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    String email,

    @Schema(description = "Имя пользователя", example = "Vitaliy")
    String firstName,

    @Schema(description = "Фамилия пользователя", example = "Ribin")
    String lastName,

    @Schema(description = "Дата создания пользователя", example = "2025-07-10T14:25:30.123456")
    LocalDateTime createdAt,

    @Schema(description = "Дата обновления пользователя", example = "2025-07-10T14:25:30.123456")
    LocalDateTime updatedAt
){}
