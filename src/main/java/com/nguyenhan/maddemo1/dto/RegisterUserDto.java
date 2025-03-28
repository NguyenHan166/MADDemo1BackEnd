package com.nguyenhan.maddemo1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
public class RegisterUserDto {
    @Email(message = "Email address should be a valid value")
    @NotEmpty(message = "Email not be empty!")
    private String email;

    @NotEmpty(message = "Password not be empty")
    private String password;

//    @NotEmpty(message = "Username not be empty")
//    @Size(min = 5, max = 30, message = "The length of the username name should be between 5 and 30")
//    private String username;

    @NotEmpty(message = "Fullname not be empty")
    private String fullName;

    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    @NotEmpty(message = "Mobile Phone not be empty")
    private String mobilePhone;
}