package com.service.authentication.util;

public final class ApiConst {
    public static final String API_VERSION_1 = "Api-Version=1.0";

    public static final String JWT_BEARER = "Bearer ";
    public static final String JWT_ROLE = "roles";
    public static final String JWT_SECRET = "Api@Secret.Key";
    public static final long JWT_TOKEN_VALIDITY = 60 * 60 * 1000; // 1 hour
    public static final long JWT_REFRESH_TOKEN_VALIDITY = 24 * 60 * 60 * 1000; // 7 day
    public static final long JWT_RESET_TOKEN_VALIDITY = 60 * 60 * 1000; // 1 hour

}
