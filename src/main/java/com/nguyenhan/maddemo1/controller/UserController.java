package com.nguyenhan.maddemo1.controller;

import com.nguyenhan.maddemo1.constants.UsersConstants;
import com.nguyenhan.maddemo1.dto.UserDto;
import com.nguyenhan.maddemo1.mapper.UsersMapper;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.responses.ResponseDto;
import com.nguyenhan.maddemo1.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/users")
@RestController
@Slf4j
public class UserController {
    private final UserService userService;
    private final UsersMapper usersMapper;
    public UserController(UserService userService , UsersMapper usersMapper) {
        this.userService = userService;
        this.usersMapper = usersMapper;
    }


    @Operation(summary = "Lấy thông tin người dùng hiện tại đã xác thực")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy thông tin người dùng thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực")
    })
    @GetMapping("/me")
    public ResponseEntity<Object> getCurrentUserDto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        UserDto  userDto = usersMapper.mapToUserDto(currentUser, new UserDto());
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "Lấy thông tin người dùng đã xác thực")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy người dùng thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực")
    })
    @GetMapping("/checkUser")
    public ResponseEntity<User> authenticatedUser() {
        User currentUser = userService.getAuthenticatedUser();
        return ResponseEntity.ok(currentUser);
    }

    @Operation(summary = "Lấy tất cả người dùng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy tất cả người dùng thành công"),
            @ApiResponse(responseCode = "500", description = "Lỗi nội bộ")
    })
    @GetMapping("/")
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Cập nhật thông tin người dùng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật người dùng thành công"),
            @ApiResponse(responseCode = "417", description = "Cập nhật thất bại")
    })
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateUser(@RequestBody UserDto userDto) {
        boolean isUpdated = userService.updateUser(userDto);
        if (isUpdated) {
            return ResponseEntity.ok(new ResponseDto(UsersConstants.STATUS_200, UsersConstants.MESSAGE_200));
        }else{
            return ResponseEntity.ok(new ResponseDto(UsersConstants.STATUS_417, UsersConstants.MESSAGE_417_UPDATE));
        }
    }

    @Operation(summary = "Xóa người dùng theo email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xóa người dùng thành công"),
            @ApiResponse(responseCode = "417", description = "Xóa thất bại"),
            @ApiResponse(responseCode = "400", description = "Yêu cầu sai, email không hợp lệ")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteUser(@RequestParam
                                                      @Email(message = "Email address should be a valid value")
                                                      @NotEmpty(message = "Email not be empty!")
                                                      String email) {
        boolean isDeleted = userService.deleteUser(email);
        if (isDeleted) {
            return ResponseEntity.ok(new ResponseDto(UsersConstants.STATUS_200, UsersConstants.MESSAGE_200));
        }else{
            return ResponseEntity.ok(new ResponseDto(UsersConstants.STATUS_417, UsersConstants.MESSAGE_417_DELETE));
        }
    }
}