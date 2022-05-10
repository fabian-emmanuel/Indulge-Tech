package com.indulgetech.services.auth;


import com.indulgetech.models.users.token.TokenBlacklist;
import com.indulgetech.repositories.token.TokenBlacklistRepository;
import com.indulgetech.security.AuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService{

    public static final int SLICE_SIZE = 100;
    private final TokenBlacklistRepository repository;
    private final AuthTokenProvider authTokenProvider;

    @Override
    public void blacklistToken(String token) {
        TokenBlacklist tokenBlacklist= this.createTokenBlacklistEntity(token);
        this.saveTokenBlacklist(tokenBlacklist);
    }

    @Override
    public void purgeExpiredTokens() {

        int pageOffset=0;
        int pageSize=SLICE_SIZE;
        Pageable pageable = PageRequest.of(pageOffset, pageSize);

        Slice<TokenBlacklist> result=this.repository.findAll(pageable);

        while (!result.isEmpty()) {
            Collection<TokenBlacklist> expiredBlacklistedTokens=this.getExpiredBlacklistedTokens(result);
            repository.deleteAll(expiredBlacklistedTokens);
            pageOffset+=pageSize;
            result=this.repository.findAll(PageRequest.of(pageOffset, pageSize));
        }

    }

    @Override
    public boolean isBlacklisted(String token) {
        return repository.existsByToken(token);
    }

    private Collection<TokenBlacklist> getExpiredBlacklistedTokens(Slice<TokenBlacklist> blacklistedTokens) {
       return blacklistedTokens.stream().filter(tokenBlacklist->authTokenProvider.isTokenExpired(tokenBlacklist.getToken())).collect(Collectors.toList());
    }

    private void saveTokenBlacklist(TokenBlacklist tokenBlacklist) {
         this.repository.save(tokenBlacklist);
    }

    private TokenBlacklist createTokenBlacklistEntity(String token) {
        TokenBlacklist tokenBlacklist=new TokenBlacklist();
        tokenBlacklist.setToken(token);
        return tokenBlacklist;
    }
}
