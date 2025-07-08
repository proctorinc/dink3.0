package com.dink3.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterLoginAndMe() throws Exception {
        String email = "test" + System.currentTimeMillis() + "@example.com";
        String username = "testuser" + System.currentTimeMillis();
        String password = "Password123";
        // Register
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", email,
                        "username", username,
                        "password", password
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
        // Login
        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", email,
                        "password", password
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(loginResponse).get("token").asText();
        // Access /me
        mockMvc.perform(get("/api/v1/users/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(email)));
    }

    @Test
    void testDuplicateEmailRegistration() throws Exception {
        String email = "duplicate" + System.currentTimeMillis() + "@example.com";
        String username = "testuser";
        String password = "Password123";
        
        // First registration should succeed
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", email,
                        "username", username,
                        "password", password
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
        
        // Second registration with same email should fail
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", email,
                        "username", "differentuser",
                        "password", "Password456"
                ))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Unable to register with the provided information")));
    }
} 