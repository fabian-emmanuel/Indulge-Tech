package com.indulgetech.services.system.mail.templateEngine;


import com.indulgetech.exceptions.TemplateEngineException;

import java.util.Map;

public interface TemplateEngine {

    public String processTemplateIntoString(String templateName, Map<String,String> templateTokens) throws TemplateEngineException;

}
