package com.bulletinboard.BulletinBoard.user.api.dto.users;

import com.bulletinboard.BulletinBoard.user.db.entity.User;
import lombok.Data;

@Data
public class UserResponseDtoForUser {
    private String name;
    private String email;
    private String login;

}
