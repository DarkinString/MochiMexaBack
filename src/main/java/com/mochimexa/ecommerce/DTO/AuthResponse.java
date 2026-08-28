package com.mochimexa.ecommerce.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private String type;
    private long expiresIn;
    private UserResponseDTO user;

    public AuthResponse(String token, String type, long expiresIn, UserResponseDTO user) {
        this.token = token;
        this.type = type;
        this.expiresIn = expiresIn;
        this.user = user;
    }
}
