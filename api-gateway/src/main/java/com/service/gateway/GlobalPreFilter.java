package com.service.gateway;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "GATEWAY-FILTER")
public class GlobalPreFilter implements GatewayFilter {
    private final CacheTokenRepo cacheTokenRepo;
    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getPath().toString();
        log.info("Execute filter request {}", url);

        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return error(exchange.getResponse(), HttpStatus.UNAUTHORIZED, url, "Access denied");
        } else {
            final String token = request.getHeaders().getOrEmpty("Authorization").get(0).substring(7);

            // verify access token
            Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String userId = decodedJWT.getSubject();
            String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

            // check token in redis cache
            Optional<CacheToken> cacheToken = cacheTokenRepo.findById(userId);
            if (cacheToken.isPresent()) {
                String accessToken = cacheToken.get().getAccessToken();
                Date expiryDate = cacheToken.get().getExpiryDate();
                if (!accessToken.equals(token) && expiryDate.before(new Date())) {
                    log.info("Access token is invalidate {}", token);
                    return error(exchange.getResponse(), HttpStatus.NOT_ACCEPTABLE, url, "Access token is invalidate");
                }
            }

        }

        return chain.filter(exchange);
    }

    private Mono<Void> error(ServerHttpResponse response, HttpStatus status, String path, String message) {
        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("timestamp", new Date());
        errors.put("status", status.value());
        errors.put("path", path);
        errors.put("error", status.getReasonPhrase());
        errors.put("message", message);
        byte[] bytes = new Gson().toJson(errors).getBytes(StandardCharsets.UTF_8);

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(status);

        return response.writeWith(Mono.just(buffer));
    }
}