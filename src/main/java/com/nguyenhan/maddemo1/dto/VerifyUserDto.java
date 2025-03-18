package com.nguyenhan.maddemo1.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyUserDto {

    @Email(message = "Email address should be a valid value")
    @NotEmpty(message = "Email not be empty!")
    private String email;
    private String verificationCode;
}