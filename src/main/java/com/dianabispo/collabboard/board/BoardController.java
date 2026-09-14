package com.dianabispo.collabboard.board;

import com.dianabispo.collabboard.board.dto.BoardResponse;
import com.dianabispo.collabboard.board.dto.CreateBoardRequest;
import com.dianabispo.collabboard.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardResponse> create(
            @Valid @RequestBody CreateBoardRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.create(request, principal.getId()));
    }

    @GetMapping
    public List<BoardResponse> listAll() {
        return boardService.listAll();
    }

    @GetMapping("/{boardId}")
    public BoardResponse get(@PathVariable Long boardId) {
        return boardService.get(boardId);
    }
}
