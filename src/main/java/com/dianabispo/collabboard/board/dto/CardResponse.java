package com.dianabispo.collabboard.board.dto;

import com.dianabispo.collabboard.board.Card;
import com.dianabispo.collabboard.board.CardStatus;

import java.time.Instant;

public record CardResponse(
        Long id,
        Long boardId,
        String title,
        String description,
        CardStatus status,
        Integer position,
        Long createdBy,
        Instant createdAt,
        Instant updatedAt) {

    public static CardResponse from(Card card) {
        return new CardResponse(
                card.getId(),
                card.getBoardId(),
                card.getTitle(),
                card.getDescription(),
                card.getStatus(),
                card.getPosition(),
                card.getCreatedBy(),
                card.getCreatedAt(),
                card.getUpdatedAt());
    }
}
