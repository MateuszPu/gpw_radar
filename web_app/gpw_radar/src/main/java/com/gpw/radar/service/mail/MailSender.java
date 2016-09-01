package com.gpw.radar.service.mail;

import com.gpw.radar.domain.User;

public interface MailSender {
    void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml);

    void sendActivationEmail(User user, String baseUrl);

    void sendPasswordResetMail(User user, String baseUrl);
}
