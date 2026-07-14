package com.bulletinboard.BulletinBoard.build.impl.mappers;


import com.bulletinboard.BulletinBoard.build.api.dto.admin.AdResponseForAdminDto;
import com.bulletinboard.BulletinBoard.build.api.dto.users.AdCreateDTO;
import com.bulletinboard.BulletinBoard.build.api.dto.users.AdResponseForUserDto;
import com.bulletinboard.BulletinBoard.build.api.dto.users.AdUpdateDtoForUser;
import com.bulletinboard.BulletinBoard.build.db.entity.Ad;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AdMapper {

    @Mapping(target = "nameUser", expression = "java(ad.getUser().getName())")
    @Mapping(target = "emailUser", expression = "java(ad.getUser().getEmail())")
    AdResponseForAdminDto toResponseDtoForAdmin(Ad ad);

    @Mapping(target = "nameUser", expression = "java(ad.getUser().getName())")
    @Mapping(target = "emailUser", expression = "java(ad.getUser().getEmail())")
    AdResponseForUserDto toResponseDtoForUser(Ad ad);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Ad toEntity(AdCreateDTO adCreateDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntity(@MappingTarget Ad ad, AdUpdateDtoForUser dto);
}
