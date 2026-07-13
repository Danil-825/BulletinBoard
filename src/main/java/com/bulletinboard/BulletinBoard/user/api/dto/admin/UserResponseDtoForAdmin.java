package com.bulletinboard.BulletinBoard.user.api.dto.admin;

import com.bulletinboard.BulletinBoard.user.db.entity.User;
import lombok.Data;

@Data
public class UserResponseDtoForAdmin {
    private Long id;
    private String name;
    private String email;
    private String login;
    private String status;
    private String role;

    public UserResponseDtoForAdmin(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.login = user.getLogin();
        this.status = user.getStatus();
        this.role = user.getRole().toString();
    }
}
