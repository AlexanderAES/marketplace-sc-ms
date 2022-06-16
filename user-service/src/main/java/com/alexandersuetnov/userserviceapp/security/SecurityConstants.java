package com.alexandersuetnov.userserviceapp.security;

public class SecurityConstants {
    public static final String SIGN_UP_URLS = "/users/*";
    public static final String ADMIN_PANEL = "api/v1/admin/**";
    public static final String GET_ALL_PRODUCTS = "api/v1/product/all";
    public static final String CONFIRM_REG = "/users/activate/**";
    public static final String API_DOCS = "/v3/api-docs/**";
    public static final String SWAGGER_UI = "/swagger-ui/**";
    public static final String SWAGGER_UI_HTML = "/swagger-ui.html";
    public static final String SECRET = "SecretKeyGenJWT";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String CONTENT_TYPE = "application/json";
    public static final long EXPIRATION_TIME = 1200_000; //20min
}