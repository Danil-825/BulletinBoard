package com.bulletinboard.BulletinBoard.user.impl.services;

import com.bulletinboard.BulletinBoard.common.exceptions.ObjectAlreadyExistsException;
import com.bulletinboard.BulletinBoard.common.exceptions.UserNotFoundException;
import com.bulletinboard.BulletinBoard.user.api.dto.admin.UserCreateDtoForUser;
import com.bulletinboard.BulletinBoard.user.api.dto.admin.UserResponseDtoForAdmin;
import com.bulletinboard.BulletinBoard.user.api.dto.users.UserResponseDtoForUser;
import com.bulletinboard.BulletinBoard.user.api.dto.users.UserUpdateDtoForUser;
import com.bulletinboard.BulletinBoard.user.db.entity.User;
import com.bulletinboard.BulletinBoard.user.db.enums.Role;
import com.bulletinboard.BulletinBoard.user.db.repository.UserRepository;
import com.bulletinboard.BulletinBoard.user.impl.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public List<UserResponseDtoForAdmin> findAll() {
        List<User> users = userRepository.findAll();
        checkList(users);
        return users.stream()
                .map(userMapper::toAdminDto)
                .collect(Collectors.toList());
    }

    public UserResponseDtoForAdmin findById(Long id) {
        User user = findByThrowUserNotFound(
                () -> userRepository.findById(id), "id", String.valueOf(id)
        );
        return userMapper.toAdminDto(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserResponseDtoForAdmin createUser(UserCreateDtoForUser createDto) {
        throwIfExists(() -> userRepository.findByLogin(createDto.getLogin()),
                "Login", createDto.getLogin());
        throwIfExists(() -> userRepository.findByEmail(createDto.getEmail()),
                "Email", createDto.getEmail());

        User user = userMapper.toEntity(createDto);

        user.setPassword(passwordEncoder.encode(createDto.getPassword()));
        user.setRole(Role.USER);
        user.setStatus("ACTIVE");

        User saved = userRepository.save(user);
        return userMapper.toAdminDto(saved);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserResponseDtoForUser updateUser(String login, UserUpdateDtoForUser dto) {
        User user = findByThrowUserNotFound(
                () -> userRepository.findByLogin(login), "login", login
        );
        if (dto.getLogin() != null && !dto.getLogin().isBlank() && !dto.getLogin().equals(user.getLogin())) {
            throwIfExists(() -> userRepository.findByLogin(dto.getLogin()),
                    "Login", dto.getLogin());
            user.setLogin(dto.getLogin());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().equals(user.getEmail())) {
            throwIfExists(() -> userRepository.findByEmail(dto.getEmail()),
                    "Email", dto.getEmail());
            user.setEmail(dto.getEmail());
        }
        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        User saved = userRepository.save(user);
        return userMapper.toUserDto(saved);
    }

    public List<UserResponseDtoForUser> findByName(String name) {
        List<User> users = userRepository.findByName(name);
        checkList(users);
        return users.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserResponseDtoForAdmin findByEmail(String email) {
        User user = findByThrowUserNotFound(
                () -> userRepository.findByEmail(email), "email", email
        );
        return userMapper.toAdminDto(user);
    }

    public UserResponseDtoForAdmin findByLogin(String login) {
        User user = findByThrowUserNotFound(
                () -> userRepository.findByLogin(login), "login", login
        );
        return userMapper.toAdminDto(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public UserResponseDtoForUser findForUserByEmail(String email) {
        User user = findByThrowUserNotFound(
                () -> userRepository.findByEmail(email),
                "email", email
        );
        return userMapper.toUserDto(user);
    }

    public List<UserResponseDtoForUser> findAllUsers() {
        List<User> users = userRepository.findAllUsers();
        checkList(users);
        return users.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private User findByThrowUserNotFound(Supplier<Optional<User>> supplier, String field, String value) {
        return supplier.get()
                .orElseThrow(() -> {
                    log.warn("User not found with {}: {}", field, value);
                    return new UserNotFoundException("User with " + field + " " + value + " not found");
                });
    }

    private void throwIfExists(Supplier<Optional<User>> supplier, String word, String value) {
        supplier.get().ifPresent(user -> {
            log.warn("{} already exists: {}", word, value);
            throw new ObjectAlreadyExistsException(word + " already exists: " + value);
        });
    }

    private void checkList(List<User> users) {
        if (users.isEmpty()) {
            log.warn("Users not found");
            throw new UserNotFoundException("Users not found");
        }
    }
}