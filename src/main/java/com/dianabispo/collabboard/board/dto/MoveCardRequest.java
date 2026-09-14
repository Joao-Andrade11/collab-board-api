package com.dianabispo.collabboard.board.dto;

import com.dianabispo.collabboard.board.CardStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record MoveCardRequest(@NotNull CardStatus status, @NotNull @PositiveOrZero Integer position) {
}
