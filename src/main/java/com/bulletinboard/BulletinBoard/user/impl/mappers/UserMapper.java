package com.bulletinboard.BulletinBoard.user.impl.mappers;

import com.bulletinboard.BulletinBoard.user.api.dto.admin.UserCreateDtoForUser;
import com.bulletinboard.BulletinBoard.user.api.dto.admin.UserResponseDtoForAdmin;
import com.bulletinboard.BulletinBoard.user.api.dto.users.UserResponseDtoForUser;
import com.bulletinboard.BulletinBoard.user.db.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {


    UserResponseDtoForAdmin toAdminDto(User user);

    UserResponseDtoForUser toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(UserCreateDtoForUser createDto);

}
