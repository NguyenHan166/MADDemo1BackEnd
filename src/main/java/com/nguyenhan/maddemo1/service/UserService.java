package com.nguyenhan.maddemo1.service;


import com.nguyenhan.maddemo1.dto.UserDto;
import com.nguyenhan.maddemo1.exception.ResourceNotFoundException;
import com.nguyenhan.maddemo1.mapper.UsersMapper;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    @Transactional
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();// Lấy User từ Authentication
            return currentUser;
        }
        return null;
    }

    @Transactional
    public UserDto getAuthenticatedUserDto() {
        User user = getAuthenticatedUser();
        if (user != null) {
            return UsersMapper.mapToUserDto(user, new UserDto());
        }
        return null;
    }


    // update user
    public boolean updateUser(UserDto userDto) {
        boolean isUpdated = false;

        User findUser = userRepository.findByEmail(userDto.getEmail()).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", userDto.getEmail())
        );

        UsersMapper.mapToUser(userDto, findUser);
        userRepository.save(findUser);

        isUpdated = true;
        return isUpdated;
    }

    public boolean deleteUser(String email) {
        boolean isDeleted = false;
        User findUser = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", email)
        );
        if (findUser != null) {
            userRepository.deleteById(findUser.getId());
            isDeleted = true;
        }
        return isDeleted;
    }
}