package com.mochimexa.ecommerce;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EcommerceApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicCatalogRegistrationLoginAndProtectedProfileShareOneContract() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProducto").isNumber())
                .andExpect(jsonPath("$[0].slug").isNotEmpty());
        mockMvc.perform(get("/api/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.envioCdmx").value(80.0))
                .andExpect(jsonPath("$.metodosPago.tarjeta").value(true));

        String email = "integracion@mochimexa.test";
        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Prueba","apellido":"Integración","correo":"%s","contrasenia":"Prueba1234","telefono":"5512345678"}
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.correo").value(email))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));

        String login = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"correo":"%s","password":"Prueba1234"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.correo").value(email))
                .andReturn().getResponse().getContentAsString();
        String token = JsonPath.read(login, "$.token");

        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value(email));
    }

    @Test
    void configuredFrontendOriginPassesCorsPreflight() throws Exception {
        mockMvc.perform(options("/api/products")
                        .header("Origin", "http://localhost:8765")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:8765"));
    }
}
