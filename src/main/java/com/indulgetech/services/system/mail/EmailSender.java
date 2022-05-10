package com.indulgetech.services.system.mail;

public interface EmailSender {
  
  void send(final Email email);

  void setEmailConfig(EmailConfig emailConfig);

}
