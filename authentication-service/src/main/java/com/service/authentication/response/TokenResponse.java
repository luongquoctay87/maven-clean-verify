package com.service.authentication.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class TokenResponse implements Serializable {
    @Schema(description = "Username has authenticated")
    private String username;

    @Schema(description = "User roles & permissions")
    private String roles;

    @Schema(description = "Access Token")
    private String access_token;

    @Schema(description = "Refresh token reserved for next authenticated (do not require login)")
    private String refresh_token;
}
