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
 * @author 한지원
 *
 */
@Mapper
public interface BoardMapper {
	// ID로 게시글 조회
	BoardRecord selectById(@Param("boardId") String boardId);

	// 특정 사용자의 모든 게시글 조회
	List<BoardRecord> selectListByUser(@Param("userId") String userId);

	// 전체 게시글 조회
	List<BoardRecord> selectAll();
	
	// 전체 게시글 수 조회 
	int count();

	// 페이징 조회
    List<BoardRecord> selectByPage(@Param("offset") int offset,
                                   @Param("limit") int limit);

    // 게시글 저장
	int insert(BoardRecord record);

	//게시글 수정
	int update(BoardRecord record);
	
	// 게시글 삭제
	int deleteById(@Param("boardId") String boardId);
	
}