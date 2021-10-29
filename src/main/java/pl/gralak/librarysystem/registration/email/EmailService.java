package pl.gralak.librarysystem.registration.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService
{
    private final JavaMailSender javaMailSender;

    @Async
    public void send(String to, String email)
    {
        try
        {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setSubject("Confirm your email");
            helper.setFrom("libraryApp@gmail.com");
            helper.setTo(to);
            helper.setText(email, true);
            javaMailSender.send(mimeMessage);

        } catch(MessagingException e)
        {
            throw new IllegalStateException("Failed to send email");
        }

    }
}
