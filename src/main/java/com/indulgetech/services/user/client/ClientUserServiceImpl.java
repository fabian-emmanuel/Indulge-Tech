package com.indulgetech.services.user.client;


import com.indulgetech.constants.Constants;
import com.indulgetech.dto.auth.ResetPasswordDto;
import com.indulgetech.dto.user.ChangePasswordDto;
import com.indulgetech.dto.user.ListUserDto;
import com.indulgetech.dto.user.UpdateUserProfileDto;
import com.indulgetech.dto.user.client.*;
import com.indulgetech.dto.user.role.ListRoleDto;
import com.indulgetech.exceptions.DuplicateEntityException;
import com.indulgetech.exceptions.EntityType;
import com.indulgetech.exceptions.InvalidRequestException;
import com.indulgetech.exceptions.ResourceNotFoundException;
import com.indulgetech.models.users.User;
import com.indulgetech.models.users.UserStatus;
import com.indulgetech.models.users.client.ClientUser;
import com.indulgetech.models.users.roles.Role;
import com.indulgetech.models.users.roles.RoleType;
import com.indulgetech.repositories.user.ClientUserRepository;
import com.indulgetech.services.generic.BaseEntityServiceImpl;
import com.indulgetech.services.storage.FileStorageService;
import com.indulgetech.services.system.SystemConfigurationService;
import com.indulgetech.services.user.admin.RoleService;
import com.indulgetech.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.File;
import java.util.*;

import static com.indulgetech.exceptions.EntityType.*;
import static com.indulgetech.models.users.UserStatus.ACTIVE;
import static com.indulgetech.models.users.UserType.*;
import static com.indulgetech.utils.CustomDateUtils.*;
import static constants.SchemaConstant.*;


@Service("clientUserServiceImpl")
@Slf4j
@RequiredArgsConstructor
public class ClientUserServiceImpl extends BaseEntityServiceImpl<Long, ClientUser> implements ClientUserService {

    private final ClientUserRepository repository;
    private final RoleService roleService;
    private final EmailTemplateUtils emailTemplateUtils;
    private final SystemConfigurationService systemConfigurationService;
    private final PasswordEncoder passwordEncoder;
    private final UserInfoUtil userInfoUtil;
    private final EntityManager entityManager;
    private final FileStorageService fileStorageService;
    @Value("${api.url-domain}")
    private String urlDomain;




    @Override
    @Transactional
    public ClientUser createClientUser(ClientUserRequestDto clientUserRequestDto) {
        if (this.userEmailTaken(clientUserRequestDto.getEmail())) {
            throw new DuplicateEntityException(CLIENT_USER, clientUserRequestDto.getEmail());
        }
        ClientUser clientUser = this.createUserModel(clientUserRequestDto);
////        clientUser.setRole(CLIENT_USER);
//        Collection<Role> roles = this.getRoleEntitiesFromIds(List.of(3));
//        if (CollectionUtils.isNotEmpty(roles)) {
//            this.addUserRoles(clientUser, roles);
//        }
        return this.createClientUser(clientUser, clientUserRequestDto.getPassword());
    }


    /**
     * Create organisation admin when selected role contains ADMIN rple
     *
     * @param clientUser
     * @return
     */
    private ClientUser createClientUser(ClientUser clientUser, String password) {
        this.createUserPassword(clientUser, password);
//        this.sendCreateClientUserEmail(clientUser, plainPassword);
        return this.saveUser(clientUser);
    }


    private boolean userEmailTaken(String email) {
        return this.repository.existsByEmail(email);
    }

    private boolean userEmailTaken(String email, Long id) {
        return repository.existsByEmailAndIdNot(email, id);
    }


    private void createPasswordCreationToken(ClientUser organizationUser) {
        String passwordSettingToken = this.getPasswordSettingToken();
        organizationUser.setPasswordResetToken(passwordSettingToken);
        String valdtyTrm = this.systemConfigurationService.findConfigValueByKey(PSSWORD_SETTING_TOKEN_VLDTY_TRM);
        organizationUser.calculateTokenExpiryDate(valdtyTrm);
    }

