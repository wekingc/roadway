package com.weking.core.services.impl;

import com.weking.core.services.interfaces.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author Jim Cen
 * @date 2020/7/21 13:05
 */
public class DefaultAlarmServiceImpl implements AlarmService {
    private final String MAIL_SEPARATOR = ",";
    @Autowired
    JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;
    @Value("${spring.mail.to}")
    private String to;
    @Override
    public void alarm(String subject, String body, boolean html) {
      try {
          MimeMessage mimeMessage = mailSender.createMimeMessage();
          MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
          helper.setFrom(from);
          String[] toList = to.split(MAIL_SEPARATOR);
          helper.setTo(toList);
          helper.setSubject(subject);
          helper.setText(body,html);
          mailSender.send(mimeMessage);
      }
      catch (Exception ex) {
          ex.printStackTrace();
      }
    }
}
