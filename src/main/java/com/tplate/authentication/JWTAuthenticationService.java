package com.tplate.authentication;

import com.tplate.constans.SecurityConstants;
import com.tplate.util.Hora;
import com.tplate.util.TimeUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTAuthenticationService {

    public String generateAccessToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuer(SecurityConstants.JWT_ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TimeUtil.toMilseconds(new Hora(SecurityConstants.JWT_EXPIRATION_TIME_HH))))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET)
                .compact();
    }
}
