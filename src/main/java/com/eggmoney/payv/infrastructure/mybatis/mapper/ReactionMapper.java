package com.eggmoney.payv.infrastructure.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.eggmoney.payv.infrastructure.mybatis.record.CommentRecord;
import com.eggmoney.payv.infrastructure.mybatis.record.ReactionRecord;

/**
 *  * MyBatis Mapper: ReactionMapper
 * - DB와 직접 SQL 매핑 담당.
 * - ReactionRecord를 기준으로 CRUD 수행.
 * 
 * @author 한지원
 *
 */
@Mapper
public interface ReactionMapper {

    ReactionRecord selectByUserAndBoard(@Param("userId") String userId,
                                        @Param("boardId") String boardId);

    long countByBoard(@Param("boardId") String boardId);

    int insert(ReactionRecord record);

    int delete(@Param("reactionId") String reactionId);
}
