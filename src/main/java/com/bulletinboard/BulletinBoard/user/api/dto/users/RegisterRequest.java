package com.bulletinboard.BulletinBoard.user.api.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String login;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
}
