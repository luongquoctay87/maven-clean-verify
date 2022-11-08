package com.service.authentication.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.service.authentication.config.Translator;
import com.service.authentication.exception.ResourceNotFoundException;
import com.service.authentication.model.Token;
import com.service.authentication.response.ServiceResponse;
import com.service.authentication.response.TokenResponse;
import com.service.authentication.service.TokenService;
import com.service.authentication.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@Slf4j(topic = "AUTHENTICATION-CONTROLLER")
@Tag(name = "Authentication Controller")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenService tokenService;

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.token.validity}")
    private long tokenValidity;

    @Operation(summary = "Authenticated User and Generate Token")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(HttpServletRequest request, @RequestParam("username") String username, @RequestParam("password") String password) {
        log.info("Username is {} and Password is {}", username, password);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        User user = (User) authentication.getPrincipal();
        TokenResponse tokens = tokenService.generateToken(user, request.getRequestURL().toString());
        return ResponseEntity.ok(tokens);
    }

    @Operation(summary = "Refresh Token")
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(HttpServletRequest request) throws ResourceNotFoundException {
        log.info("Refreshing the token from database");

        String authorizeHeader = request.getHeader(AUTHORIZATION);
        if (authorizeHeader != null && authorizeHeader.startsWith("Bearer")) {
            String refreshToken = authorizeHeader.substring("Bearer ".length());
            Token token = tokenService.findByToken(refreshToken);
            if (token == null) {
                throw new ResourceNotFoundException(Translator.toLocale("refresh-token-not-found"));
            }

            Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(refreshToken);
            String username = decodedJWT.getSubject();
            User user = (User) userService.loadUserByUsername(username);

            List<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            String accessToken = JWT.create()
                    .withSubject(username)
                    .withExpiresAt(new Date(System.currentTimeMillis() + (tokenValidity * 60 * 1000)))
                    .withIssuer(request.getRequestURL().toString())
                    .withClaim("roles", authorities)
                    .sign(algorithm);

            TokenResponse tokens = new TokenResponse(user.getUsername(), authorities.toString(), accessToken, refreshToken);
            return ResponseEntity.ok(tokens);
        } else {
            throw new ResourceNotFoundException(Translator.toLocale("refresh-header-not-found"));
        }
    }

    @Operation(summary = "Forgot Password")
    @ApiResponses(value = {@ApiResponse(responseCode = "202", description = "Reset token has send to email successfully")})
    @PostMapping("/forgot-password")
    public ServiceResponse forgotPassword(@Parameter(description = "Email to receive Reset Token", required = true) @RequestParam("email") @Email String email) throws MessagingException, ResourceNotFoundException {
        log.info("Sending reset token to email {}", email);
        userService.sendResetTokenToEmail(email);
        return new ServiceResponse(ACCEPTED.value(), Translator.toLocale("user-forgot-password-success"));
    }

    @Operation(summary = "Reset Password")
    @PostMapping("/reset-password")
    public ServiceResponse resetPassword(@Parameter(description = "Reset Token to confirm change password", required = true) @RequestParam("token") String resetToken,
                                         @Parameter(description = "New password", required = true) @RequestParam("newPassword") String newPassword) throws ResourceNotFoundException {
        log.info("Processing reset and update new password for token {}", resetToken);
        userService.resetAndUpdatePassword(resetToken, newPassword);
        return new ServiceResponse(OK.value(), Translator.toLocale("user-change-password-success"));
    }
}
