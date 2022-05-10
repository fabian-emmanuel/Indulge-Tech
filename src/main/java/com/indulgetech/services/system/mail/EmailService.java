package com.indulgetech.services.system.mail;

public interface EmailService {

    void sendEmail(Email email);

    void sendAsyncEmail(Email email);

}
