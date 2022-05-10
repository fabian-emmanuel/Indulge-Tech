package com.indulgetech.security.admin;


import com.indulgetech.constants.Constants;
import com.indulgetech.models.users.UserStatus;
import com.indulgetech.models.users.admin.AdminUser;
import com.indulgetech.models.users.roles.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class AdminUserInfo implements UserDetails {

    private AdminUser user;
    public AdminUserInfo() {
    }

    public AdminUserInfo(AdminUser user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getUserAuthorities();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public Long getUserId() {
        return user.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus().equals(UserStatus.ACTIVE);
    }

    private Collection<? extends GrantedAuthority> getUserAuthorities() {
        return getGrantedAuthorities(getPrivileges(this.user.getRoles()));
    }

    private Collection<String> getPrivileges(Collection<Role> roles) {
        Collection<String> privileges = new HashSet<>();
        roles.forEach((role) -> {
            privileges.add(Constants.ROLE_PREFIX +role.getRoleKey());
            role.getPermissions().forEach(perm -> privileges.add(perm.getPermission()));
        });
        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(Collection<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        privileges.forEach(privilege -> authorities.add(new SimpleGrantedAuthority(privilege)));
        return authorities;
    }


    @Override
    public String toString() {
        return "UserInfo{" +
                "userName=" + this.getUsername() +
                ",isEnabled=" + this.isEnabled() +
                "user=" + user +
                '}';
    }
}