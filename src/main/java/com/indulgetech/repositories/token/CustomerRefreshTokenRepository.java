package com.indulgetech.repositories.token;

import com.indulgetech.models.users.token.CustomerRefreshToken;
import com.indulgetech.models.users.token.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRefreshTokenRepository extends JpaRepository<CustomerRefreshToken, Long> {
    Optional<UserRefreshToken> findByToken(String token);
}
