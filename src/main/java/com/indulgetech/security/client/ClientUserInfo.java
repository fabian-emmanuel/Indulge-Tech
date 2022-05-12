package com.indulgetech.security.client;


import com.indulgetech.models.users.UserStatus;
import com.indulgetech.models.users.client.ClientUser;
import com.indulgetech.utils.AuthUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClientUserInfo implements UserDetails {

    private ClientUser user;

    public ClientUserInfo() {
    }

    public ClientUserInfo(ClientUser user) {
        this.user = user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getUserAuthorities();
    }

    private Collection<? extends GrantedAuthority> getUserAuthorities() {
        return AuthUtils.getUserAuthorities(this.user.getRoles());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
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


    @Override
    public String toString() {
        return "UserInfo{" +
                "userName=" + this.getUsername() +
                ",isEnabled=" + this.isEnabled() +
                "user=" + user +
                '}';
    }


    public ClientUser getUser() {
        return user;
    }
}