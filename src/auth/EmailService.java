package auth;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailService {

    public void sendVerificationCode(String email, String code) {

        String gmailAddress = System.getenv("GMAIL_ADDRESS");
        String gmailPassword = System.getenv("GMAIL_APP_PASSWORD");

        if(gmailAddress == null || gmailAddress.isBlank()
                || gmailPassword == null || gmailPassword.isBlank()) {
            throw new IllegalStateException("GMAIL SMTP configuration is missing");
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(gmailAddress, gmailPassword);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(gmailAddress));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(email)
            );
            message.setSubject("SecureLogin - Verification Code", "UTF-8");

            message.setText(
                    "Your verification code: " + code,
                    "UTF-8"
            );
            Transport.send(message);

        } catch(MessagingException e) {
            throw new IllegalStateException("Could not send verification email",e);
        }

    }
}
