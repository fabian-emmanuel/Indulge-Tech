package com.indulgetech.repositories.user;

import com.indulgetech.dto.user.admin.ListAdminUserDto;
import com.indulgetech.models.users.admin.AdminUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser,Long> {

    Optional<AdminUser> findByEmail(String email);

    @Query("select u from AdminUser u left join fetch u.roles r left join fetch r.permissions p  where u.email =:username and u.auditSection.delF='0'")
    Optional<AdminUser> findAuthUserByEmail(String username);

    boolean existsByEmail(String email);

    @Query("select new com.indulgetech.dto.user.admin.ListAdminUserDto(u.id,CONCAT(u.lastName,' ', u.firstName),u.email,u.telephoneNumber,u.status,u.auditSection.dateCreated) from AdminUser u WHERE  u.auditSection.delF <> '1' order by u.lastName asc")
    Page<ListAdminUserDto> listAdminUsers(Pageable pageable);

    @Query("select u from AdminUser u left join fetch u.roles r where u.id =:userId and u.auditSection.delF='0'")
    Optional<AdminUser> findAdminUserDetail(final Long userId);

    boolean existsByEmailAndIdNot(String email, Long userId);

    Optional<AdminUser> findUserByPasswordResetToken(String token);
}
