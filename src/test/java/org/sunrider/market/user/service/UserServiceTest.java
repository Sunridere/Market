package org.sunrider.market.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.sunrider.market.exception.BadRequestException;
import org.sunrider.market.exception.NotFoundException;
import org.sunrider.market.exception.UserAlreadyExistsException;
import org.sunrider.market.user.dto.UserDto;
import org.sunrider.market.user.entity.Role;
import org.sunrider.market.user.entity.User;
import org.sunrider.market.user.mapper.UserMapper;
import org.sunrider.market.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;
    private UUID userId;
    private int page = 0, size = 10;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
            .id(userId)
            .username("testuser")
            .email("test@test.com")
            .password("encoded")
            .firstName("Test")
            .lastName("User")
            .role(Role.ROLE_USER)
            .build();

        userDto = new UserDto(userId, "testuser", "test@test.com", "Updated",
            "Name", LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void updateUser_success() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.updateUser(user, userDto);

        assertThat(result).isEqualTo(userDto);
        assertThat(user.getFirstName()).isEqualTo("Updated");
        assertThat(user.getLastName()).isEqualTo("Name");
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_wrongId_throwsBadRequest() {
        UserDto wrongIdDto = new UserDto(UUID.randomUUID(), "testuser", "test@test.com",
            "Test", "User", LocalDateTime.now(), LocalDateTime.now());

        assertThatThrownBy(() -> userService.updateUser(user, wrongIdDto))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Неверный ID");
    }

    @Test
    void createUser_success() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertThat(result).isEqualTo(user);
        verify(userRepository).save(user);
    }

    @Test
    void createUser_usernameExists_throws() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(user))
            .isInstanceOf(UserAlreadyExistsException.class)
            .hasMessage("Пользователь с таким именем уже существует");
    }

    @Test
    void createUser_emailExists_throws() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(user))
            .isInstanceOf(UserAlreadyExistsException.class)
            .hasMessage("Пользователь с таким email уже существует");
    }

    @Test
    void findUserByUsername_found() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = userService.findUserByUsername("testuser");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void findUserByUsername_notFound_throws() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserByUsername("unknown"))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Пользователь не найден");
    }

    @Test
    void getCurrentUser_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getCurrentUser(user);

        assertThat(result).isEqualTo(userDto);
    }

    @Test
    void getCurrentUser_notFound_throws() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUser(user))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Такого пользователя не существует");
    }

    @Test
    void getAllUsers_success() {
        Page<User> users = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.findAll(PageRequest.of(page, size))).thenReturn(users);
        when(userMapper.toDto(user)).thenReturn(userDto);

        Page<UserDto> result = userService.getAllUsers(page, size);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(userDto);
    }

    @Test
    void userDetailsService_returnsService() {
        assertThat(userService.userDetailsService()).isNotNull();
    }

    @Test
    void blockUser_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.blockUser(userId);

        assertThat(user.getIsBlocked()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void blockUser_notFound_throws() {
        UUID unknownId = UUID.randomUUID();
        when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.blockUser(unknownId))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Пользователь не найден");
    }
}