    private void setDefaultPasswordValidityTerm(ClientUser clientUser, String valdtyTrm) {
        log.warn("Found invalid password setting token validity term:'" + valdtyTrm + "' defined in configuration. Using application defined password setting token validity:" + DEFAULT_PWRD_SETTING_VLDTY_TRM + " as a fallback");
        clientUser.setPasswordResetValidityTerm(DateUtils.addHours(now(), DEFAULT_PWRD_SETTING_VLDTY_TRM));
    }

    @Override
    public Page<ListUserDto> listUsers(Pageable pageable) {
        return this.repository.listUsers(PageUtils.syncPageRequest(pageable));
    }

    @Override
    public ClientUserDetailResponseDto fetchUserDetail(Long userId) {
        Optional<ClientUser> clientUser = this.repository.findClientUserDetail(userId);
        if (clientUser.isEmpty()) {
            throw new ResourceNotFoundException(CLIENT_USER, String.valueOf(userId));
        }
        ClientUserDetailDto clientUserDetailDto = this.convertToDetailDto(clientUser.get());
        Collection<ListRoleDto> roles = this.roleService.listRolesForClient();
        return createUserDetailResponseDto(clientUserDetailDto, roles);
    }

    private ClientUserDetailDto convertToDetailDto(ClientUser user) {
        ClientUserDetailDto clientUserDetailDto = new ClientUserDetailDto();
        clientUserDetailDto.setUserId(user.getId());
        clientUserDetailDto.setEmail(user.getEmail());
        clientUserDetailDto.setFirstName(user.getFirstName());
        clientUserDetailDto.setLastName(user.getLastName());
        clientUserDetailDto.setPhone(user.getTelephoneNumber());
        clientUserDetailDto.setStatus(user.getStatus().name());
//        clientUserDetailDto.setRoles(user.getRoles().stream().map(Role::getId).toList());
        return clientUserDetailDto;
    }


    private ClientUserDetailResponseDto createUserDetailResponseDto(ClientUserDetailDto detailDto, Collection<ListRoleDto> roles) {
        ClientUserDetailResponseDto userDetailResponseDto = new ClientUserDetailResponseDto();
        userDetailResponseDto.setClientUserDetailDto(detailDto);
        Map<String, Object> extras = new HashMap<>();
        extras.put("roles", roles);
        extras.put("status", UserStatus.toItemList());
        userDetailResponseDto.setExtras(extras);
        return userDetailResponseDto;
    }

    @Override
    @Transactional
    public ClientUser updateUser(Long userId, ClientUserRequestDto clientUserRequestDto) {
        Optional<ClientUser> clientUser = this.repository.findById(userId);
        if (clientUser.isEmpty()) {
            throw new ResourceNotFoundException(CLIENT_USER, String.valueOf(userId));
        }
        this.checkUniqueUserEmailIfEmailHasChanged(clientUserRequestDto, clientUser.get());
        this.mapUserDtoToModelEntity(clientUserRequestDto, clientUser.get());
//        this.updateUserRoles(clientUser.get(), clientUserRequestDto.getRole());
        return this.updateUser(clientUser.get());
    }

//    private void updateUserRoles(ClientUser user, Collection<Integer> reqRoles) {
//        this.removeAllUserRoles(user);
//        Collection<Role> roles = this.getRoleEntitiesFromIds(reqRoles);
//        if (CollectionUtils.isNotEmpty(roles)) {
//            this.addUserRoles(user, roles);
//        }
//    }

