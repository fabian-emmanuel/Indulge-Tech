package com.indulgetech.repositories.user;

import com.indulgetech.dto.user.ListUserDto;
import com.indulgetech.models.users.client.ClientUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClientUserRepository extends JpaRepository<ClientUser, Long> {

    boolean existsByEmail(String email);

    @Query("select u from ClientUser u " +
            "where u.email =:email " +
            "and u.auditSection.delF='0'")
    Optional<ClientUser> findByEmail(String email);

    @Query("select u from ClientUser u " +
            "where u.email =:username and u.auditSection.delF='0'")
    Optional<ClientUser> findAuthUserByEmail(String username);

    Optional<ClientUser> findUserByPasswordResetToken(String resetToken);

    @Query("select new com.indulgetech.dto.user.ListUserDto(u.id,CONCAT(u.lastName,' ', u.firstName),u.email,u.telephoneNumber,u.status,u.auditSection.dateCreated) from ClientUser u WHERE u.auditSection.delF <> '1' order by u.lastName asc")
    Page<ListUserDto> listUsers(Pageable pageable);

    boolean existsByEmailAndIdNot(String email, Long userId);

    @Query("select u from ClientUser u " +
            "where u.id =:userId " +
            "and u.auditSection.delF='0'")
    Optional<ClientUser> findClientUserDetail(long userId);

}