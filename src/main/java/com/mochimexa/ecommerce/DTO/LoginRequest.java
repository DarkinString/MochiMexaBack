package com.mochimexa.ecommerce.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String correo;
    private String password;

    public LoginRequest() {}

    public LoginRequest(String correo, String password) {
        this.correo = correo;
        this.password = password;
    }
}