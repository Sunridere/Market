package org.sunrider.market.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sunrider.market.user.dto.UserDto;
import org.sunrider.market.user.entity.User;
import org.sunrider.market.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Сервис пользователя")
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Получение профиля пользователя")
    @GetMapping("/me")
    public UserDto getCurrentUser(@AuthenticationPrincipal User user) {
        return userService.getCurrentUser(user);
    }

    @Operation(summary = "Обновить информация о пользователе")
    @PutMapping("/me")
    public UserDto updateCurrentUser(
        @AuthenticationPrincipal User user,
        @RequestBody UserDto userDto) {
        return userService.updateUser(user, userDto);
    }

    @Operation(summary = "Получение списка всех пользователей (Только для Админа)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }
}
