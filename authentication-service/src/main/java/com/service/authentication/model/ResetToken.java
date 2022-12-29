package com.service.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Date;

@RedisHash("ResetToken")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ResetToken implements Serializable {
    private String id;
    private String email;
    private Date expiryDate;
}
