package com.example.spirituality_be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @org.springframework.beans.factory.annotation.Value("${spring.mail.username}")
    private String fromEmail;

    public void sendResetPasswordEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("[HUYỀN MỆNH] Khôi phục mật khẩu linh hồn của bạn");
        message.setText("Chào bạn,\n\n" +
                "Vũ trụ đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.\n" +
                "Vui lòng nhấn vào liên kết bên dưới để thiết lập chìa khóa mới (Liên kết có hiệu lực trong 15 phút):\n\n" +
                resetLink + "\n\n" +
                "Nếu bạn không yêu cầu điều này, hãy bỏ qua email này.\n\n" +
                "Ánh sáng và tình yêu,\n" +
                "Đội ngũ Huyền Mệnh");

        mailSender.send(message);
    }

    public void sendPasswordChangedNotification(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("[HUYỀN MỆNH] Thông báo thay đổi mật khẩu thành công");
        message.setText("Chào bạn,\n\n" +
                "Mật khẩu cho tài khoản của bạn tại Huyền Mệnh đã được thay đổi thành công.\n" +
                "Nếu bạn không thực hiện thay đổi này, vui lòng liên hệ với chúng tôi ngay lập tức để bảo vệ hành trình của mình.\n\n" +
                "Trân trọng,\n" +
                "Hệ thống Huyền Mệnh");

        mailSender.send(message);
    }
}