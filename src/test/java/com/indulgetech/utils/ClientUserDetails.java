package com.indulgetech.utils;


import com.indulgetech.constants.Constants;
import com.indulgetech.models.users.UserStatus;
import com.indulgetech.models.users.UserType;
import com.indulgetech.models.users.client.ClientUser;
import com.indulgetech.models.users.permissions.Permission;
import com.indulgetech.models.users.roles.Role;
import com.indulgetech.models.users.roles.RoleType;
import com.indulgetech.repositories.role.RoleRepository;
import com.indulgetech.repositories.user.ClientUserRepository;
import com.indulgetech.security.AuthTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class ClientUserDetails {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthTokenProvider tokenProvider;

  @Autowired
  private ClientUserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  private ClientUser user;

  public Role getRole() {
    Role role = new Role();
    role.setRoleKey(Constants.ROLE_CLIENT_MEMBER);
    role.setName("Administrator");
    role.setRoleType(RoleType.CLIENT);
    return roleRepository.save(role);
  }

  public Permission getPermission() {
    Permission permission = new Permission();
    return permission;
  }


  public ClientUser getUser(String email, String phone, Role role) {
    if(role==null){
      role=this.getRole();
    }
    user = new ClientUser();
    user.setLastName("zee");
    user.setFirstName("Dewale");
    user.addRole(role);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode("password"));
    user.setStatus(UserStatus.ACTIVE);
//    user.setOrganization(organization);
    user.setTelephoneNumber(phone);
//    user.setOrganizationRole("HR");
    userRepository.save(user);
    return user;
  }


  @Transactional
  public String getLoggedInToken(String email,String phone) {
    this.getUser(email,phone, null);
    return tokenProvider.generateToken(email, UserType.CLIENT.name());
  }

  @Transactional
  public String getLoggedInToken(String email,String phone, Role role) {
    this.getUser(email,phone,role);
    return tokenProvider.generateToken(email, UserType.CLIENT.name());
  }

  public ClientUser getUser() {
    return user;
  }
}