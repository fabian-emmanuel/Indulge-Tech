package com.indulgetech.services.user.client;

import com.indulgetech.dto.auth.ResetPasswordDto;
import com.indulgetech.dto.user.ChangePasswordDto;
import com.indulgetech.dto.user.ListUserDto;
import com.indulgetech.dto.user.UpdateUserProfileDto;
import com.indulgetech.dto.user.client.*;
import com.indulgetech.models.users.client.ClientUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ClientUserService {

    Page<ListUserDto> listUsers(Pageable pageable);

    ClientUserDetailResponseDto fetchUserDetail(final Long userId);

    ClientUser updateUser(Long userId, ClientUserRequestDto clientUserRequestDto);

    void deactivateUser(final Long userId);

    void activateUser(final Long userId);

    void deleteUser(final Long userId);

    Optional<ClientUser> findByPasswordResetToken(final String token);

    ClientUser createClientUser(final ClientUserRequestDto clientUserRequestDto);


    ClientUserProfileDto fetchClientUserProfile(String username);

    void changePassword(String username, ChangePasswordDto changePasswordDto);

    void publishForgotPasswordEmail(String email);

    void resetPassword(ResetPasswordDto resetPasswordDto);

    ClientUserDetailDto updateProfile(long userId, UpdateUserProfileDto dto);

    String updateProfilePic(long id, MultipartFile profileFileUpload);

    Optional<ClientUser> findUserDetailById(long userId);

    void resendPasswordLink(long userId);
}
