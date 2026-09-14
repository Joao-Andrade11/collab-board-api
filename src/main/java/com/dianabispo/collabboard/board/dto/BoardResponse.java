package com.dianabispo.collabboard.board.dto;

import com.dianabispo.collabboard.board.Board;

import java.time.Instant;

public record BoardResponse(Long id, String name, Long createdBy, Instant createdAt) {

    public static BoardResponse from(Board board) {
        return new BoardResponse(board.getId(), board.getName(), board.getCreatedBy(), board.getCreatedAt());
    }
}
