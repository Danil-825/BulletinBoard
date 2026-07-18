package com.bulletinboard.BulletinBoard.build.api.dto.users;

import com.bulletinboard.BulletinBoard.build.db.entity.Ad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AdResponseForUserDto {
    private String name;
    private String description;
    private String category;
    private double price;
    private String status;
    private String nameUser;
    private String emailUser;

    public AdResponseForUserDto(Ad ad1) {
    }
}
