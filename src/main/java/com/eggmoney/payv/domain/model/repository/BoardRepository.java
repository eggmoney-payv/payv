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
 * @author 한지원
 *
 */
public interface BoardRepository {
	// 식별자로 게시글 조회
    Optional<Board> findById(BoardId id);

    // 저장/갱신
    void save(Board board);

    // 전체 게시글 조회 
    List<Board> findAll();

    // 특정 사용자의 게시글 조회
    List<Board> findByUser(UserId userId);

    // 전체 게시글 수 조회 
    int count();

    // 페이징 조회
    List<Board> findByPage(int offset, int limit);
    
 // 제목, 내용, 작성자별 검색
    List<Board> findBySearch(String keyword, String searchType, int offset, int limit);

    // 제목, 내용, 작성자별 검색된 게시글 수
    int countBySearch(String keyword, String searchType);
}
