package com.nguyenhan.maddemo1.service;


import com.nguyenhan.maddemo1.dto.LoginUserDto;
import com.nguyenhan.maddemo1.dto.RegisterUserDto;
import com.nguyenhan.maddemo1.dto.UserDto;
import com.nguyenhan.maddemo1.dto.VerifyUserDto;
import com.nguyenhan.maddemo1.exception.*;
import com.nguyenhan.maddemo1.mapper.UsersMapper;
import com.nguyenhan.maddemo1.model.Notification;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UsersMapper usersMapper;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            UsersMapper usersMapper
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.usersMapper = usersMapper;
    }

    public User signup(RegisterUserDto input) {
        Optional<User> findUser = userRepository.findByEmail(input.getEmail());

        if (findUser.isPresent() && !findUser.get().isEnabled()) {
            throw new AccountNotVerifiedException("Account not verified. Please verify your account.");
        }

        if (findUser.isPresent()) {
            throw new UserAlreadyExistsException("User already exists with this Email");
        }


        User user = new User(input.getEmail(), passwordEncoder.encode(input.getPassword()), input.getFullName(), input.getMobilePhone());
//        user.setVerificationCode(generateVerificationCode());
//        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(true);
        return userRepository.save(user);
    }

    public User signupWithGoogle(String email, String username, String mobilePhone) {

        if (userRepository.findByEmail(email).isPresent()) {
            return userRepository.findByEmail(email).get();
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFullName(username);
        newUser.setMobilePhone(mobilePhone);
        newUser.setEnabled(true);

        userRepository.save(newUser);

        return newUser;
    }

    public User authenticate(LoginUserDto input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", input.getEmail()));

        if (!user.isEnabled()) {
            throw new AccountNotVerifiedException("Account not verified. Please verify your account.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return user;
    }

    public void verifyUser(VerifyUserDto input) {
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new VerificationCodeInvalid("Verification code has expired");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userRepository.save(user);
            } else {
                throw new VerificationCodeInvalid("Invalid verification code");
            }
        } else {
            throw new ResourceNotFoundException("User", "email", input.getEmail());
        }
    }

//    public void sendVerificationCodeForgotPassword(String email) {
//        User user = userRepository.findByEmail(email).orElseThrow(
//                () -> new ResourceNotFoundException("User", "email", email)
//        );
//        if (!user.isEnabled()) {
//            throw new AccountNotVerifiedException("Account not verified. Please verify your account.");
//        }
//        user.setVerificationCode(generateVerificationCode());
//        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
//        sendVerificationEmailForgotPassword(user);
//        userRepository.save(user);
//    }

    public UserDto forgotPassword(VerifyUserDto verifyUserDto, String newPassword) {
        User user = userRepository.findByEmail(verifyUserDto.getEmail()).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", verifyUserDto.getEmail())
        );
        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new VerificationCodeInvalid("Verification code has expired");
        }
        if (user.getVerificationCode().equals(verifyUserDto.getVerificationCode())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setVerificationCode(null);
            user.setVerificationCodeExpiresAt(null);
            User savedUser = userRepository.save(user);
            return usersMapper.mapToUserDto(savedUser, new UserDto());
        } else {
            throw new VerificationCodeInvalid("Invalid verification code");
        }
    }

    public UserDto changePassword(VerifyUserDto verifyUserDto, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(verifyUserDto.getEmail()).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", verifyUserDto.getEmail())
        );
        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new VerificationCodeInvalid("Verification code has expired");
        }

        if (user.getVerificationCode().equals(verifyUserDto.getVerificationCode())) {
            if (passwordEncoder.encode(oldPassword).equals(user.getPassword())) {
                throw new PasswordIncorrectException("Passwords do not match");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setVerificationCode(null);
            user.setVerificationCodeExpiresAt(null);
            userRepository.save(user);
            return usersMapper.mapToUserDto(user, new UserDto());
        } else {
            throw new VerificationCodeInvalid("Invalid verification Code");
        }
    }

    public VerifyUserDto sendEmail(String email, String event) {
        VerifyUserDto verifyUserDto = new VerifyUserDto();
        String verifyCode = generateVerificationCode();
        verifyUserDto.setEmail(email);
        verifyUserDto.setVerificationCode(verifyCode);

        if (event.equals("ForgotPassword")) {
            User user = userRepository.findByEmail(email).orElseThrow(
                    () -> new ResourceNotFoundException("User", "email" , email)
            );
            user.setVerificationCode(verifyCode);
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            userRepository.save(user);
            sendVerificationEmailForgotPassword(user);
        } else if (event.equals("VerifyUser")) {
            sendVerificationEmail(email, verifyCode);
        } else if (event.equals("ChangePassword")) {
            sendVerificationEmailChangePassword(email, verifyCode);
        }
        return verifyUserDto;
    }


    private void sendVerificationEmail(String email, String verifyCode) { //TODO: Update with company logo
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + verifyCode;
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(email, subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }

    private void sendVerificationEmailChangePassword(String email, String verifyCode) { //TODO: Update with company logo
        String subject = "Change Password";
        String verificationCode = "VERIFICATION CODE " + verifyCode;
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(email, subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }

    private void sendVerificationEmailForgotPassword(User user) { //TODO: Update with company logo
        String subject = "Verification Code";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Please using this code for verified your Email in app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }


    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}