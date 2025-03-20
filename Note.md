### Sửa lại

- Phần login bằng google:
  - Bên UI sẽ gửi về email, username, photoUrl(dùng cloud), mobile phone 
  - Server sẽ nhận và check exception, nếu có rồi sẽ trả luôn về token
  - Thêm phần forgot password
- Sửa lại các response:
  - Response Login trả về token thôi 
- Sửa lại các exception, response exception (Phần logic đăng kí, các hoặt động trả ra exception)
- Đang bị lỗi phần signup, 