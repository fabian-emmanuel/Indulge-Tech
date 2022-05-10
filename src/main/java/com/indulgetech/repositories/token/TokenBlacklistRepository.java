package com.indulgetech.repositories.token;

import com.indulgetech.models.users.token.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {

     boolean existsByToken(String token);
}
