package com.eggmoney.payv.application.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.eggmoney.payv.domain.model.entity.Reaction;
import com.eggmoney.payv.domain.model.repository.ReactionRepository;
import com.eggmoney.payv.domain.model.vo.BoardId;
import com.eggmoney.payv.domain.model.vo.UserId;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReactionAppService {
    private final ReactionRepository reactionRepository;

    public void toggleLike(BoardId boardId, UserId userId) {
        Optional<Reaction> existing = reactionRepository.findByUserAndBoard(userId, boardId);
        if (existing.isPresent()) {
            reactionRepository.delete(existing.get().getId());
        } else {
            Reaction reaction = Reaction.like(boardId, userId);
            reactionRepository.save(reaction);
        }
    }

    public long getLikeCount(BoardId boardId) {
        return reactionRepository.countByBoard(boardId);
    }
}
