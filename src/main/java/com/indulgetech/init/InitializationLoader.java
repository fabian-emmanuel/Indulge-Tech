package com.indulgetech.init;


import com.indulgetech.security.admin.AdminUserDetailsService;
import com.indulgetech.services.init.InitializationDatabase;
import constants.SchemaConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
@Profile("!test")
@Slf4j
public class InitializationLoader {


    @Value("${db.init.data:true}")
    private boolean initDefaultData;


    @Autowired
    private InitializationDatabase initializationDatabase;

    @Autowired
    protected AdminUserDetailsService adminUserDetailsService;



    @PostConstruct
    //@Transactional
    public void init() {

        try {

            //Check flag to populate or not the database
            if (!this.initDefaultData) {
                //return;
            }

           // if (initializationDatabase.isEmpty()) {

                //All default data to be created
                log.info(String.format("%s : Indulge database is empty, populate it....", "indulge"));

                initializationDatabase.populate(SchemaConstant.DEFAULT_APP_NAME);

           // }

        } catch (Exception e) {
            log.error("Error in the init method", e);
        }

    }


}
