package com.eggmoney.payv.domain.model.repository;

import java.util.Optional;

import com.eggmoney.payv.domain.model.entity.Reaction;
import com.eggmoney.payv.domain.model.vo.BoardId;
import com.eggmoney.payv.domain.model.vo.ReactionId;
import com.eggmoney.payv.domain.model.vo.UserId;

/**
 * Repository Interface: ReactionRepository 
 * - 책임: Reaction 엔티티 영속성 관리.
 * - 인터페이스만 정의하여 구현(MyBatis, JPA, Memory 등)은 인프라 레이어에서 제공.
 * 
 * @author 한지원
 *
 */
public interface ReactionRepository {
    Optional<Reaction> findByUserAndBoard(UserId userId, BoardId boardId);
    long countByBoard(BoardId boardId);
    void save(Reaction reaction);
    void delete(ReactionId id);
}

