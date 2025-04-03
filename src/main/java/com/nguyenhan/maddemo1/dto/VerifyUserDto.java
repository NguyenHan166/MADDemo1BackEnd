package com.nguyenhan.maddemo1.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Data@AllArgsConstructor@NoArgsConstructor
public class VerifyUserDto {

    @Email(message = "Email address should be a valid value")
    @NotEmpty(message = "Email not be empty!")
    private String email;
    private String verificationCode;
}