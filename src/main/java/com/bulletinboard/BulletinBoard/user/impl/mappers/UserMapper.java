package com.bulletinboard.BulletinBoard.user.impl.mappers;

import com.bulletinboard.BulletinBoard.user.api.dto.admin.UserCreateDtoForUser;
import com.bulletinboard.BulletinBoard.user.api.dto.admin.UserResponseDtoForAdmin;
import com.bulletinboard.BulletinBoard.user.api.dto.users.UserResponseDtoForUser;
import com.bulletinboard.BulletinBoard.user.api.dto.users.UserUpdateDtoForUser;
import com.bulletinboard.BulletinBoard.user.db.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Маппинг с Optional
    default UserResponseDtoForAdmin toAdminDto(Optional<User> user) {
        return user.map(this::toAdminDtoInternal).orElse(null);
    }

    default UserResponseDtoForUser toUserDto(Optional<User> user) {
        return user.map(this::toUserDtoInternal).orElse(null);
    }

    UserResponseDtoForAdmin toAdminDto(User user);

    UserResponseDtoForUser toUserDto(User user);

    // Основные методы маппинга
    UserResponseDtoForAdmin toAdminDtoInternal(User user);

    UserResponseDtoForUser toUserDtoInternal(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(UserCreateDtoForUser createDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntity(@MappingTarget User user, UserUpdateDtoForUser updateDto);
}
