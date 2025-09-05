package com.eggmoney.payv.infrastructure.mybatis.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.eggmoney.payv.domain.model.entity.Reaction;
import com.eggmoney.payv.domain.model.entity.ReactionType;
import com.eggmoney.payv.domain.model.repository.ReactionRepository;
import com.eggmoney.payv.domain.model.vo.BoardId;
import com.eggmoney.payv.domain.model.vo.ReactionId;
import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.infrastructure.mybatis.mapper.ReactionMapper;
import com.eggmoney.payv.infrastructure.mybatis.record.ReactionRecord;

import lombok.RequiredArgsConstructor;

/**
 * Repository Implementation: MyBatisReactionRepository
 * - 책임: ReactionRepository 인터페이스 구현 (MyBatis 기반).
 * - ReactionRecord ↔ Reaction 변환을 담당.
 * 
 * @author 한지원
 */

@Repository
@RequiredArgsConstructor
public class MyBatisReactionRepository implements ReactionRepository {

    private final ReactionMapper mapper;

    @Override
    public Optional<Reaction> findByUserAndBoard(UserId userId, BoardId boardId) {
        return Optional.ofNullable(mapper.selectByUserAndBoard(userId.value(), boardId.value()))
                       .map(this::toDomain);
    }

    @Override
    public long countByBoard(BoardId boardId) {
        return mapper.countByBoard(boardId.value());
    }

    @Override
    public void save(Reaction reaction) {
        // Reaction은 단순 PK 기반 insert (update 개념 없음 → like toggle 시 delete/insert)
        mapper.insert(toRecord(reaction));
    }

    @Override
    public void delete(ReactionId id) {
        mapper.delete(id.value());
    }

    // === 변환 메서드 ===
    private Reaction toDomain(ReactionRecord record) {
        return Reaction.builder()
                .id(ReactionId.of(record.getReactionId()))
                .boardId(BoardId.of(record.getBoardId()))
                .userId(UserId.of(record.getUserId()))
                .type(record.getType() != null ? ReactionType.valueOf(record.getType()) : null)
                .build();
    }

    private ReactionRecord toRecord(Reaction reaction) {
        return ReactionRecord.builder()
                .reactionId(reaction.getId().value())
                .boardId(reaction.getBoardId().value())
                .userId(reaction.getUserId().value())
                .type(reaction.getType() != null ? reaction.getType().name() : null)
                .build();
    }
}