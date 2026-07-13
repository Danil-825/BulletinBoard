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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    public List<UserResponseDtoForAdmin> findAll() {
        List<User> users = userRepository.findAll();
        checkList(users);
        return users.stream()
                .map(UserResponseDtoForAdmin::new)
                .collect(Collectors.toList());
    }

    public UserResponseDtoForAdmin findById(Long id) {
        User user = findByThrowUserNotFound(
                () -> userRepository.findById(id), "id", String.valueOf(id)
        );
        return new UserResponseDtoForAdmin(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserResponseDtoForAdmin createUser(UserCreateDtoForUser createDto) {
        User user = new User();
        user.setName(createDto.getName());
        user.setLogin(createDto.getLogin());
        throwIfExists(() -> userRepository.findByLogin(createDto.getLogin()),
                "Login", createDto.getLogin());
        user.setEmail(createDto.getEmail());
        throwIfExists(() -> userRepository.findByEmail(createDto.getEmail()),
                "Email", createDto.getEmail());
        user.setPassword(passwordEncoder.encode(createDto.getPassword()));
        user.setRole(Role.valueOf("USER"));
        user.setStatus("ACTIVE");
        User saved = userRepository.save(user);
        return new UserResponseDtoForAdmin(saved);
    }

    private User updateBaseFields(User user, String name, String login, String email, String password) {
        if (name != null) user.setName(name);
        if (login != null) {
            user.setLogin(login);
            throwIfExists(() -> userRepository.findByLogin(login),
                    "Login", login);
        }
        if (email != null) {
            user.setEmail(email);
            throwIfExists(() -> userRepository.findByEmail(email),
                    "Email", email);
        }
        if (password != null) user.setPassword(passwordEncoder.encode(password));
        return user;
    }


    @Transactional(rollbackFor = Exception.class)
    public UserResponseDtoForUser updateUser(String login, UserUpdateDtoForUser dto) {
        User user = findByThrowUserNotFound(
                () -> userRepository.findByLogin(login), "login", login
        );
        User userUpdate = updateBaseFields
                (user, dto.getName(), dto.getLogin(), dto.getEmail(), dto.getPassword());
        User saved = userRepository.save(userUpdate);

        return new UserResponseDtoForUser(saved);
    }

    public List<UserResponseDtoForUser> findByName(String name) {
        List<User> users = userRepository.findByName(name);
        checkList(users);
        return users.stream()
                .map(UserResponseDtoForUser::new)
                .collect(Collectors.toList());
    }


    public UserResponseDtoForAdmin findByEmail(String email) {
        User user = findByThrowUserNotFound(
                () -> userRepository.findByEmail(email), "email", email
        );
        return new UserResponseDtoForAdmin(user);
    }

    public UserResponseDtoForAdmin findByLogin(String login) {
        User user = findByThrowUserNotFound(
                () -> userRepository.findByLogin(login), "login", login
        );
        return new UserResponseDtoForAdmin(user);
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
        return new UserResponseDtoForUser(user);
    }

    public List<UserResponseDtoForUser> findAllUsers() {
        List<User> users = userRepository.findAllUsers();
        checkList(users);
        return users.stream()
                .map(UserResponseDtoForUser::new)
                .collect(Collectors.toList());
    }




    private User findByThrowUserNotFound(Supplier<Optional<User>> supplier, String field, String value) {
        return supplier.get()
                .orElseThrow(() -> {
                    log.warn("User not found with {}: {}", field, value);
                    return new UserNotFoundException("User with " + field + " " + value + " not found");
                });
    }

    private void throwIfExists(Supplier<Optional<User>> supplier, String word,
                               String value) {
        supplier.get().ifPresent(user -> {
            log.warn("{} already exists: {}", word, value);
            throw new ObjectAlreadyExistsException(word + " already exists: " + value);
        });
    }

    private void checkList(List<User> users) {
        if (users.isEmpty()){
            log.warn("Users not found");
            throw new UserNotFoundException("Users not found");
        }
    }
}
