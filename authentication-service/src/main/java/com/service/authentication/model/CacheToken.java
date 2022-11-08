package com.service.authentication.model;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Date;

@RedisHash("CacheToken")
@AllArgsConstructor
public class CacheToken implements Serializable {
    private String id;
    private String roles;
    private String accessToken;
    private Date expiryDate;
}
