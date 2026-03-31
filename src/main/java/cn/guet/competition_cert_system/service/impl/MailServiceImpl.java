package cn.guet.competition_cert_system.service.impl;

import cn.guet.competition_cert_system.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:qzkabcd@qq.com}")
    private String fromEmail;

    @Override
    public void sendRegisterCode(String to, String code) {
        String subject = "【竞赛证书系统】注册验证码";
        String content = buildCodeEmail("注册", code, "您正在注册竞赛证书管理系统，验证码 5 分钟内有效。");
        sendHtmlEmail(to, subject, content);
    }

    @Override
    public void sendResetPasswordCode(String to, String code) {
        String subject = "【竞赛证书系统】找回密码验证码";
        String content = buildCodeEmail("找回密码", code, "您正在找回竞赛证书管理系统密码，验证码 5 分钟内有效。");
        sendHtmlEmail(to, subject, content);
    }

    private String buildCodeEmail(String title, String code, String tip) {
        return "<div style='font-family: Arial, sans-serif; max-width: 500px; margin: 0 auto;'>" +
               "<h2 style='color: #333;'>" + title + "</h2>" +
               "<p style='color: #666; font-size: 16px;'>" + tip + "</p>" +
               "<div style='background: #f5f5f5; padding: 20px; text-align: center; margin: 20px 0;'>" +
               "<span style='font-size: 32px; letter-spacing: 8px; font-weight: bold; color: #333;'>" +
               code + "</span>" +
               "</div>" +
               "<p style='color: #999; font-size: 12px;'>如非本人操作，请忽略此邮件。</p>" +
               "</div>";
    }

    private void sendHtmlEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            log.info("邮件发送成功: to={}, subject={}", to, subject);
        } catch (MessagingException e) {
            log.error("邮件发送失败: to={}, error={}", to, e.getMessage());
            throw new RuntimeException("邮件发送失败", e);
        }
    }
}
