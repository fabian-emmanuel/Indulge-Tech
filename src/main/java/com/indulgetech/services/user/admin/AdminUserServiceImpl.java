package com.indulgetech.services.user.admin;



import com.indulgetech.constants.Constants;
import com.indulgetech.dto.auth.ResetPasswordDto;
import com.indulgetech.dto.user.ChangePasswordDto;
import com.indulgetech.dto.user.UpdateUserProfileDto;
import com.indulgetech.dto.user.UserProfileDto;
import com.indulgetech.dto.user.admin.AdminUserDetailDto;
import com.indulgetech.dto.user.admin.AdminUserDetailResponseDto;
import com.indulgetech.dto.user.admin.AdminUserRequestDto;
import com.indulgetech.dto.user.admin.ListAdminUserDto;
import com.indulgetech.dto.user.role.ListRoleDto;
import com.indulgetech.exceptions.*;
import com.indulgetech.models.users.User;
import com.indulgetech.models.users.UserStatus;
import com.indulgetech.models.users.admin.AdminUser;
import com.indulgetech.models.users.roles.Role;
import com.indulgetech.models.users.roles.RoleType;
import com.indulgetech.repositories.user.AdminUserRepository;
import com.indulgetech.security.admin.AdminUserInfo;
import com.indulgetech.services.generic.BaseEntityServiceImpl;
import com.indulgetech.services.storage.FileStorageService;
import com.indulgetech.services.system.SystemConfigurationService;
import com.indulgetech.utils.*;
import constants.SchemaConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.indulgetech.exceptions.EntityType.ADMIN_USER;


