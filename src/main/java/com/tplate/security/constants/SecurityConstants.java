package com.tplate.security.constants;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;

@Component
@Log4j2
public class SecurityConstants {

//  CONSTANTES POR ARCHIVO DE CONFIGURACION
    @Value("${jwt2.secret}")
    private String JWT_SECRET_KEY_;
    public static String JWT_SECRET_KEY;

    @Value("${jwt2.expiration-time-minutes}")
    private Long JWT_EXPIRATION_TIME_MINUTES_;
    public static Long JWT_EXPIRATION_TIME_MINUTES;

//  CONSTANTES INAMOVIBLES
    public static final String JWT_TOKEN_BEAR_PREFIX = "Bearer ";
    public static final String JWT_HEADER_AUTHORIZATION_KEY = "Authorization";
    public static final String JWT_AUTHORITIES_KEY = "CLAIM_TOKEN";
    public static final String JWT_USER_ID = "CLAIM_ID";

//  PARA TENER ACCESO A LAS CONSTANTES DE MANERA ESTATICA.
    @PostConstruct
    protected void init(){
        JWT_SECRET_KEY = Base64.getEncoder().encodeToString(JWT_SECRET_KEY_.getBytes());
        log.info("JWT_SECRET_KEY: {}", "???");

        JWT_EXPIRATION_TIME_MINUTES = JWT_EXPIRATION_TIME_MINUTES_;
        log.info("JWT_EXPIRATION_TIME_HH: {}", JWT_EXPIRATION_TIME_MINUTES);

    }
}
