package com.indulgetech.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Function;

import static com.indulgetech.constants.Constants.AUTHORITIES_CLAIM_KEY_SEP;
import static com.indulgetech.constants.Constants.USER_TYPE;


@Component
public class JwtUtil implements AuthTokenProvider {

    @Value("${jwt.token.secret-key:secret-key}")
    private String secretKey;

    @Value("${jwt.token.expire-length:86400000}")
    private long validityInMilliseconds;

    @Value("${jwt.authorities.key}")
    public String AUTHORITIES_KEY;

    @PostConstruct
    public void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(now).setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }


    public boolean validateToken(String token, String authenticatedUserName) {
        final String username = extractUsername(token);
        return (username.equals(authenticatedUserName) && !isTokenExpired(token));
    }

    @Override
    public String generateToken(String userName, String userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_TYPE, userType);
        return createToken(claims, userName);
    }

    @Override
    public String extractUserTypeFromToken(String token) throws UnsupportedEncodingException {
        Claims claims = this.extractAllClaims(token);
        return (String) claims.get(USER_TYPE);
    }

    @Override
    public String extractTokenFromRequest(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }

    public void setValidityInMilliseconds(long validityInMilliseconds) {
        this.validityInMilliseconds = validityInMilliseconds;
    }

    @Override
    public Collection<String> extractAuthoritiesClaims(String token) {
        Claims claims = this.extractAllClaims(token);
        if(claims.get(AUTHORITIES_KEY)!=null) {
            return Arrays.asList(((String) claims.get(AUTHORITIES_KEY)).split(AUTHORITIES_CLAIM_KEY_SEP));
        }
        return Collections.emptyList();
    }
}
