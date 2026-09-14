package com.dianabispo.collabboard.board;

import com.dianabispo.collabboard.board.dto.CardResponse;
import com.dianabispo.collabboard.board.dto.CreateCardRequest;
import com.dianabispo.collabboard.board.dto.MoveCardRequest;
import com.dianabispo.collabboard.board.dto.UpdateCardRequest;
import com.dianabispo.collabboard.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/boards/{boardId}/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping
    public List<CardResponse> list(@PathVariable Long boardId) {
        return cardService.listByBoard(boardId);
    }

    @PostMapping
    public ResponseEntity<CardResponse> create(
            @PathVariable Long boardId,
            @Valid @RequestBody CreateCardRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cardService.create(boardId, request, principal.getId()));
    }

    @PutMapping("/{cardId}")
    public CardResponse update(
            @PathVariable Long boardId,
            @PathVariable Long cardId,
            @Valid @RequestBody UpdateCardRequest request) {
        return cardService.update(boardId, cardId, request);
    }

    @PatchMapping("/{cardId}/move")
    public CardResponse move(
            @PathVariable Long boardId,
            @PathVariable Long cardId,
            @Valid @RequestBody MoveCardRequest request) {
        return cardService.move(boardId, cardId, request);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> delete(@PathVariable Long boardId, @PathVariable Long cardId) {
        cardService.delete(boardId, cardId);
        return ResponseEntity.noContent().build();
    }
}
