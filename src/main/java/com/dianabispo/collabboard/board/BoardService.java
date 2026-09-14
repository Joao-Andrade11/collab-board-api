package com.dianabispo.collabboard.board;

import com.dianabispo.collabboard.board.dto.BoardResponse;
import com.dianabispo.collabboard.board.dto.CreateBoardRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardResponse create(CreateBoardRequest request, Long userId) {
        Board board = Board.builder()
                .name(request.name())
                .createdBy(userId)
                .build();
        return BoardResponse.from(boardRepository.save(board));
    }

    public List<BoardResponse> listAll() {
        return boardRepository.findAll().stream().map(BoardResponse::from).toList();
    }

    public BoardResponse get(Long boardId) {
        return BoardResponse.from(findOrThrow(boardId));
    }

    Board findOrThrow(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));
    }
}
