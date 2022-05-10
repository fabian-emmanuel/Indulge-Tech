package com.indulgetech.utils;


import com.indulgetech.models.users.UserStatus;
import com.indulgetech.models.users.admin.AdminUser;
import com.indulgetech.models.users.client.ClientUser;
import com.indulgetech.models.users.permissions.Permission;
import com.indulgetech.models.users.roles.Role;
import com.indulgetech.models.users.roles.RoleType;
import com.indulgetech.security.admin.AdminUserInfo;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.util.Date;

public class TestModels {

    public static AdminUser adminUser(String firstName, String lastName, String middleName, String email, String password, String phoneNo) {
        AdminUser user = new AdminUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setStatus(UserStatus.ACTIVE);
        user.setTelephoneNumber(phoneNo);
        return user;
    }






    public static Role role(String name, RoleType roleType) {
        Role role = new Role();
        role.setName(name);
        //role.setRoleKey(roleType.name());
        role.setRoleType(roleType);
        return role;
    }

    public static Permission permission(String name) {
        Permission permission = new Permission();
        permission.setName(name);
        return permission;
    }

    public static ClientUser organizationUser(String firstName, String lastName, String middleName, String email, String password, String phoneNo) {
        ClientUser user = new ClientUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setStatus(UserStatus.ACTIVE);
        user.setTelephoneNumber(phoneNo);
        return user;
    }

    public static AdminUserInfo adminUserInfo() {

        AdminUser user = new AdminUser();
        user.setId(1l);
        user.setLastName("Dee");
        user.setFirstName("Dewale");
        user.setEmail("clientadmin@gmail.com");
        user.setPassword("password");
        user.setStatus(UserStatus.ACTIVE);

        return new AdminUserInfo(user);
    }
}
