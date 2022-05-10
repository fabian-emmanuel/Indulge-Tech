package com.indulgetech.utils;

import com.indulgetech.exceptions.UnAuthorizedException;
import com.indulgetech.models.users.client.ClientUser;
import com.indulgetech.security.admin.AdminUserInfo;
import com.indulgetech.security.client.ClientUserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserInfoUtil {

    public UserDetails authenticatedUserInfo() {

        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null) {
            Authentication auth = securityContext.getAuthentication();
            if (auth != null) {
                return (UserDetails) auth.getPrincipal();
            }
        }
        return null;
    }

    public ClientUser authenticatedUserOrganisation() {

        UserDetails userDetails = this.authenticatedUserInfo();
        if (userDetails instanceof ClientUserInfo authenticatedUserInfo) {
            return authenticatedUserInfo.getUser();
        }
        throw new UnAuthorizedException("Authenticated user info not found");
    }

    public AdminUserInfo authenticatedAdminUserInfo() {
        UserDetails userDetails = this.authenticatedUserInfo();
        return userDetails != null ? (AdminUserInfo) userDetails : null;
    }

    public boolean isClient(){
        return this.authenticatedUserInfo() instanceof ClientUserInfo;
    }

    public boolean isAdmin() {
        return this.authenticatedUserInfo() instanceof AdminUserInfo;
    }
}
