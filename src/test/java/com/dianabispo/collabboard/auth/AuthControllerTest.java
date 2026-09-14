package com.dianabispo.collabboard.auth;

import com.dianabispo.collabboard.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends IntegrationTest {

    private static final String REGISTER_BODY = """
            {
              "username": "alice",
              "email": "alice@example.com",
              "password": "password123",
              "displayName": "Alice"
            }
            """;

    @Test
    void register_returnsTokenAndCreatesUser() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    void register_duplicateUsername_returnsConflict() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(REGISTER_BODY));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "email": "another@example.com",
                                  "password": "password123",
                                  "displayName": "Alice 2"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void register_duplicateEmail_returnsConflict() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(REGISTER_BODY));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice2",
                                  "email": "alice@example.com",
                                  "password": "password123",
                                  "displayName": "Alice 2"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void register_blankUsername_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "",
                                  "email": "blank@example.com",
                                  "password": "password123",
                                  "displayName": "Blank"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withCorrectCredentials_returnsToken() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(REGISTER_BODY));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "alice", "password": "password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_withWrongPassword_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(REGISTER_BODY));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "alice", "password": "wrong-password"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_withUnknownUsername_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "ghost", "password": "password123"}
                                """))
                .andExpect(status().isUnauthorized());
    }
}
