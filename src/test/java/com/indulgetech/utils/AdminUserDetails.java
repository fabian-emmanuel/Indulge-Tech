package com.indulgetech.utils;


//import com.indulgetech.configurations.PasswordEncoder;
import com.indulgetech.constants.Constants;
import com.indulgetech.models.users.UserStatus;
import com.indulgetech.models.users.UserType;
import com.indulgetech.models.users.admin.AdminUser;
import com.indulgetech.models.users.permissions.Permission;
import com.indulgetech.models.users.roles.Role;
import com.indulgetech.models.users.roles.RoleType;
import com.indulgetech.repositories.role.RoleRepository;
import com.indulgetech.repositories.user.AdminUserRepository;
import com.indulgetech.security.AuthTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;


@Component
public class AdminUserDetails {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthTokenProvider tokenProvider;

  @Autowired
  private AdminUserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  private AdminUser user;

  public Role getRole() {
    Role role = new Role();
    role.setRoleKey(Constants.ROLE_SUPER_ADMIN);
    role.setName("Administrator");
    role.setRoleType(RoleType.ADMIN);
    return roleRepository.save(role);
  }

  public Permission getPermission() {
    Permission permission = new Permission();
    return permission;
  }


  public AdminUser getUser(String email, String phone, Role role) {

    if(role==null){
      role=this.getRole();
    }
    user = new AdminUser();
    user.setLastName("zee");
    user.setFirstName("Dewale");
    user.addRole(role);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode("password"));
    user.setStatus(UserStatus.ACTIVE);
    user.setTelephoneNumber(phone);
    userRepository.save(user);
    return user;
  }

  @Transactional
  public String getLoggedInToken(String email,String phone) {
    if(this.userRepository.findByEmail(email).isEmpty()){
      this.getUser(email,phone,null);
    }
    return tokenProvider.generateToken(email, UserType.ADMIN.name());
  }

  @Transactional
  public String getLoggedInToken(String email,String phone,Role role) {
    this.roleRepository.save(role);
    this.getUser(email,phone,role);
    return tokenProvider.generateToken(email, UserType.ADMIN.name());
  }


  public AdminUser getUser() {
    return user;
  }
}