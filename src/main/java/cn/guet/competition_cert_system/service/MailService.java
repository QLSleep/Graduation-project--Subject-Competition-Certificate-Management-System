package cn.guet.competition_cert_system.service;

/**
 * 邮件服务接口
 */
public interface MailService {

    /**
     * 发送注册验证码
     * @param to 收件人邮箱
     * @param code 验证码
     */
    void sendRegisterCode(String to, String code);

    /**
     * 发送找回密码验证码
     * @param to 收件人邮箱
     * @param code 验证码
     */
    void sendResetPasswordCode(String to, String code);
}
