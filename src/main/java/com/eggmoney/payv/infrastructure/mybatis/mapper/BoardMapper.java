package com.eggmoney.payv.infrastructure.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.eggmoney.payv.infrastructure.mybatis.record.BoardRecord;

/**
 *  * MyBatis Mapper: BoardMapper
 * - DB와 직접 SQL 매핑 담당.
 * - BoardRecord를 기준으로 CRUD 수행.
 * 
 * 주요 기능:
 *   - selectById(): ID로 게시글 조회
 *   - selectListByUser(): 특정 사용자 게시글 목록 조회
 *   - selectAll(): 전체 게시글 조회
 *   - insert(): 게시글 저장
 *   - update(): 게시글 수정
 * 
 * @author 한지원
 *
 */
@Mapper
public interface BoardMapper {
	BoardRecord selectById(@Param("boardId") String boardId);

	// 특정 사용자의 모든 게시글 조회
	List<BoardRecord> selectListByUser(@Param("userId") String userId);

	// 전체 게시글 조회 (옵션)
	List<BoardRecord> selectAll();

	int insert(BoardRecord record);

	int update(BoardRecord record);
	
//	int deleteById(@Param("boardId") String boardId);
	
}