    private Collection<Role> getRoleEntitiesFromIds(Collection<Integer> reqRoles) {
        Collection<Role> roles;
        if (CollectionUtils.isNotEmpty(reqRoles)) {
            roles = this.roleService.getRoleByIds(reqRoles);
        } else {
            Optional<Role> clientMemberRole = this.roleService.fetchByRoleKey(Constants.ROLE_CLIENT_MEMBER);
            if (clientMemberRole.isEmpty()) {
                log.error("Client Member role not defined by system");
                throw new ResourceNotFoundException("Resource not found");
            }
            roles = List.of(clientMemberRole.get());
        }
        return roles;
    }

//    private void removeAllUserRoles(ClientUser user) {
//        Collection<Role> definedRoles = user.getRoles();
//        if (!CollectionUtils.isEmpty(definedRoles)) {
//            //to avoid concurrent modification two loops are used TODO: optimize this to use one loop instead
//            Collection<Role> toRemove = new ArrayList<>(definedRoles);
//            toRemove.forEach((user::removeRole));
//        }
//        this.entityManager.clear();
//    }


    private void checkUniqueUserEmailIfEmailHasChanged(ClientUserRequestDto dto, ClientUser clientUser) {
        if (!dto.getEmail().equals(clientUser.getEmail()) && this.userEmailTaken(dto.getEmail(), clientUser.getId())) {
            throw new DuplicateEntityException(ADMIN_USER, dto.getEmail());
        }
    }

    @Override
    public void publishForgotPasswordEmail(String email) {
        Optional<ClientUser> user = repository.findAuthUserByEmail(email);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException(EntityType.USER, email);
        }
        final String token = UUID.randomUUID().toString();
        this.createPasswordResetTokenForUser(user.get(), token);
        String passwordResetUrl = this.createPasswordResetUrl(token);
        this.publishPasswordResetEmail(user.get(), passwordResetUrl);
    }

    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        String result = this.validatePasswordResetToken(resetPasswordDto.getToken());
        if (StringUtils.isNotEmpty(result)) {
            throw new InvalidRequestException(result);
        }
        Optional<ClientUser> user = this.findUserByPasswordResetToken(resetPasswordDto.getToken());
        if (user.isEmpty()) {
            throw new InvalidRequestException("user with token does not exist");
        }
        this.resetPassword(user.get(), resetPasswordDto.getNewPassword());
    }

    @Override
    public ClientUserDetailDto updateProfile(long userId, UpdateUserProfileDto dto) {
        Optional<ClientUser> optionalUser = this.repository.findClientUserDetail(userId);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException(CLIENT_USER, String.valueOf(userId));
        }
        ClientUser user = optionalUser.get();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setTelephoneNumber(dto.getPhone());
        this.updateUser(user);
        return this.convertToDetailDto(user);
    }

    @Override
    @Transactional
    public String updateProfilePic(long userId, MultipartFile profilePic) {
        Optional<ClientUser> optionalUser = this.repository.findClientUserDetail(userId);
        if (optionalUser.isEmpty()) {
            throw new ResourceNotFoundException(ADMIN_USER, String.valueOf(userId));
        }
        return storeFile(optionalUser.get(), profilePic);
    }


    @Override
    public Optional<ClientUser> findUserDetailById(long userId) {
        return this.repository.findClientUserDetail(userId);
    }


    private String storeFile(ClientUser user, MultipartFile profilePic) {
        if (FileUploadValidatorUtils.isFileUploaded(profilePic)) {
            try {
                //build file path
                String filename = FilePathUtils.buildUniqueFileName(profilePic);
                String filePath = FilePathUtils.buildClientUserProfilePicUploadPath(user);
                String fileNamePath = filePath + File.separator + filename;
                //if edit mode delete existing file
                if (user.getProfilePic() != null) {
                    this.fileStorageService.deleteFile(filePath + File.separator + user.getProfilePic());
                }
                //store file
                this.fileStorageService.storeFile(profilePic, fileNamePath);
                //add or update record
                user.setProfilePic(filename);
                return this.fileStorageService.getStorageLocation() + File.separator + fileNamePath;
            } catch (Exception e) {
                log.error("Unable to store uploaded profile pic", e);
            }
        }
        return null;
    }


    private void publishPasswordResetEmail(ClientUser user, String passwordResetUrl) {
        try {
            this.emailTemplateUtils.sendPasswordResetEmail(user, passwordResetUrl);
        } catch (Exception e) {
            log.error("Cannot send email to user ", e);
        }
    }

    private String createPasswordResetUrl(String token) {
        return urlDomain + Constants.PASSWORD_RESET_URL + Constants.CLIENT_URI + "/" + token;
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        this.performActivationRequest(userId, UserStatus.INACTIVE);
    }

    @Override
    @Transactional
    public void activateUser(Long userId) {
        this.performActivationRequest(userId, ACTIVE);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        Optional<ClientUser> optional = this.repository.findById(userId);
        optional.ifPresent(t -> t.getAuditSection().setDelF("1") /*this.repository.delete(t)*/);
    }

    @Override
    public Optional<ClientUser> findByPasswordResetToken(String token) {
        return this.repository.findUserByPasswordResetToken(token);
    }


    private void sendCreateClientUserEmail(ClientUser clientUser, String plainPassword) {

        try {
            this.emailTemplateUtils.sendCreateClientUserEmail(clientUser, plainPassword);
        } catch (Exception e) {
            log.error("Cannot send create organisation user email", e);
        }

    }

