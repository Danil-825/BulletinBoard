package com.bulletinboard.BulletinBoard.user.api.dto.users;

import com.bulletinboard.BulletinBoard.user.db.entity.User;
import lombok.Data;

@Data
public class UserResponseDtoForUser {
    private String name;
    private String email;
    private String login;


    public UserResponseDtoForUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.login = user.getLogin();

    }
}
