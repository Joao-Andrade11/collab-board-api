package com.dianabispo.collabboard.board;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByBoardIdOrderByStatusAscPositionAsc(Long boardId);

    long countByBoardIdAndStatus(Long boardId, CardStatus status);
}
