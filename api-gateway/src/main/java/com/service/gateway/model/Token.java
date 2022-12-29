package com.service.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Date;

@RedisHash("Token")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Token implements Serializable {
    private String id;
    private String roles;
    private String accessToken;
    private String refreshToken;
    private Date expiryAccessDate;
    private Date expiryRefreshDate;
}
