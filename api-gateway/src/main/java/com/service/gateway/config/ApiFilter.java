package com.service.gateway.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.service.gateway.model.CacheToken;
import com.service.gateway.service.CacheTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Slf4j(topic = "API-FILTER")
public class ApiFilter extends AbstractGatewayFilterFactory<ApiFilter.Config> {

    private final CacheTokenService tokenService;
    private final String HEADER_API_KEY = "X-Api-Key";
    private final String HEADER_API_VERSION = "X-Api-Version";
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${header.api.key:apiKey}")
    private String apiKey;
    @Value("${header.api.version:1.0}")
    private String apiVersion;

    public ApiFilter(CacheTokenService tokenService) {
        super(Config.class);
        this.tokenService = tokenService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        //Custom Pre Filter. Suppose we can extract JWT and perform Authentication
        return (exchange, chain) -> {
            log.info("Processing API filter request");

            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();

            if (request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                String apiVersionHeader = request.getHeaders().containsKey(HEADER_API_VERSION) ? request.getHeaders().get(HEADER_API_VERSION).get(0) : HEADER_API_VERSION;
                if (!apiVersionHeader.contentEquals(apiVersion)) {
                    return error(exchange.getResponse(), HttpStatus.NOT_ACCEPTABLE, path, "Invalid header: " + HEADER_API_VERSION);
                }

                final String token = request.getHeaders().getOrEmpty("Authorization").get(0).substring(7);

                // verify access token
                Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);
                String userId = decodedJWT.getSubject();
                String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

                // check token in redis cache
                CacheToken cacheToken = tokenService.findById(userId);
                if (cacheToken != null) {
                    String accessToken = cacheToken.getAccessToken();
                    Date expiryDate = cacheToken.getExpiryDate();
                    if (!accessToken.equals(token) && expiryDate.before(new Date())) {
                        log.info("Access token is invalidate {}", token);
                        return error(exchange.getResponse(), HttpStatus.NOT_ACCEPTABLE, path, "Access token is invalidate");
                    }
                }
            } else {
                String apiKeyHeader = request.getHeaders().containsKey(HEADER_API_KEY) ? request.getHeaders().get(HEADER_API_KEY).get(0) : HEADER_API_KEY;
                if (!request.getHeaders().containsKey(HEADER_API_KEY) || !apiKeyHeader.contentEquals(apiKey)) {
                    return error(exchange.getResponse(), HttpStatus.NOT_ACCEPTABLE, path, "Invalid header: " + HEADER_API_KEY);
                }
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Filter request successfully");
            }));
        };
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

    public static class Config {
        // Put the configuration properties
    }
}
