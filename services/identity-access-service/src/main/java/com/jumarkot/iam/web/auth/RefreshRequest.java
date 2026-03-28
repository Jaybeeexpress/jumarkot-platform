package com.jumarkot.iam.web.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
