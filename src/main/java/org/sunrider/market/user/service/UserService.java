package org.sunrider.market.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.sunrider.market.exception.NotFoundException;
import org.sunrider.market.exception.UserAlreadyExistsException;
import org.sunrider.market.user.entity.Role;
import org.sunrider.market.user.entity.User;
import org.sunrider.market.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
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

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public UserDetailsService userDetailsService() {
        return this::findUserByUsername;
    }

    public User getCurrentUser() {
        // Получение имени пользователя из контекста Spring Security
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return findUserByUsername(username);
    }

    /**
     * Выдача прав администратора текущему пользователю
     * <p>
     * Нужен для демонстрации
     */
    @Deprecated
    public void getAdmin() {
        var user = getCurrentUser();
        user.setRole(Role.ADMIN);
        saveUser(user);
    }

}
