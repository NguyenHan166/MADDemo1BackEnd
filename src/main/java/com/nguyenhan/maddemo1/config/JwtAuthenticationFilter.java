package com.nguyenhan.maddemo1.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nguyenhan.maddemo1.constants.UsersConstants;
import com.nguyenhan.maddemo1.exception.TokenInvalidException;
import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.UserRepository;
import com.nguyenhan.maddemo1.responses.ErrorResponseDto;
import com.nguyenhan.maddemo1.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.rmi.server.LogStream.log;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver,
            UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);  // Extract token
            final String userEmail = jwtService.extractUsername(jwt);
            Optional<User> user = userRepository.findByEmail(userEmail);

            log.atInfo().log(userEmail);

            if (userEmail != null) {
                // Nếu token hết hạn, trả về lỗi
                if (jwtService.isTokenExpired(jwt)) {
                    sendErrorResponse(response, "Token is expired");
                    return;
                }

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                // Nếu chưa xác thực, xác thực lại token
                if (authentication == null || !authentication.getName().equals(userEmail)) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(user.get().getEmail());

                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        sendErrorResponse(response, "Token invalid");
                        return;
                    }
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            sendErrorResponse(response, "Token invalid");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String errorMessage) throws IOException {
        // Tạo ErrorResponseDto
        ErrorResponseDto errorResponseDTO = new ErrorResponseDto(
                "Request failed", // Mô tả yêu cầu, có thể lấy từ webRequest
                UsersConstants.STATUS_401,
                errorMessage,
                LocalDateTime.now()
        );

        // Cấu hình phản hồi JSON
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Đảm bảo JavaTimeModule được đăng ký
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // Tắt việc chuyển ngày thành timestamp

        // Cấu hình phản hồi JSON
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
        response.setContentType("application/json");

        // Chuyển đối tượng thành JSON và viết vào response
        response.getWriter().write(objectMapper.writeValueAsString(errorResponseDTO));
    }
}