package com.indulgetech.utils.extensions;

import com.indulgetech.utils.DBCleanerUtil;
import org.junit.jupiter.api.extension.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DBCleanerExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {


    @Override
    public void afterAll(ExtensionContext extensionContext) {
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
     
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {

    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        if(!extensionContext.getTags().contains("parameterized")){
            DBCleanerUtil.clearDb(SpringExtension.getApplicationContext(extensionContext).getBean(JdbcTemplate.class));
        }
    }

}
