package com.tplate.authentication;

import com.tplate.constans.Security;
import com.tplate.permission.PermissionEntity;
import com.tplate.user.UserEntity;
import com.tplate.util.Hora;
import com.tplate.util.TimeUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;

    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(Security.JWT_SECRET_KEY.getBytes());
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }


    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }


    public String generateToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();

        claims.put(Security.JWT_AUTHORITIES_KEY,
                getAuthorities(user)
                        .stream()
                        .map(s -> new SimpleGrantedAuthority(s.getAuthority()))
                        .collect(Collectors.toList()));
        claims.put(Security.JWT_USER_ID, user.getUsername());
        return doGenerateToken(claims, user.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() +
                        TimeUtil.toMilseconds(new Hora(Security.JWT_EXPIRATION_TIME_HH))))
                .signWith(SignatureAlgorithm.HS512, Security.JWT_SECRET_KEY).compact();
    }


    public Boolean validateToken(String token, UserDetails userDetails) {
        Jwts.parser().setSigningKey(Security.JWT_SECRET_KEY).parseClaimsJws(token);

        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(Security.JWT_HEADER_STRING);
        if (bearerToken != null && bearerToken.startsWith(Security.JWT_TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private List<PermissionEntity> getAuthorities(UserEntity user) {
        return user.getRol().getPermissions();

    }
}
