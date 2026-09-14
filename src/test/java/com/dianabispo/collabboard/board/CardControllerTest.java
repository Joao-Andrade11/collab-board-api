package com.dianabispo.collabboard.board;

import com.dianabispo.collabboard.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CardControllerTest extends IntegrationTest {

    @Test
    void create_addsCardToTodoColumn() throws Exception {
        AuthedUser user = registerNewUser();
        Long boardId = createBoard(user.token(), "Board A");

        mockMvc.perform(post("/api/boards/{boardId}/cards", boardId)
                        .header("Authorization", "Bearer " + user.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Write tests", "description": "cover the happy path"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.position").value(0))
                .andExpect(jsonPath("$.title").value("Write tests"));
    }

    @Test
    void create_withoutToken_returnsUnauthorized() throws Exception {
        AuthedUser owner = registerNewUser();
        Long boardId = createBoard(owner.token(), "Board A");

        mockMvc.perform(post("/api/boards/{boardId}/cards", boardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "No auth"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void update_changesTitleAndDescription() throws Exception {
        AuthedUser user = registerNewUser();
        Long boardId = createBoard(user.token(), "Board A");
        Long cardId = createCard(user.token(), boardId, "Original title");

        mockMvc.perform(put("/api/boards/{boardId}/cards/{cardId}", boardId, cardId)
                        .header("Authorization", "Bearer " + user.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Updated title", "description": "updated"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.description").value("updated"));
    }

    @Test
    void move_updatesStatusAndPosition() throws Exception {
        AuthedUser user = registerNewUser();
        Long boardId = createBoard(user.token(), "Board A");
        Long cardId = createCard(user.token(), boardId, "Card to move");

        mockMvc.perform(patch("/api/boards/{boardId}/cards/{cardId}/move", boardId, cardId)
                        .header("Authorization", "Bearer " + user.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "DONE", "position": 0}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void delete_removesCardFromBoard() throws Exception {
        AuthedUser user = registerNewUser();
        Long boardId = createBoard(user.token(), "Board A");
        Long cardId = createCard(user.token(), boardId, "Card to delete");

        mockMvc.perform(delete("/api/boards/{boardId}/cards/{cardId}", boardId, cardId)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/boards/{boardId}/cards", boardId)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void update_cardFromAnotherBoard_returnsNotFound() throws Exception {
        AuthedUser user = registerNewUser();
        Long boardOne = createBoard(user.token(), "Board One");
        Long boardTwo = createBoard(user.token(), "Board Two");
        Long cardOnBoardOne = createCard(user.token(), boardOne, "Belongs to board one");

        mockMvc.perform(put("/api/boards/{boardId}/cards/{cardId}", boardTwo, cardOnBoardOne)
                        .header("Authorization", "Bearer " + user.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Should not apply"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_onNonExistentBoard_returnsNotFound() throws Exception {
        AuthedUser user = registerNewUser();

        mockMvc.perform(post("/api/boards/{boardId}/cards", 999_999)
                        .header("Authorization", "Bearer " + user.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Ghost board"}
                                """))
                .andExpect(status().isNotFound());
    }

    private Long createCard(String token, Long boardId, String title) throws Exception {
        var result = mockMvc.perform(post("/api/boards/{boardId}/cards", boardId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TitleOnly(title))))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private record TitleOnly(String title) {
    }
}
