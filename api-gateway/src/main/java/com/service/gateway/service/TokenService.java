package com.service.gateway.service;

import com.service.gateway.model.Token;
import com.service.gateway.repository.TokenRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j(topic = "TOKEN-SERVICE")
public class TokenService {
    @Autowired
    private TokenRepo tokenRepo;

    public String validateToken(String userId, String token) {
        Optional<Token> cacheToken = tokenRepo.findById(userId);
        if (cacheToken.isPresent()) {
            Token cache = cacheToken.get();
            String accessToken = cache.getAccessToken();
            String refreshToken = cache.getRefreshToken();
            Date expiryAccessDate = cache.getExpiryAccessDate();
            Date expiryRefreshDate = cache.getExpiryRefreshDate();

            if (!accessToken.equals(token) && !refreshToken.equals(token)) {
                return "invalidate";
            }

            if (expiryAccessDate.before(new Date()) && expiryRefreshDate.before(new Date())) {
                return "out of date";
            }
        }

        return "validated";
    }
}
