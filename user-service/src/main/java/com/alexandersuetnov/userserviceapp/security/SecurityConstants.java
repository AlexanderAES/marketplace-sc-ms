package com.alexandersuetnov.userserviceapp.security;

public class SecurityConstants {
    public static final String SIGN_UP_URLS = "/users/*";
    public static final String ADMIN_PANEL = "/admin/**";
    public static final String CONFIRM_REG = "/users/activate/**";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String CONTENT_TYPE = "application/json";
    public static final long EXPIRATION_TIME = 1200_000; //20min
    public static final String SECRET = "SecretKeyGenJWTSecretKeyGenJWTSecretKeyGenJWT";
}