package com.bulletinboard.BulletinBoard.user.api.dto.users;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateDtoForUser {

    private String name;

    private String login;

    @Email
    private String email;

    private String password;
}
