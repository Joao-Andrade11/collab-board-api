package com.dianabispo.collabboard.security;

import com.dianabispo.collabboard.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityTest extends IntegrationTest {

    @Test
    void protectedEndpoint_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/boards"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withMalformedToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/boards")
                        .header("Authorization", "Bearer not-a-real-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withTokenForDeletedOrUnknownUser_returnsUnauthorized() throws Exception {
        // A syntactically valid-looking but unsigned/foreign token must not authenticate.
        String bogusToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnaG9zdCJ9.invalidSignature";

        mockMvc.perform(get("/api/boards")
                        .header("Authorization", "Bearer " + bogusToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authEndpoints_areAccessibleWithoutToken() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "noauth",
                                  "email": "noauth@example.com",
                                  "password": "password123",
                                  "displayName": "No Auth"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void demoClient_isAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoint_withValidToken_returnsOk() throws Exception {
        AuthedUser user = registerNewUser();

        mockMvc.perform(get("/api/boards")
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk());
    }
}
