package com.dianabispo.collabboard.auth;

public record AuthResponse(String token, Long userId, String username, String displayName) {
}
