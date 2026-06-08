package com.example.the_cheaper.external;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[The Cheaper] Mã xác thực đặt lại mật khẩu");
        message.setText(
                "Xin chào,\n\n" +
                        "Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.\n\n" +
                        "Mã OTP của bạn là: " + otp + "\n\n" +
                        "Mã này có hiệu lực trong 15 phút.\n" +
                        "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
                        "Trân trọng,\nThe Cheaper Team");
        mailSender.send(message);
    }
}
