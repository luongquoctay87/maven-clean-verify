package com.service.gateway;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Date;

@RedisHash("CacheToken")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CacheToken implements Serializable {
    private String id;
    private String roles;
    private String accessToken;
    private Date expiryDate;
}
