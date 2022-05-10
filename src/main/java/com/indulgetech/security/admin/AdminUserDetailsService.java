package com.indulgetech.security.admin;

import com.indulgetech.constants.Constants;
import com.indulgetech.exceptions.CustomException;
import com.indulgetech.models.users.UserStatus;
import com.indulgetech.models.users.admin.AdminUser;
import com.indulgetech.models.users.roles.Role;
import com.indulgetech.repositories.user.AdminUserRepository;
import com.indulgetech.services.user.admin.AdminUserService;
import com.indulgetech.services.user.admin.RoleService;
import constants.SchemaConstant;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static constants.SchemaConstant.*;


/**
 *
 */
@RequiredArgsConstructor
@Component("adminUserDetailsService")
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminUserRepository userRepository;
    private final AdminUserService adminUserService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Override
    @Cacheable("adminUserAuthInfo")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser user = userRepository.findAuthUserByEmail(username).orElseThrow(() -> new UsernameNotFoundException("No user found with username:" + username));
        return new AdminUserInfo(user);
    }

    public void createDefaultAdmin() throws ServiceException {
        if (this.adminUserService.findUserByEmail(DEFAULT_SUPER_ADMINISTRATOR_EMAIL).isEmpty()) {
            AdminUser user = new AdminUser();
            String password = passwordEncoder.encode(DEFAULT_SUPER_ADMINISTRATOR_DEFAULT_PASS);
            Role role = roleService.fetchByRoleKey(Constants.ROLE_SUPER_ADMIN).orElseThrow(() -> new CustomException.EntityNotFoundException("Resource not found for role with key:" + Constants.ROLE_SUPER_ADMIN));
            //creation of the super admin admin:password)
            user.setFirstName("Administrator");
            user.setLastName("SuperAdmin");
            user.setEmail(DEFAULT_SUPER_ADMINISTRATOR_EMAIL);
            user.setPassword(password);
            user.addRole(role);
            user.setStatus(UserStatus.ACTIVE);
            adminUserService.create(user);
        }
    }
}
