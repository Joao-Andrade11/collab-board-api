package com.dianabispo.collabboard.board.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCardRequest(@NotBlank String title, String description) {
}
