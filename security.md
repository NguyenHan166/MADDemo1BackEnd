# Luồng Đăng Nhập và Các Chức Năng Liên Quan

## 1. Giới Thiệu
Hệ thống hỗ trợ đăng nhập người dùng qua email và mật khẩu. Sau khi người dùng đăng nhập thành công, hệ thống tạo một **JWT token** (JSON Web Token) để sử dụng cho các yêu cầu bảo mật tiếp theo. Dưới đây là mô tả chi tiết về luồng đăng nhập và các chức năng liên quan.

## 2. Luồng Đăng Nhập

### Bước 1: Người dùng gửi yêu cầu đăng nhập
- Người dùng gửi một yêu cầu HTTP POST đến endpoint `/login` với dữ liệu đăng nhập (email và mật khẩu).

**Ví dụ yêu cầu POST**:
```json
{
    "email": "user@example.com",
    "password": "password123"
}

```

### Bước 2:
- Controller nhận yêu cầu và gọi phương thức authenticate() của authenticationService để xác thực người dùng.

```java
@PostMapping("/login")
public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
    User authenticatedUser = authenticationService.authenticate(loginUserDto);
    String jwtToken = jwtService.generateToken(authenticatedUser);
    LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
    return ResponseEntity.ok(loginResponse);
}
```

### Bước 3:
- Trong authenticationService, người dùng được tìm kiếm trong cơ sở dữ liệu qua email.
- Nếu người dùng không tồn tại, hệ thống ném ra ngoại lệ "User not found".
- Kiểm tra xem tài khoản có được kích hoạt (enabled) hay không. Nếu chưa, ném ra ngoại lệ "Account not verified".

### Bước 4: Kiểm tra thông tin đăng nhập
- Nếu tài khoản hợp lệ, hệ thống sử dụng authenticationManager.authenticate() để xác thực thông tin đăng nhập (email và mật khẩu).
- Nếu thông tin đăng nhập không chính xác, một ngoại lệ sẽ được ném ra và trả về thông báo lỗi.

### Bước 5: Tạo JWT Token
- Nếu xác thực thành công, hệ thống sử dụng jwtService.generateToken(user) để tạo một JWT token cho người dùng.
- JWT token chứa thông tin người dùng và thời gian hết hạn của token.

### Bước 6: Trả về token cho người dùng
- Sau khi JWT được tạo, hệ thống trả về LoginResponse chứa token và thời gian hết hạn.

```json
{
  "token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYW5kenZsIiwiaWF0IjoxNzQyMDY1MDAzLCJleHAiOjE3NDIwNjUwNjN9.FSlWpP0baUZOLQK722StEAvvz0jZpuxjG3El_fH726w",
  "expiresIn":60000
}
```

## 3. Các điểu cần lưu ý:

- Cấu hình đúng JwtAuthenticationFilter: Kiểm tra token trước và thả ra ngoại lệ nếu cần thiết
  - Luồng hoạt động trong filter:  

```text
### Giải thích chi tiết:

#### 1. **Nhận yêu cầu HTTP**
- Mỗi khi một yêu cầu HTTP đến server, filter sẽ được gọi và xử lý yêu cầu đó.
- `authHeader` được lấy từ header của yêu cầu `Authorization`.
  ```java
  final String authHeader = request.getHeader("Authorization");
  ```
Nếu header không tồn tại hoặc không bắt đầu bằng `Bearer`, filter sẽ bỏ qua và tiếp tục với chuỗi filter tiếp theo.

   ```java
   if (authHeader == null || !authHeader.startsWith("Bearer ")) {
       filterChain.doFilter(request, response);
       return;
   }
   ```

#### 2. **Trích xuất Token từ Header**
- Token được trích xuất từ header `Authorization` sau từ "Bearer ".
  ```java
  final String jwt = authHeader.substring(7);  // Extract token
  ```

#### 3. **Lấy tên người dùng (username) từ token**
- Dùng `jwtService` để giải mã và trích xuất thông tin người dùng (username) từ token.
  ```java
  final String username = jwtService.extractUsername(jwt);
  ```

#### 4. **Kiểm tra xem người dùng có tồn tại trong cơ sở dữ liệu không**
- Tìm người dùng trong cơ sở dữ liệu bằng `username` để xác thực.
  ```java
  Optional<User> user = userRepository.findByUsername(username);
  ```

#### 5. **Kiểm tra xem token có hết hạn không**
- Nếu token hết hạn, hệ thống sẽ trả về mã lỗi `401 Unauthorized` với thông báo "Token has expired".
  ```java
  if (jwtService.isTokenExpired(jwt)) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
      response.getWriter().write("Token has expired");
      return;
  }
  ```

#### 6. **Xác thực thông tin người dùng**
- Nếu người dùng chưa được xác thực (`authentication == null`) hoặc tên người dùng trong `SecurityContext` không khớp với token, thì tiến hành xác thực lại token.
- Sau khi xác thực token thành công, một `UsernamePasswordAuthenticationToken` mới được tạo và đặt vào `SecurityContextHolder` để Spring Security nhận diện người dùng hiện tại.
  ```java
  if (authentication == null || !authentication.getName().equals(username)) {
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
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
          response.getWriter().write("Invalid token");
          return;
      }
  }
  ```

#### 7. **Tiếp tục chuỗi filter**
- Nếu không có lỗi xảy ra, yêu cầu sẽ tiếp tục đi qua các filter tiếp theo trong chuỗi của Spring Security.
  ```java
  filterChain.doFilter(request, response);
  ```

#### 8. **Xử lý ngoại lệ**
- Nếu có bất kỳ lỗi nào trong quá trình xử lý, chẳng hạn như token không hợp lệ hoặc token không thể giải mã, hệ thống sẽ trả về mã lỗi `401 Unauthorized` và thông báo lỗi.
  ```java
  response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
  response.getWriter().write("Invalid token: " + exception.getMessage());
  handlerExceptionResolver.resolveException(request, response, null, exception);
  ```

