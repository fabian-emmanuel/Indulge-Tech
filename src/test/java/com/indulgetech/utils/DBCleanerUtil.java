package com.indulgetech.utils;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static constants.SchemaConstant.*;


@Component
public class DBCleanerUtil {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void cleanDb(){
        clearDb(jdbcTemplate);
    }

    public static void clearDb(JdbcTemplate jdbcTemplate){

        List<String> tables = new ArrayList<>();
        tables.add(TABLE_ADMIN_LOGIN_HISTORY);
        tables.add(TABLE_ADMIN_USER_ROLE);
        tables.add(TABLE_ADMIN_USER);
        tables.add(TABLE_CLIENT_LOGIN_HISTORY);
        tables.add(TABLE_CUSTOMER_REFRESH_TOKEN);
        tables.add(TABLE_CLIENT_USER_ROLE);
        tables.add(TABLE_CLIENT_USERS);
        tables.add(TABLE_ROLE_PERMISSIONS);
        tables.add(TABLE_PERMISSIONS);
        tables.add(TABLE_ROLES);
        tables.add(TABLE_SYSTEM_CONFIGURATION);
        tables.add(TABLE_TOKEN_BLACKLIST);

        String[] excludes={};
        List<String> excludesList = Arrays.asList(excludes);
        if (CollectionUtils.isNotEmpty(excludesList)) {
            tables.removeAll(excludesList);
        }

        String[] tablesArr = new String[tables.size()];
        tablesArr = tables.toArray(tablesArr);

        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                tablesArr);
    }

    public static void clearTables(JdbcTemplate jdbcTemplate, String... tables) {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, tables);
    }
}