//    private String createUserPassword(User user) {
//        String password = CommonUtils.generatePassword();
//        user.setPassword(password);
//        this.encodeUserPassword(user);
//        return password;
//    }

    private void createUserPassword(User user, String password){
        user.setPassword(password);
        this.encodeUserPassword(user);
    }

    @Override
    public ClientUserProfileDto fetchClientUserProfile(String username) {
        Optional<ClientUser> clientUser = this.repository.findByEmail(username);
        if (clientUser.isEmpty()) {
            throw new ResourceNotFoundException(CLIENT_USER, username);
        }
        return this.convertToProfileDto(clientUser.get());
    }

    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordDto changePasswordDto) {

        Optional<ClientUser> user = this.repository.findByEmail(email);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException(CLIENT_USER, email);
        }
        if (!this.checkIfValidOldPassword(changePasswordDto.getPassword(), user.get().getPassword())) {
            throw new InvalidRequestException("Invalid old password");
        }

        this.changePassword(user.get(), changePasswordDto.getNewPassword());
    }


    @Override
    @Transactional
    public void resendPasswordLink(long userId) {
        Optional<ClientUser> user = this.repository.findClientUserDetail(userId);
        if(user.isEmpty()){
            throw new ResourceNotFoundException(CLIENT_USER, String.valueOf(userId));
        }
            this.resendPasswordLink(user.get());
    }

    private void resendPasswordLink(ClientUser clientUser) {
        this.createPasswordCreationToken(clientUser);
        this.publishCreatePasswordUrl(clientUser);
    }

    private void changePassword(User user, String newPassword) {
        user.setPassword(this.passwordEncoder.encode(newPassword));
        this.repository.save((ClientUser) user);
    }

    private boolean checkIfValidOldPassword(final String oldPassword, final String userPassword) {
        return passwordEncoder.matches(oldPassword, userPassword);
    }

    private ClientUserProfileDto convertToProfileDto(ClientUser clientUser) {
        ClientUserProfileDto userProfileDto = new ClientUserProfileDto();
        userProfileDto.setUserId(clientUser.getId());
        userProfileDto.setEmail(clientUser.getEmail());
        userProfileDto.setFirstName(clientUser.getFirstName());
        userProfileDto.setLastName(clientUser.getLastName());
        userProfileDto.setPhone(clientUser.getTelephoneNumber());
        userProfileDto.setProfilePic(FilePathUtils.buildFileUrl(fileStorageService.getStorageLocation(), FilePathUtils.buildClientUserProfilePicUploadPath(clientUser), clientUser.getProfilePic()));
        return userProfileDto;
    }

    private void encodeUserPassword(User user) {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
    }

