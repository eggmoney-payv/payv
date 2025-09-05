package com.eggmoney.payv.domain.model.entity;

import com.eggmoney.payv.domain.model.vo.BoardId;
import com.eggmoney.payv.domain.model.vo.ReactionId;
import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.domain.shared.util.EntityIdentifier;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Reaction {
    private ReactionId id;
    private BoardId boardId;
    private UserId userId;
    private ReactionType type;

    @Builder
    public Reaction(ReactionId id, BoardId boardId, UserId userId, ReactionType type) {
        this.id = id;
        this.boardId = boardId;
        this.userId = userId;
        this.type = type;
    }

    public static Reaction like(BoardId boardId, UserId userId) {
        return Reaction.builder()
                .id(ReactionId.of(EntityIdentifier.generateUuid()))
                .boardId(boardId)
                .userId(userId)
                .type(ReactionType.LIKE)
                .build();
    }
}
