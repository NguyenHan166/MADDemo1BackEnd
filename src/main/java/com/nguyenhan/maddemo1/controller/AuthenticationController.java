package com.nguyenhan.maddemo1.controller;

import com.nguyenhan.maddemo1.constants.UsersConstants;
import com.nguyenhan.maddemo1.dto.LoginUserDto;
import com.nguyenhan.maddemo1.dto.RegisterUserDto;
import com.nguyenhan.maddemo1.dto.UserDto;
import com.nguyenhan.maddemo1.dto.VerifyUserDto;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.responses.LoginResponse;
import com.nguyenhan.maddemo1.responses.ResponseDto;
import com.nguyenhan.maddemo1.service.AuthenticationService;
import com.nguyenhan.maddemo1.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Đăng ký người dùng", description = "Tạo tài khoản mới cho người dùng.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Đăng ký thành công", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    @PostMapping("/signup")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.status(UsersConstants.STATUS_201).body(registeredUser);
    }

//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginUserDto loginUserDto) {
//        User authenticatedUser = authenticationService.authenticate(loginUserDto);
//        String jwtToken = jwtService.generateToken(authenticatedUser);
//        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
//        return ResponseEntity.status(UsersConstants.STATUS_200).body(loginResponse);
//    }

    @Operation(summary = "Đăng nhập người dùng", description = "Đăng nhập người dùng bằng email và mật khẩu.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng nhập thành công", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Thông tin đăng nhập không hợp lệ")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
//        LoginUserDto loginUserDto = new LoginUserDto();
//        loginUserDto.setEmail(email);
//        loginUserDto.setPassword(password);
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.status(UsersConstants.STATUS_200).body(loginResponse);
    }

//    @PostMapping("/verify")
//    public ResponseEntity<ResponseDto> verifyUser(@Valid @RequestBody VerifyUserDto verifyUserDto) {
//        try {
//            authenticationService.verifyUser(verifyUserDto);
//            ResponseDto responseDto = new ResponseDto(UsersConstants.STATUS_200, "Account verified successfully");
//            return ResponseEntity.status(UsersConstants.STATUS_200).body(responseDto);
//        } catch (RuntimeException e) {
//            ResponseDto responseDto = new ResponseDto(UsersConstants.STATUS_400, "Account verified failed");
//            return ResponseEntity.status(UsersConstants.STATUS_400).body(responseDto);
//        }
//    }


    @Operation(summary = "Đăng nhập với Google", description = "Đăng nhập người dùng qua tài khoản Google.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng nhập thành công", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Thông tin đăng nhập không hợp lệ")
    })
    @PostMapping("/loginWithGoogle")
    public ResponseEntity<LoginResponse> loginWithGoogle(
            @Parameter(description = "Email của người dùng", required = true)
            @RequestParam
            @Email(message = "Email address should be a valid value")
            @NotEmpty(message = "Email not be empty!")
            String email, @RequestParam
            @NotEmpty(message = "Username not be empty!")
            String username, @RequestParam String mobilePhone) {
        User authenticatedUser = authenticationService.signupWithGoogle(email, username, mobilePhone);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.status(UsersConstants.STATUS_200).body(loginResponse);
    }


    @Operation(summary = "Gửi email xác minh", description = "Gửi email xác minh cho người dùng.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email xác minh đã được gửi thành công"),
            @ApiResponse(responseCode = "400", description = "Có lỗi xảy ra khi gửi email")
    })
    @PostMapping("/sendEmail")
    public ResponseEntity<Object> sendEmail(
            @Parameter(description = "Email của người dùng", required = true)
            @RequestParam
            @Email(message = "Email address should be a valid value")
            @NotEmpty(message = "Email not be empty!")
            String email,
            @Parameter(description = "Event muốn thực thiện (gửi email xác minh hoặc quên mật khẩu)", required = true)
            @RequestParam String event) {
        try {
            VerifyUserDto verifyUserDto = authenticationService.sendEmail(email, event);
            return ResponseEntity.status(UsersConstants.STATUS_200).body(verifyUserDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(UsersConstants.STATUS_400).body(new ResponseDto(UsersConstants.STATUS_400, e.getMessage()));
        }
    }

    @Operation(summary = "Quên mật khẩu", description = "Xử lý quên mật khẩu và thay đổi mật khẩu mới.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mật khẩu đã được thay đổi thành công", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Có lỗi khi thay đổi mật khẩu")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<UserDto> forgotPassword(@Valid @RequestBody VerifyUserDto verifyUserDto,
                                                  @Parameter(description = "Password mới ", required = true)
                                                  @RequestParam String newPassword) {
        try {
            UserDto userDto = authenticationService.forgotPassword(verifyUserDto, newPassword);
            return ResponseEntity.status(UsersConstants.STATUS_200).body(userDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(UsersConstants.STATUS_400)
                    .build();
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<Object> changePassword(@Valid @RequestBody VerifyUserDto verifyUserDto,
                                                  @RequestParam String oldPassword,
                                                  @Parameter(description = "Password mới ", required = true)
                                                  @RequestParam String newPassword) {
        try {
            UserDto userDto = authenticationService.changePassword(verifyUserDto, oldPassword ,newPassword);
            return ResponseEntity.status(UsersConstants.STATUS_200).body(userDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(UsersConstants.STATUS_400).body(new ResponseDto(UsersConstants.STATUS_400, e.getMessage()));
        }
    }
}