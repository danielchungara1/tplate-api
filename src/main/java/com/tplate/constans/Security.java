package com.tplate.constans;

public class Security {
    public static final String JWT_SECRET_KEY = "zdtlD3JK56m6wTTgsNFhqzjqP";
    public static final String JWT_ISSUER = "example.io";
    public static final Integer JWT_EXPIRATION_TIME_HH = 8760; // 1 Year
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String JWT_HEADER_STRING = "Authorization";
    public static final String JWT_AUTHORITIES_KEY = "CLAIM_TOKEN";
    public static final String JWT_USER_ID = "CLAIM_ID";
}
