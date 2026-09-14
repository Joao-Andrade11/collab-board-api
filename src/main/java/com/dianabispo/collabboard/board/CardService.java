package com.dianabispo.collabboard.board;

import com.dianabispo.collabboard.board.dto.CardEvent;
import com.dianabispo.collabboard.board.dto.CardResponse;
import com.dianabispo.collabboard.board.dto.CreateCardRequest;
import com.dianabispo.collabboard.board.dto.MoveCardRequest;
import com.dianabispo.collabboard.board.dto.UpdateCardRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final BoardService boardService;
    private final SimpMessagingTemplate messagingTemplate;

    public List<CardResponse> listByBoard(Long boardId) {
        boardService.findOrThrow(boardId);
        return cardRepository.findByBoardIdOrderByStatusAscPositionAsc(boardId).stream()
                .map(CardResponse::from)
                .toList();
    }

    public CardResponse create(Long boardId, CreateCardRequest request, Long userId) {
        boardService.findOrThrow(boardId);
        int nextPosition = (int) cardRepository.countByBoardIdAndStatus(boardId, CardStatus.TODO);

        Card card = Card.builder()
                .boardId(boardId)
                .title(request.title())
                .description(request.description())
                .status(CardStatus.TODO)
                .position(nextPosition)
                .createdBy(userId)
                .build();

        CardResponse response = CardResponse.from(cardRepository.save(card));
        broadcast(boardId, CardEvent.created(response));
        return response;
    }

    public CardResponse update(Long boardId, Long cardId, UpdateCardRequest request) {
        Card card = findInBoardOrThrow(boardId, cardId);
        card.setTitle(request.title());
        card.setDescription(request.description());
        card.setUpdatedAt(Instant.now());

        CardResponse response = CardResponse.from(cardRepository.save(card));
        broadcast(boardId, CardEvent.updated(response));
        return response;
    }

    public CardResponse move(Long boardId, Long cardId, MoveCardRequest request) {
        Card card = findInBoardOrThrow(boardId, cardId);
        card.setStatus(request.status());
        card.setPosition(request.position());
        card.setUpdatedAt(Instant.now());

        CardResponse response = CardResponse.from(cardRepository.save(card));
        broadcast(boardId, CardEvent.moved(response));
        return response;
    }

    public void delete(Long boardId, Long cardId) {
        Card card = findInBoardOrThrow(boardId, cardId);
        cardRepository.delete(card);
        broadcast(boardId, CardEvent.deleted(cardId));
    }

    private Card findInBoardOrThrow(Long boardId, Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"));
        if (!card.getBoardId().equals(boardId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found on this board");
        }
        return card;
    }

    private void broadcast(Long boardId, CardEvent event) {
        messagingTemplate.convertAndSend("/topic/boards/" + boardId, event);
    }
}
