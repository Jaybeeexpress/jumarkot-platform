package com.jumarkot.contracts.identity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginRequest {
    @NotBlank @Email
    String email;

    @NotBlank @Size(min = 8)
    String password;
}