@Service("adminUserServiceImpl")
public class AdminUserServiceImpl extends BaseEntityServiceImpl<Long, AdminUser> implements AdminUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminUserServiceImpl.class);

    private final AdminUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final EmailTemplateUtils emailTemplateUtils;
    private final UserInfoUtil userInfoUtil;
    private final EntityManager entityManager;
    private final SystemConfigurationService systemConfigurationService;
    private final FileStorageService fileStorageService;
    private final CacheManager cacheManager;

    @Value("${api.url-domain}")
    private String urlDomain;


    @Autowired
    public AdminUserServiceImpl(AdminUserRepository repository, PasswordEncoder passwordEncoder, RoleService roleService
            , EmailTemplateUtils emailTemplateUtils, UserInfoUtil userInfoUtil
            , EntityManager entityManager, SystemConfigurationService systemConfigurationService
            ,FileStorageService fileStorageService,CacheManager cacheManager) {

        super(repository);
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.emailTemplateUtils = emailTemplateUtils;
        this.userInfoUtil = userInfoUtil;
        this.entityManager = entityManager;
        this.systemConfigurationService = systemConfigurationService;
        this.fileStorageService=fileStorageService;
        this.cacheManager=cacheManager;

    }

    @Transactional
    @Override
    public AdminUser createUser(AdminUserRequestDto adminUserRequestDto) {

        if (this.userEmailTaken(adminUserRequestDto.getEmail())) {
            throw new DuplicateEntityException(ADMIN_USER, adminUserRequestDto.getEmail());
        }

        AdminUser adminUser = this.createAdminUserModelEntity(adminUserRequestDto);

        this.addUserRoles(adminUser, adminUserRequestDto.getRoles());

        String plainPassword = this.createUserPassword(adminUser);

        this.sendCreateAdminUserEmail(adminUser, plainPassword);

        return this.saveAdminUser(adminUser);
    }

    private void addUserRoles(AdminUser adminUser, Collection<Integer> requestRoles) {
        boolean adminMemberRoleSelected = false;

        if (CollectionUtils.isEmpty(requestRoles)){
            Optional<Role> role = this.roleService.fetchByRoleKey(Constants.ROLE_ADMIN_MEMBER);
            if (role.isEmpty()){
                throw new CustomSystemException("Role admin member not defined");
            }
            this.addAdminUserRoles(adminUser, List.of(role.get()));
        } else {
            Collection<Role> roles = this.roleService.getRoleByIds(requestRoles);
            for (Role role: roles) {
                if (Constants.ROLE_ADMIN_MEMBER.equals(role.getRoleKey())){
                    adminMemberRoleSelected = true;
                    break;
                }
            }
             if (!adminMemberRoleSelected){
                 Optional<Role> role = this.roleService.fetchByRoleKey(Constants.ROLE_ADMIN_MEMBER);
                 if (role.isEmpty()){
                     throw new CustomSystemException("Role admin member not defined");
                 }
                 this.addAdminUserRoles(adminUser, List.of(role.get()));
             }
            if (CollectionUtils.isNotEmpty(roles)) {
                this.addAdminUserRoles(adminUser, roles);
            }
       }
    }

    private String createUserPassword(User user) {
        String password = CommonUtils.generatePassword();
        user.setPassword(password);
        this.encodeUserPassword(user);
        return password;
    }

    private void encodeUserPassword(User user) {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
    }


    @Override
    public Page<ListAdminUserDto> listUsers(Pageable pageable) {
        return this.repository.listAdminUsers(PageUtils.syncPageRequest(pageable));
    }

    @Override
    public UserProfileDto fetchAdminUserProfile(String username) {
        Optional<AdminUser> adminUser = this.repository.findByEmail(username);
        if (adminUser.isEmpty()) {
            throw new ResourceNotFoundException(ADMIN_USER, username);
        }
        return this.convertToProfileDto(adminUser.get());
    }

    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordDto changePasswordDto) {
        Optional<AdminUser> adminUser = this.repository.findByEmail(email);
        if (adminUser.isEmpty()) {
            throw new ResourceNotFoundException(ADMIN_USER, email);
        }
        if (!this.checkIfValidOldPassword(changePasswordDto.getPassword(), adminUser.get().getPassword())) {
            throw new InvalidRequestException("Invalid old password");
        }
        this.changePassword(adminUser.get(), changePasswordDto.getNewPassword());
    }

    private void changePassword(User user, String newPassword) {
        user.setPassword(this.passwordEncoder.encode(newPassword));
        this.repository.save((AdminUser) user);
    }

    private boolean checkIfValidOldPassword(final String oldPassword, final String userPassword) {
        return passwordEncoder.matches(oldPassword, userPassword);
    }


    @Override
    public AdminUserDetailResponseDto fetchAdminUserDetail(Long userId) {
        Optional<AdminUser> adminUser = this.repository.findAdminUserDetail(userId);
        if (adminUser.isEmpty()) {
            throw new ResourceNotFoundException(ADMIN_USER, String.valueOf(userId));
        }
        AdminUserDetailDto detailDto = this.convertToDetailDto(adminUser.get());
        Collection<ListRoleDto> roles = this.roleService.listRolesForAdmin();
        return createAdminUserDetailResponseDto(detailDto, roles);
    }

    @Override
    @Transactional
    //@CacheEvict(value="adminUserAuthInfo", key = "#userId")
    public AdminUser updateAdminUser(Long userId, AdminUserRequestDto dto) {

        Optional<AdminUser> optional = this.repository.findAdminUserDetail(userId);
        if (optional.isEmpty()) {
            throw new ResourceNotFoundException(ADMIN_USER, String.valueOf(userId));
        }

        AdminUser adminUser = optional.get();

        this.checkUniqueUserEmailIfEmailHasChanged(dto, adminUser);

        this.mapRequestDtoToModelEntity(dto, adminUser);

        this.updateAdminUserRoles(adminUser, dto.getRoles());

        adminUser=updateUser(adminUser);

        //todo: do this asynchronously
        Cache cache=cacheManager.getCache("adminUserAuthInfo");
        if(cache!=null) {
            cache.evictIfPresent(adminUser.getEmail());
        }
        return adminUser;
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        this.performActivationRequest(userId, UserStatus.INACTIVE);
    }

    @Override
    @Transactional
    public void activateUser(Long userId) {
        this.performActivationRequest(userId, UserStatus.ACTIVE);
    }

    @Override
    @Transactional
    public void publishForgotPasswordEmail(String email) {

        Optional<AdminUser> user = repository.findAuthUserByEmail(email);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException(EntityType.USER,email);
        }

        final String token = UUID.randomUUID().toString();
        this.createPasswordResetTokenForUser(user.get(),token);

        String passwordResetUrl = this.createPasswordResetUrl(token);

        this.publishPasswordResetEmail(user.get(),passwordResetUrl);

    }

    private void publishPasswordResetEmail(AdminUser user, String passwordResetUrl) {
        try {
            this.emailTemplateUtils.sendPasswordResetEmail(user,passwordResetUrl);
        } catch (Exception e) {
            LOGGER.error("Cannot send email to user ", e);
        }
    }


    private String createPasswordResetUrl(String token) {
        return urlDomain + Constants.PASSWORD_RESET_URL +Constants.ADMIN_URI+ "/"+ token;

    }

    private void performActivationRequest(Long userId, UserStatus status) {
        Optional<AdminUser> adminUser = this.repository.findById(userId);
        if (adminUser.isEmpty()) {
            throw new ResourceNotFoundException(ADMIN_USER, String.valueOf(userId));
        }
        adminUser.get().setStatus(status);
        adminUser.get().setStatusDate(CustomDateUtils.now());
        AdminUserInfo adminUserInfo = this.userInfoUtil.authenticatedAdminUserInfo();
        if (adminUserInfo == null) {
            throw new UnAuthorizedException();
        }
        adminUser.get().getAuditSection().setModifiedBy(adminUserInfo.getUserId());
    }


    private AdminUser updateUser(AdminUser adminUser) {
        return this.repository.save(adminUser);
    }


    private void checkUniqueUserEmailIfEmailHasChanged(AdminUserRequestDto dto, AdminUser adminUser) {
        if (!dto.getEmail().equals(adminUser.getEmail()) && this.userEmailTaken(dto.getEmail(), adminUser.getId())) {
            throw new DuplicateEntityException(ADMIN_USER, dto.getEmail());
        }
    }

    private AdminUserDetailResponseDto createAdminUserDetailResponseDto(AdminUserDetailDto detailDto, Collection<ListRoleDto> roles) {
        AdminUserDetailResponseDto userDetailResponseDto = new AdminUserDetailResponseDto();
        userDetailResponseDto.setAdminUserDto(detailDto);
        Map<String, Object> extras = new HashMap<>();
        extras.put("roles", roles);
        extras.put("status", UserStatus.toItemList());
        userDetailResponseDto.setExtras(extras);
        return userDetailResponseDto;
    }

    private AdminUserDetailDto convertToDetailDto(AdminUser adminUser) {
        AdminUserDetailDto dto = new AdminUserDetailDto();
        dto.setUserId(adminUser.getId());
        dto.setEmail(adminUser.getEmail());
        dto.setFirstName(adminUser.getFirstName());
        dto.setLastName(adminUser.getLastName());
        dto.setPhone(adminUser.getTelephoneNumber());
        dto.setStatus(adminUser.getStatus().name());
        dto.setRoles(adminUser.getRoles().stream().map(Role::getId).collect(Collectors.toList()));
        return dto;
    }


    private UserProfileDto convertToProfileDto(AdminUser adminUser) {
        UserProfileDto dto = new UserProfileDto();
        dto.setUserId(adminUser.getId());
        dto.setEmail(adminUser.getEmail());
        dto.setFirstName(adminUser.getFirstName());
        dto.setLastName(adminUser.getLastName());
        dto.setPhone(adminUser.getTelephoneNumber());
        dto.setProfilePic(FilePathUtils.buildFileUrl(fileStorageService.getStorageLocation(),FilePathUtils.buildAdminUserProfilePicUploadPath(),adminUser.getProfilePic()));

        return dto;
    }

    private boolean userEmailTaken(String email) {
        return this.repository.existsByEmail(email);
    }

    private boolean userEmailTaken(String email, Long id) {
        return repository.existsByEmailAndIdNot(email, id);
    }

    private AdminUser saveAdminUser(AdminUser adminUser) {
        return this.repository.save(adminUser);
    }

    private AdminUser createAdminUserModelEntity(AdminUserRequestDto adminUserRequestDto) {
        AdminUser adminUser = new AdminUser();
        this.mapRequestDtoToModelEntity(adminUserRequestDto, adminUser);
        return adminUser;
    }

    /**
     * add user roles
     * only admin type role can be assigned to admin users
     *
     * @param adminUser
     * @param roles
     */
    private void addAdminUserRoles(AdminUser adminUser, Collection<Role> roles) {
        roles.forEach(role -> {
            if (role.getRoleType().equals(RoleType.ADMIN)) {
                adminUser.addRole(role);
            }
        });
    }

    private void updateAdminUserRoles(AdminUser adminUser, Collection<Integer> reqRoles) {
        this.removeAllAdminUserRoles(adminUser);
        this.addUserRoles(adminUser, reqRoles);
    }

    private void removeAllAdminUserRoles(AdminUser adminUser) {
        Collection<Role> definedRoles = adminUser.getRoles();
        if (!CollectionUtils.isEmpty(definedRoles)) {
            //to avoid concurrent modification two loops are used TODO: optimize this to use one loop instead
            Collection<Role> toRemove = new ArrayList<>(definedRoles);
            toRemove.forEach((adminUser::removeRole));
        }
        this.entityManager.clear();
    }


    private void sendCreateAdminUserEmail(AdminUser adminUser, String plainPassword) {
        try {
            this.emailTemplateUtils.sendCreateAdminUserEmail(adminUser, plainPassword);
        } catch (Exception e) {
            LOGGER.error("Cannot send create admin user email", e);
        }
    }

    private void mapRequestDtoToModelEntity(AdminUserRequestDto adminUserRequestDto, AdminUser adminUser) {

        adminUser.setEmail(adminUserRequestDto.getEmail());
        adminUser.setFirstName(adminUserRequestDto.getFirstName());
        adminUser.setLastName(adminUserRequestDto.getLastName());
        adminUser.setTelephoneNumber(adminUserRequestDto.getPhone());
        if (adminUser.getId() == null) {//save mode
        } else {//edit mode
            AdminUserInfo adminUserInfo = (AdminUserInfo) this.userInfoUtil.authenticatedUserInfo();
            adminUser.getAuditSection().setModifiedBy(adminUserInfo.getUserId());
            if (!adminUserRequestDto.getStatus().equals(adminUser.getStatus().name())) {
                adminUser.setStatusDate(CustomDateUtils.now());
            }
        }
        adminUser.setStatus(UserStatus.valueOf(adminUserRequestDto.getStatus()));
    }

    public Optional findUserByEmail(String email) {
        return this.repository.findByEmail(email);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordDto resetPasswordDto) {

        String result=this.validatePasswordResetToken(resetPasswordDto.getToken());
        if(StringUtils.isNotEmpty(result)){
            throw new InvalidRequestException(result);
        }

        Optional<AdminUser> user = this.findUserByPasswordResetToken(resetPasswordDto.getToken());
        if (user.isEmpty()) {
            throw new InvalidRequestException("user with token does not exist");
        }
        this.resetPassword(user.get(),resetPasswordDto.getNewPassword());
    }

    @Override
    public AdminUserDetailDto updateProfile(long userId, UpdateUserProfileDto dto) {
        Optional<AdminUser> optionalAdminUser = this.repository.findAdminUserDetail(userId);
        if (optionalAdminUser.isEmpty()) {
            throw new ResourceNotFoundException(ADMIN_USER, String.valueOf(userId));
        }
        AdminUser adminUser=optionalAdminUser.get();
        adminUser.setFirstName(dto.getFirstName());
        adminUser.setLastName(dto.getLastName());
        adminUser.setTelephoneNumber(dto.getPhone());

        this.updateUser(adminUser);

        return this.convertToDetailDto(adminUser);
    }

    @Override
    @Transactional
    public String updateProfilePic(long userId, MultipartFile profilePic) {
        Optional<AdminUser> optionalAdminUser = this.repository.findAdminUserDetail(userId);
        if (optionalAdminUser.isEmpty()) {
            throw new ResourceNotFoundException(ADMIN_USER, String.valueOf(userId));
        }
      return storeFile(optionalAdminUser.get(),profilePic);
    }

    private String storeFile(AdminUser adminUser, MultipartFile profilePic) {
        if (FileUploadValidatorUtils.isFileUploaded(profilePic)) {
            try {
                //build file path
                String filename = FilePathUtils.buildUniqueFileName(profilePic);
                String filePath = FilePathUtils.buildAdminUserProfilePicUploadPath();
                String fileNamePath = filePath + File.separator + filename;
                //if edit mode delete existing file
                if(adminUser.getProfilePic()!=null) {
                    this.fileStorageService.deleteFile(filePath+File.separator+adminUser.getProfilePic());
                }
                //store file
                this.fileStorageService.storeFile(profilePic, fileNamePath);
                //add or update record
                adminUser.setProfilePic(filename);
                return this.fileStorageService.getStorageLocation()+File.separator+fileNamePath;
            } catch (Exception e) {
                LOGGER.error("Unable to store uploaded profile pic", e);
            }
        }
        return null;
    }

    private void createPasswordResetTokenForUser(AdminUser user, String token) {
        user.setPasswordResetToken(token);
        String valdtyTrm = this.systemConfigurationService.findConfigValueByKey(SchemaConstant.PSSWORD_SETTING_TOKEN_VLDTY_TRM);
        user.calculateTokenExpiryDate(valdtyTrm);
        this.repository.save(user);
    }

    public String validatePasswordResetToken(String token) {
        if (token == null) {
            return "Invalid token";
        }
        Optional<AdminUser> user = this.repository.findUserByPasswordResetToken(token);
        if (user.isEmpty()) {
            return "Invalid token";
        }

        if (isExpiredToken(user.get())) {
            return "Token has expired";
        }
        return null;
    }

    private boolean isExpiredToken(AdminUser user) {
        return user.getPasswordResetValidityTerm().before(CustomDateUtils.now());
    }

    private Optional<AdminUser> findUserByPasswordResetToken(String token) {
        return this.repository.findUserByPasswordResetToken(token);
    }

    private void resetPassword(User user, String password) {
        this.changePassword(user, password);
    }


}
