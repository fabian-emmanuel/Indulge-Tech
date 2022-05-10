package com.indulgetech.services.user.admin;

import com.indulgetech.dto.auth.ResetPasswordDto;
import com.indulgetech.dto.user.ChangePasswordDto;
import com.indulgetech.dto.user.UpdateUserProfileDto;
import com.indulgetech.dto.user.UserProfileDto;
import com.indulgetech.dto.user.admin.AdminUserDetailDto;
import com.indulgetech.dto.user.admin.AdminUserDetailResponseDto;
import com.indulgetech.dto.user.admin.AdminUserRequestDto;
import com.indulgetech.dto.user.admin.ListAdminUserDto;
import com.indulgetech.models.users.admin.AdminUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Optional;

public interface AdminUserService {

    AdminUser createUser(AdminUserRequestDto adminUserRequestDto);

    Page<ListAdminUserDto> listUsers(Pageable pageable);

    UserProfileDto fetchAdminUserProfile(String username);

    void changePassword(String user, ChangePasswordDto changePasswordDto);

    AdminUserDetailResponseDto fetchAdminUserDetail(final Long userId);

    AdminUser updateAdminUser(Long userId, AdminUserRequestDto dto);

    void deactivateUser(final Long userId);

    void activateUser(final Long userId);

    void create(AdminUser adminUser);

    void publishForgotPasswordEmail(String email);

    Optional<AdminUser> findUserByEmail(String email);

    void resetPassword(ResetPasswordDto resetPasswordDto);

    AdminUserDetailDto updateProfile(long userId, UpdateUserProfileDto dto);

    String updateProfilePic(long userId, MultipartFile profilePic);

}
