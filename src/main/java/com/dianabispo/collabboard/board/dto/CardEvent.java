package com.dianabispo.collabboard.board.dto;

public record CardEvent(Type type, CardResponse card, Long cardId) {

    public enum Type {
        CREATED, UPDATED, MOVED, DELETED
    }

    public static CardEvent created(CardResponse card) {
        return new CardEvent(Type.CREATED, card, card.id());
    }

    public static CardEvent updated(CardResponse card) {
        return new CardEvent(Type.UPDATED, card, card.id());
    }

    public static CardEvent moved(CardResponse card) {
        return new CardEvent(Type.MOVED, card, card.id());
    }

    public static CardEvent deleted(Long cardId) {
        return new CardEvent(Type.DELETED, null, cardId);
    }
}
