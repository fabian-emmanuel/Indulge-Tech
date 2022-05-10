package com.indulgetech.security;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

public interface AuthTokenProvider {

    String generateToken(String userName, String userType);

    String extractUserTypeFromToken(String token) throws UnsupportedEncodingException;

    String extractTokenFromRequest(HttpServletRequest request);

    String extractUsername(String token);

    Boolean isTokenExpired(String token);

    void setValidityInMilliseconds(long validityInMilliseconds);

    Collection<String> extractAuthoritiesClaims(String token);
}
