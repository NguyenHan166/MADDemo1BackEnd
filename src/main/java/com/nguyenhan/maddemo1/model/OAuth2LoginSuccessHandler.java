package com.nguyenhan.maddemo1.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nguyenhan.maddemo1.repository.UserRepository;
import com.nguyenhan.maddemo1.service.AuthenticationService;
import com.nguyenhan.maddemo1.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Random;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtService jwtService; // Lớp này cần tạo để cấp JWT

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
        String token = oauthUser.getToken();
        oauthUser.getUser().setVerificationCode(generateVerificationCode());
        oauthUser.getUser().setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));

        // Trả token về client
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"token\": \"" + token + "\"}");
        response.getWriter().flush();
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
