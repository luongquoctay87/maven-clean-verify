package com.service.authentication.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.service.authentication.config.Translator;
import com.service.authentication.exception.ResourceNotFoundException;
import com.service.authentication.model.ResetToken;
import com.service.authentication.model.Token;
import com.service.authentication.repository.ResetTokenRepo;
import com.service.authentication.repository.TokenRepo;
import com.service.authentication.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "TOKEN-SERVICE")
@RequiredArgsConstructor
public class TokenService {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.token.validity}")
    private long tokenValidity;
    @Value("${jwt.refresh.token.validity}")
    private long refreshTokenValidity;

    private final TokenRepo tokenRepo;
    private final ResetTokenRepo resetTokenRepo;

    /**
     * Generate token information
     *
     * @param user
     * @param url
     * @return
     */
    public TokenResponse generateToken(User user, String url) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
        List<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        Date expiryAccessDate = new Date(System.currentTimeMillis() + (tokenValidity * 60 * 1000));
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(expiryAccessDate)
                .withIssuer(url)
                .withClaim("roles", authorities)
                .sign(algorithm);

        Date expiryRefreshDate = new Date(System.currentTimeMillis() + (refreshTokenValidity * 24 * 60 * 60 * 1000));
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(expiryRefreshDate)
                .withIssuer(url)
                .sign(algorithm);

        // save access token to redis cache
        Token token = Token.builder()
                .id(user.getUsername())
                .roles(authorities.toString())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiryAccessDate(expiryAccessDate)
                .expiryRefreshDate(expiryRefreshDate)
                .build();
        saveAccessToken(token);

        return TokenResponse.builder()
                .username(user.getUsername())
                .roles(authorities.toString())
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .build();
    }

    /**
     * Save token
     *
     * @param token token info
     */
    public void saveAccessToken(Token token) {
        tokenRepo.save(token);
    }

    /**
     * Get token by id
     *
     * @param id username
     * @return access token otherwise throw exception
     * @throws ResourceNotFoundException
     */
    public Token getTokenById(String id) throws ResourceNotFoundException {
        Optional<Token> token = tokenRepo.findById(id);
        if (token.isEmpty()) {
            throw new ResourceNotFoundException(Translator.toLocale("access-token-not-found"));
        } else {
            return token.get();
        }
    }

    /**
     * Save Reset Token
     * @param resetToken token
     * @param email email of user
     * @param expiryDate expire date
     */
    public void saveResetToken(String resetToken, String email, Date expiryDate) {
        resetTokenRepo.save(new ResetToken(resetToken, email, expiryDate));
    }

    /**
     * Find token by token string
     * @param resetToken reset token
     * @return email
     */
    public String validateResetToken(String resetToken) throws ResourceNotFoundException {
        Optional<ResetToken> token = resetTokenRepo.findById(resetToken);
        if (token.isEmpty()) {
            throw new ResourceNotFoundException(Translator.toLocale("reset-token-not-found"));
        } else if (token.get().getExpiryDate().before(new Date())){
            throw new ResourceNotFoundException(Translator.toLocale("reset-token-expired"));
        }
        return token.get().getEmail();
    }
}
