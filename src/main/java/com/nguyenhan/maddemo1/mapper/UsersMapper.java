package com.nguyenhan.maddemo1.mapper;

import com.nguyenhan.maddemo1.dto.UserDto;
import com.nguyenhan.maddemo1.model.User;

public class UsersMapper {
    public static UserDto mapToUserDto(User user, UserDto userDto) {
//        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setAge(user.getAge());
        userDto.setFullName(user.getFullName());
        userDto.setGender(user.getGender());
        userDto.setMobilePhone(user.getMobilePhone());
        userDto.setDateOfBirth(user.getDateOfBirth());
        return userDto;
    }

    public static User mapToUser(UserDto userDto, User user) {
//        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setAge(userDto.getAge());
        user.setFullName(userDto.getFullName());
        user.setGender(userDto.getGender());
        user.setMobilePhone(userDto.getMobilePhone());
        user.setDateOfBirth(userDto.getDateOfBirth());
        return user;
    }
}
