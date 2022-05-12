package com.indulgetech.utils;

import com.indulgetech.security.admin.AdminUserDetailsService;
import com.indulgetech.services.init.InitializationDatabase;
import constants.SchemaConstant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static constants.SchemaConstant.*;

/**
 * Class is primarily used for testing.
 * Class functionalities are the same with real InitializationLoader class of the
 * application. The difference being that the test method in the real class is annotated with @PostConstruct
 * that runs as part of the bean initialization which is not suitable for testing.
 */
@Component
@Slf4j
public class InitializationLoaderTestComponent {

    @Value("${db.init.data:true}")
    private boolean initDefaultData;

    @Autowired
    private InitializationDatabase initializationDatabase;

    @Autowired
    protected AdminUserDetailsService adminUserDetailsService;

    public void init() {

        try {
            //Check flag to populate or not the database
            if (!this.initDefaultData) {
                return;
            }

            if (initializationDatabase.isEmpty()) {

                //All default data to be created
                log.info(String.format("%s : "+ DEFAULT_APP_NAME +" database is empty, populate it....", "indulge"));

                initializationDatabase.populate(DEFAULT_APP_NAME);
            }

        } catch (Exception e) {
            log.error("Error in the init method", e);
        }

    }


}
