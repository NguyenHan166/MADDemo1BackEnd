package com.nguyenhan.maddemo1.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter@Setter
public class UserDto {
//    @NotEmpty(message = "Username not be empty")
//    @Size(min = 5, max = 30, message = "The length of the username name should be between 5 and 30")
//    private String username;

    @Email(message = "Email address should be a valid value")
    @NotEmpty(message = "Email not be empty!")
    private String email;

    @NotEmpty(message = "FullName not be empty")
    private String fullName;

    private String age;
    private String gender;
    private LocalDateTime dateOfBirth;

    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    @NotEmpty(message = "Mobile Phone not be empty")
    private String mobilePhone;

}
