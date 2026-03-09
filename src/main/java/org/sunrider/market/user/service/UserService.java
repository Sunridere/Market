package org.sunrider.market.user.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunrider.market.exception.BadRequestException;
import org.sunrider.market.exception.NotFoundException;
import org.sunrider.market.exception.UserAlreadyExistsException;
import org.sunrider.market.user.dto.UserDto;
import org.sunrider.market.user.entity.Role;
import org.sunrider.market.user.entity.User;
import org.sunrider.market.user.mapper.UserMapper;
import org.sunrider.market.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto updateUser(User user, UserDto userDto) {

        if (!user.getId().equals(userDto.id()))
        {
            throw new BadRequestException("Неверный ID");
        }

        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());

        return userMapper.toDto(userRepository.save(user));
    }

    public User createUser(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже существует");
        }

        return userRepository.save(user);

    }

    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public UserDetailsService userDetailsService() {
        return this::findUserByUsername;
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(User user) {
        return userMapper.toDto(userRepository.findById(user.getId())
            .orElseThrow(() -> new NotFoundException("Такого пользователя не существует")));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userMapper.toDtos(userRepository.findAll());
    }

    @Transactional
    public void blockUser(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        user.setIsBlocked(true);
        userRepository.save(user);
    }
}
