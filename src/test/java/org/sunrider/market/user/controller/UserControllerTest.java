package org.sunrider.market.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.sunrider.market.exception.NotFoundException;
import org.sunrider.market.security.JwtService;
import org.sunrider.market.user.dto.UserDto;
import org.sunrider.market.user.entity.Role;
import org.sunrider.market.user.entity.User;
import org.sunrider.market.user.service.UserService;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    private User testUser;
    private UserDto userDto;
    private UUID userId;
    private int page = 0;
    private int size = 10;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
            .id(userId)
            .username("testuser")
            .email("test@test.com")
            .password("encoded")
            .firstName("Test")
            .lastName("User")
            .role(Role.ROLE_USER)
            .build();

        userDto = new UserDto(userId, "testuser", "test@test.com", "Test", "User",
            LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void getCurrentUser_success() throws Exception {
        when(userService.getCurrentUser(any(User.class))).thenReturn(userDto);

        mockMvc.perform(get("/api/v1/user/me")
                .with(user(testUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void updateCurrentUser_success() throws Exception {
        UserDto updatedDto = new UserDto(userId, "testuser", "test@test.com", "Updated", "Name",
            LocalDateTime.now(), LocalDateTime.now());
        when(userService.updateUser(any(User.class), any(UserDto.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/api/v1/user/me")
                .with(user(testUser))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Updated"))
            .andExpect(jsonPath("$.lastName").value("Name"));
    }

    @Test
    void getAllUsers_success() throws Exception {
        User adminUser = User.builder()
            .id(UUID.randomUUID())
            .username("admin")
            .email("admin@test.com")
            .password("encoded")
            .role(Role.ROLE_ADMIN)
            .build();

        when(userService.getAllUsers(page, size)).thenReturn(new PageImpl<>(Collections.singletonList(userDto)));

        mockMvc.perform(get("/api/v1/user")
                .with(user(adminUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].username").value("testuser"));
    }

    @Test
    void blockUser_success() throws Exception {
        User adminUser = User.builder()
            .id(UUID.randomUUID())
            .username("admin")
            .email("admin@test.com")
            .password("encoded")
            .role(Role.ROLE_ADMIN)
            .build();

        doNothing().when(userService).blockUser(userId);

        mockMvc.perform(patch("/api/v1/user/block/{id}", userId)
                .with(user(adminUser))
                .with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    void blockUser_notFound() throws Exception {
        User adminUser = User.builder()
            .id(UUID.randomUUID())
            .username("admin")
            .email("admin@test.com")
            .password("encoded")
            .role(Role.ROLE_ADMIN)
            .build();

        UUID unknownId = UUID.randomUUID();
        doThrow(new NotFoundException("Пользователь не найден"))
            .when(userService).blockUser(unknownId);

        mockMvc.perform(delete("/api/v1/user/{id}", unknownId)
                .with(user(adminUser))
                .with(csrf()))
            .andExpect(status().isNotFound());
    }
}
