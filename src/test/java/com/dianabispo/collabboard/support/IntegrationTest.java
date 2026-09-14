package com.dianabispo.collabboard.support;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Base for controller-level integration tests: boots the full Spring context against
 * the H2 in-memory datasource configured in src/test/resources/application.properties,
 * and exposes helpers for the register -> login -> create board flow every test needs.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class IntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected record AuthedUser(Long id, String username, String token) {
    }

    protected AuthedUser registerNewUser() throws Exception {
        String username = "user_" + UUID.randomUUID().toString().substring(0, 8);
        String body = """
                {
                  "username": "%s",
                  "email": "%s@example.com",
                  "password": "password123",
                  "displayName": "%s"
                }
                """.formatted(username, username, username);

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return new AuthedUser(json.get("userId").asLong(), json.get("username").asText(), json.get("token").asText());
    }

    protected Long createBoard(String token, String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/boards")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NameOnly(name))))
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private record NameOnly(String name) {
    }
}
