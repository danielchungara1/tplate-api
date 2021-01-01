package com.tplate.security.jwt;

import com.tplate.security.constants.Security;
import com.tplate.user.UserEntity;
import com.tplate.util.Minutes;
import com.tplate.util.TimeUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(Security.JWT_SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Boolean ignoreTokenExpiration(String token) {
        // here you specify tokens, for that the expiration is ignored
        return false;
    }

    public String generateToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();

        claims.put(Security.JWT_AUTHORITIES_KEY,
                getAuthorities(user)
                        .stream()
                        .map(s -> new SimpleGrantedAuthority(s.getAuthority()))
                        .collect(Collectors.toList()));
        claims.put(Security.JWT_USER_ID, user.getId());
        return doGenerateToken(claims, user.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUtil.toMiliseconds(new Minutes(Security.JWT_EXPIRATION_TIME_MINUTES))))
                .signWith(SignatureAlgorithm.HS512, Security.JWT_SECRET_KEY).compact();
    }

    public Boolean canTokenBeRefreshed(String token) {
        return (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        Jwts.parser().setSigningKey(Security.JWT_SECRET_KEY).parseClaimsJws(token);

        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(Security.JWT_HEADER_AUTHORIZATION_KEY);
        if (bearerToken != null && bearerToken.startsWith(Security.JWT_TOKEN_BEAR_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Collection<? extends GrantedAuthority>  getAuthorities(UserEntity user) {
        return user.getRol().getPermissions();

    }
}