package com.indulgetech.services.system.mail;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Email implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6481794982612826257L;
	private String from;
	private String fromEmail;
	private String to;
	private String subject;
	private String templateName;
	private Map<String,String> templateTokens = new HashMap<String,String>();
	private String body;

}
