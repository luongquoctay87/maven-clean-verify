package com.service.authentication.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.service.authentication.model.CacheToken;
import com.service.authentication.repository.CacheTokenRepo;
import com.service.authentication.response.TokenResponse;
import com.service.authentication.exception.ResourceNotFoundException;
import com.service.authentication.model.AppUser;
import com.service.authentication.model.Token;
import com.service.authentication.repository.TokenRepo;
import com.service.authentication.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.service.authentication.util.ApiConst.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "TOKEN-SERVICE")
public class TokenService {

    private final TokenRepo tokenRepo;
    private final CacheTokenRepo cacheTokenRepo;
    private final UserRepo userRepo;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.token.validity}")
    private long tokenValidity;

    @Value("${jwt.refresh.token.validity}")
    private long refreshTokenValidity;

    @Value("${jwt.reset.token.validity}")
    private long resetTokenValidity;

    /**
     * Generate token information
     * @param user
     * @param url
     * @return
     */
    public TokenResponse generateToken(User user, String url) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
        List<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        Date expiryDate = new Date(System.currentTimeMillis() + (tokenValidity * 60 * 1000));
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(expiryDate)
                .withIssuer(url)
                .withClaim("roles", authorities)
                .sign(algorithm);

        Date refreshDate = new Date(System.currentTimeMillis() + (refreshTokenValidity * 24 * 60 * 60 * 1000));
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(refreshDate)
                .withIssuer(url)
                .sign(algorithm);

        // save refresh token into database
        saveRefreshToken(user.getUsername(), refreshToken, refreshDate);

        // save access token to redis cache
        CacheToken cacheToken = new CacheToken(user.getUsername(), authorities.toString(), accessToken, expiryDate);
        cacheTokenRepo.save(cacheToken);

        return TokenResponse.builder()
                .username(user.getUsername())
                .roles(authorities.toString())
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .build();
    }

    /**
     * Find token by token string
     * @param token
     * @return
     */
    public Token findByToken(String token) {
        return tokenRepo.findByToken(token);
    }

    /**
     * Save refresh token after login successful
     *
     * @param username
     * @param refreshToken
     * @param expiryDate
     */
    public void saveRefreshToken(String username, String refreshToken, Date expiryDate) {
        log.info("Saving refresh token to the database");

        AppUser appUser = userRepo.findByUsername(username);

        Token ref = tokenRepo.findByUserId(appUser.getId());
        if (ref != null) {
            ref.setToken(refreshToken);
            ref.setExpiryDate(expiryDate);
            tokenRepo.save(ref);
        } else {
            Token token = Token.builder()
                    .user(appUser)
                    .token(refreshToken)
                    .expiryDate(expiryDate)
                    .build();
            tokenRepo.save(token);
        }
    }

    /**
     * Save reset password token
     *
     * @param appUser
     * @param resetToken Token will be expired after 1 hour
     * @throws ResourceNotFoundException
     */
    public void saveResetToken(AppUser appUser, String resetToken) throws ResourceNotFoundException {
        // Will be expired after 1 hour
        Date expiryDate = new Date(System.currentTimeMillis() + (resetTokenValidity * 60 * 1000));

        Token token = tokenRepo.findByUserId(appUser.getId());
        if (token == null) {
            Token refreshToken = Token.builder()
                    .user(appUser)
                    .token(resetToken)
                    .expiryDate(expiryDate)
                    .build();
            tokenRepo.save(refreshToken);
        } else {
            token.setToken(resetToken);
            token.setExpiryDate(expiryDate);
            tokenRepo.save(token);
        }
    }
}
