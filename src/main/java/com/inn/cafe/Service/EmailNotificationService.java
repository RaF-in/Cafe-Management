package com.inn.cafe.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class EmailNotificationService  {

    @Autowired
    private JavaMailSender mailSender;

    public boolean sendSimpleEmail(String toEmail, String subject, String body, List<String> ccList) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("rafin.neaz@gmail.com");
        if (ccList != null && ccList.size() > 0) {
            message.setCc(getCcList(ccList));
        }
        try {
            mailSender.send(message);
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new Exception(ex);
        }
        return true;
    }

    private String[] getCcList(List<String> ccList) {
        String[] newCcList = new String[ccList.size()];
        for (int i = 0; i < ccList.size(); i ++) {
            newCcList[i] = ccList.get(i);
        }
        return newCcList;
    }
}
