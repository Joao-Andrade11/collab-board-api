package com.dianabispo.collabboard.board.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCardRequest(@NotBlank String title, String description) {
}
