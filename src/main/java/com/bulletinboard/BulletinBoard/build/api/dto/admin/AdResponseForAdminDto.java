package com.bulletinboard.BulletinBoard.build.api.dto.admin;

import com.bulletinboard.BulletinBoard.build.db.entity.Ad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AdResponseForAdminDto {
    private Long id;
    private String name;
    private String description;
    private String category;
    private double price;
    private String status;
    private LocalDateTime createdAt;
    private String nameUser;
    private String emailUser;


    public AdResponseForAdminDto(Ad ad1) {
    }
}
