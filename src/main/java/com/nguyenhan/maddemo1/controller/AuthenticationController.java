package com.nguyenhan.maddemo1.controller;

import com.nguyenhan.maddemo1.constants.UsersConstants;
import com.nguyenhan.maddemo1.dto.LoginUserDto;
import com.nguyenhan.maddemo1.dto.RegisterUserDto;
import com.nguyenhan.maddemo1.dto.VerifyUserDto;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.responses.LoginResponse;
import com.nguyenhan.maddemo1.service.AuthenticationService;
import com.nguyenhan.maddemo1.service.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.status(UsersConstants.STATUS_201).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginUserDto loginUserDto){
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.status(UsersConstants.STATUS_200).body(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@Valid @RequestBody VerifyUserDto verifyUserDto) {
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam
                                                        @Email(message = "Email address should be a valid value")
                                                        @NotEmpty(message = "Email not be empty!")
                                                        String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // sửa lại
        }
    }

//    POST http://localhost:8080/auth/loginWithGoogle?
//    email={{$random.alphanumeric(8)}}&
//    username={{$random.alphanumeric(8)}}&
//    mobilePhone={{$random.alphanumeric(8)}}
    @PostMapping("/loginWithGoogle")
    public ResponseEntity<LoginResponse> loginWithGoogle(@RequestParam
                                                             @Email(message = "Email address should be a valid value")
                                                             @NotEmpty(message = "Email not be empty!")
                                                             String email, @RequestParam
    @NotEmpty(message = "Username not be empty!")
    String username,@RequestParam String mobilePhone) {
        User authenticatedUser = authenticationService.signupWithGoogle(email, username, mobilePhone);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.status(UsersConstants.STATUS_200).body(loginResponse);
    }
}