//    private void addUserRoles(ClientUser clientUser, Collection<Role> roles) {
//        roles.forEach(clientUser::addRole);
//    }


    private ClientUser createUserModel(ClientUserRequestDto clientUserRequestDto) {
        ClientUser clientUser = new ClientUser();
        this.mapUserDtoToModelEntity(clientUserRequestDto, clientUser);
        return clientUser;
    }

    private void performActivationRequest(Long userId, UserStatus status) {
        Optional<ClientUser> clientUser = this.repository.findById(userId);
        if (clientUser.isEmpty()) {
            throw new ResourceNotFoundException(CLIENT_USER, String.valueOf(userId));
        }
        clientUser.get().setStatus(status);
        clientUser.get().setStatusDate(today());
    }


    private ClientUser updateUser(ClientUser user) {
        return this.repository.save(user);
    }


    private boolean isGlobalRole(RoleType roleType) {
        return roleType.equals(RoleType.GLOBAL);
    }

    private ClientUser saveUser(ClientUser clientUser) {
        return this.repository.save(clientUser);
    }


    /**
     * send email to organisation admin user that allows user to create a password
     * to login into the client portal as main administrator
     *
     * @param clientUser
     */
    private void publishCreatePasswordUrl(ClientUser clientUser) {
        try {
            String passwordUrl = this.createPasswordUrl(clientUser.getPasswordResetToken());
            this.emailTemplateUtils.sendCreatePasswordUrlLinkEmail(clientUser, passwordUrl);

        } catch (Exception e) {
            log.error("Cannot send email to user ", e);
        }
    }

    private String createPasswordUrl(String passwordResetToken) {
        return urlDomain + Constants.PASSWORD_URL + "?" +
                Constants.PASSWORD_URL_TOKEN_PARAM + passwordResetToken;
        //return urlDomain + Constants.PASSWORD_URL + "/" + passwordResetToken;
    }

    private void mapUserDtoToModelEntity(ClientUserRequestDto clientUserRequestDto, ClientUser clientUser) {
        clientUser.setEmail(clientUserRequestDto.getEmail());
        clientUser.setFirstName(clientUserRequestDto.getFirstName());
        clientUser.setLastName(clientUserRequestDto.getLastName());
        clientUser.setTelephoneNumber(clientUserRequestDto.getPhone());
        clientUser.setStatus(ACTIVE);
        clientUser.setUserType(CLIENT);
    }

    private String getPasswordSettingToken() {
        return RandomStringUtils.randomAlphanumeric(32);
    }

    private Optional<ClientUser> findUserByEmail(String email) {
        return this.repository.findByEmail(email);
    }


    private void createPasswordResetTokenForUser(ClientUser user, String token) {
        user.setPasswordResetToken(token);
        String valdtyTrm = this.systemConfigurationService.findConfigValueByKey(PSSWORD_SETTING_TOKEN_VLDTY_TRM);
        user.calculateTokenExpiryDate(valdtyTrm);
        this.repository.save(user);
    }


    private String validatePasswordResetToken(String token) {
        if (token == null) {
            return "Invalid token";
        }
        Optional<ClientUser> user = this.repository.findUserByPasswordResetToken(token);
        if (user.isEmpty()) {
            return "Invalid token";
        }
        return isExpiredToken(user.get()) ? "Token has expired" : null;
    }

    private boolean isExpiredToken(ClientUser user) {
        return user.getPasswordResetValidityTerm().before(now());
    }


    private Optional<ClientUser> findUserByPasswordResetToken(String token) {
        return this.repository.findUserByPasswordResetToken(token);
    }

    public void resetPassword(User user, String password) {
        this.changePassword(user, password);
    }


}
