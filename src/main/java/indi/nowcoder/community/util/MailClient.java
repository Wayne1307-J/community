package indi.nowcoder.community.util;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
@Component
@Slf4j
public class MailClient {
    @Autowired
    private JavaMailSender mailSender; // MailSender是一个邮件发送的接口，其中定义两个发送邮件的方法。JavaMailSender继承自MailSender接口，提供了更多的邮件发送方法。
    @Value("${spring.mail.username}")
    private String from;
    public void sendMail(String to, String subject, String content){
        try {
            MimeMessage message =mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); //使用true，表明支持html文本
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e){
            log.error("发送邮件失败："+e.getMessage());
        }
    }
}
