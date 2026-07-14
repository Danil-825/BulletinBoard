package com.bulletinboard.BulletinBoard.build.api.dto.users;

import lombok.Data;

@Data
public class AdUpdateDtoForUser {

    private String name;

    private String description;

    private String category;

    private double price;
}
