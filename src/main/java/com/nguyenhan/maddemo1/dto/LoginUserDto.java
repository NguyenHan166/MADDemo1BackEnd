package com.nguyenhan.maddemo1.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserDto {
    @Email(message = "Email address should be a valid value")
    private String email;

    @NotEmpty
    private String password;
}