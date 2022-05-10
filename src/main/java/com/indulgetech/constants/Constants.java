package com.indulgetech.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class Constants {

    public static final Charset UTF_8 = StandardCharsets.UTF_8;

    public static final String TEST_ENVIRONMENT = "TEST";
    public static final String PRODUCTION_ENVIRONMENT = "PROD";

    public static final String UNDERSCORE = "_";
    public static final String SLASH = "/";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static final String ALPHANUMERIC = "\"^[A-Za-z0-9]+$\\n\"";

    public static final String AUTHORITIES_CLAIM_KEY = "authorities";

    public static final String ATTR_VAL_KEY_SEP = "_";
    public static final String AUTHORITIES_CLAIM_KEY_SEP = ",";

    public static final String ROLE_SUPER_ADMIN = "SUPERADMIN";
    public static final String ROLE_ADMIN_MEMBER = "ROLEADMINMEMBER" ;
    public static final String ROLE_CLIENT_MEMBER = "ROLECLIENTMEMBER";


    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ADMIN_URI = "/admin-users";
    public static final String CLIENT_URI = "/client-users";
    public static final String SPECIFICATION_KEY_SEP = ",";
    public static final String PWORD_RST_TKN_PATTERN = "^[A-Za-z0-9]*$";
    public static final String USER_TYPE="user_type";

    //create pasword
    public static final String PASSWORD_URL = "/createpassword";
    public static final String PASSWORD_URL_TOKEN_PARAM = "token=";
    public static final String CREATE_PASSWORD_URL = "CREATE_PASSWORD_URL";

    public static final String ENGINEER_PROFILE_PIC_DIR = "avatars";
    public static final String LOGO_DIR = "logo";
    public static final String REPORTS_DIR = "reports";
    public static final String INFINITE_DATE = "9999-12-30";
    public static final String PASSWORD_RESET_URL = "/resetpassword";


    public static final String SUBSCRIPTION_RECEIPTS_DIR = "subscription/receipts";
    public static final String SUBSCRIPTION_CONTRACTS_DIR = "subscription/contracts";
    public static final int AVATAR_THUMBNAIL_WIDTH = 50;
    public static final int AVATAR_THUMBNAIL_HEIGHT = 50;
    public static final String ROLE_TYPE_ADMIN = "admin";
    public static final String ROLE_TYPE_CLIENT = "client";

    public static final String PROFILE_PIC_DIR = "profilePics";
    public static final String DOCUMENT_DIR = "docs";
}
