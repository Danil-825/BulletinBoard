package com.bulletinboard.BulletinBoard.user.api.controllers;

import com.bulletinboard.BulletinBoard.user.api.dto.admin.UserCreateDtoForUser;
import com.bulletinboard.BulletinBoard.user.api.dto.admin.UserResponseDtoForAdmin;
import com.bulletinboard.BulletinBoard.user.api.dto.users.UserResponseDtoForUser;
import com.bulletinboard.BulletinBoard.user.api.dto.users.UserUpdateDtoForUser;
import com.bulletinboard.BulletinBoard.user.impl.services.UserService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Пользователи", description = "Операции с пользователями")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @Operation(summary = "Найти пользователей по name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователи найдены"),
            @ApiResponse(responseCode = "404", description = "Пользователи не найдены"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @GetMapping("/user/name/{name}")
    public List<UserResponseDtoForUser> findByName(@PathVariable String name) {
        return userService.findByName(name);
    }


    @Operation(summary = "Найти пользователя по email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/email/{email}")
    public UserResponseDtoForAdmin findByEmail(@Valid @PathVariable String email) {
        return userService.findByEmail(email);
    }

    @Operation(summary = "Найти пользователя по login")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/login/{login}")
    public UserResponseDtoForAdmin findByLogin(@Valid @PathVariable String login) {
        return userService.findByLogin(login);
    }

    @Operation(summary = "Все пользователи")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователи найдены"),
            @ApiResponse(responseCode = "404", description = "Пользователи не найдены"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public List<UserResponseDtoForAdmin> findAll() {
        return userService.findAll();
    }

    @Operation(summary = "Найти пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/id/{id}")
    public UserResponseDtoForAdmin findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @Operation(summary = "Создать пользователя", description = "Регистрирует нового пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "409", description = "такой email уже есть")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/create")
    public UserResponseDtoForAdmin createUser(@Valid @RequestBody UserCreateDtoForUser user) {
        return userService.createUser(user);
    }



    @Operation(summary = "Удаление пользователя", description = "Удаляет пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удален"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/del/{id}")
    public void delete(@PathVariable Long id) {
        userService.deleteById(id);
    }


    @Operation(summary = "Обновление пользователя им самостоятельно", description = "Пользователь сам обновляет свои данные")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен",
                    content = @Content(schema = @Schema(implementation = UserResponseDtoForAdmin.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "этот email уже есть)")
    })
    @PutMapping("/user/update/{email}")
    public UserResponseDtoForUser updateUser(@AuthenticationPrincipal UserDetails login, @Valid @RequestBody UserUpdateDtoForUser user) {
        return userService.updateUser(login.getUsername(), user);
    }


    @Operation(summary = "Поиск пользователя пользователем", description = "Пользователь ищет другого пользователя по email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @GetMapping("/user/findUser/{email}")
    public UserResponseDtoForUser findForUserByEmail(@Valid @PathVariable String email) {
        return userService.findForUserByEmail(email);
    }

    @Operation(summary = "Вывод всех пользователей для пользователя", description = "Пользователь смотрит на всех других пользователей")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователи найдены"),
            @ApiResponse(responseCode = "404", description = "Пользователи не найдены"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @GetMapping("/user/all")
    public List<UserResponseDtoForUser> findAllUsers() {
        return userService.findAllUsers();
    }



}
