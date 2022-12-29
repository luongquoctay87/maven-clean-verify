package com.service.authentication.model;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Date;

@RedisHash("Token")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token implements Serializable {
    private String id;
    private String roles;
    private String accessToken;
    private String refreshToken;
    private Date expiryAccessDate;
    private Date expiryRefreshDate;
}
