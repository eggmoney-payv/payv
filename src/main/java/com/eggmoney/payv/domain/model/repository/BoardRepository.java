package com.eggmoney.payv.domain.model.repository;

import java.util.List;
import java.util.Optional;

import com.eggmoney.payv.domain.model.entity.Board;
import com.eggmoney.payv.domain.model.vo.BoardId;
import com.eggmoney.payv.domain.model.vo.UserId;

/**
 * Repository Interface: BoardRepository
 * - 책임: Board 엔티티 영속성 관리.
 * - 인터페이스만 정의하여 구현(MyBatis, JPA, Memory 등)은 인프라 레이어에서 제공.
 * 
 * 주요 기능:
 *   - findById(): 식별자로 게시글 조회
 *   - save(): 저장/갱신
 *   - findAll(): 전체 게시글 조회
 *   - findByUser(): 특정 사용자의 게시글 조회
 * 
 * @author 한지원
 *
 */
public interface BoardRepository {
	Optional<Board> findById(BoardId id);

	void save(Board board);

	// 전체 게시글 조회
	List<Board> findAll();

	// 특정 유저 게시글 조회
	List<Board> findByUser(UserId userId);
}