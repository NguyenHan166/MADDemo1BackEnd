package com.nguyenhan.maddemo1.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDto {
    private String email;
    private String password;
    private String username;
    private String fullname;
    private String mobilePhone;
}