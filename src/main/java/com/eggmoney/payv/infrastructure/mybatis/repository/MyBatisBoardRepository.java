package com.eggmoney.payv.infrastructure.mybatis.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.eggmoney.payv.domain.model.entity.Board;
import com.eggmoney.payv.domain.model.entity.BoardType;
import com.eggmoney.payv.domain.model.entity.Visibility;
import com.eggmoney.payv.domain.model.repository.BoardRepository;
import com.eggmoney.payv.domain.model.vo.BoardId;
import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.infrastructure.mybatis.mapper.BoardMapper;
import com.eggmoney.payv.infrastructure.mybatis.record.BoardRecord;

import lombok.RequiredArgsConstructor;

/**
 * Repository Implementation: MyBatisBoardRepository - 책임: BoardRepository 인터페이스
 * 구현 (MyBatis 기반). - BoardRecord ↔ Board 변환을 담당.
 * 
 * 동작 방식: - findById(): Mapper 호출 후 Domain 변환 - save(): 존재 여부 확인 후 insert/update
 * 결정 - findAll(): 전체 조회 후 Domain 변환 - findByUser(): 특정 사용자 조회 후 Domain 변환
 * 
 * @author 한지원
 *
 */
@Repository
@RequiredArgsConstructor
public class MyBatisBoardRepository implements BoardRepository {
	private final BoardMapper mapper;

	@Override
	public Optional<Board> findById(BoardId id) {
		return Optional.ofNullable(mapper.selectById(id.value())).map(this::toDomain);
	}

	@Override
	public void save(Board board) {
		BoardRecord existing = mapper.selectById(board.getId().value());
		if (existing == null) {
			mapper.insert(toRecord(board));
		} else {
			mapper.update(toRecord(board));
		}
	}

	@Override
	public List<Board> findAll() {
		return mapper.selectAll().stream().map(this::toDomain).collect(Collectors.toList());
	}

	@Override
	public List<Board> findByUser(UserId userId) {
		return mapper.selectListByUser(userId.value()).stream().map(this::toDomain).collect(Collectors.toList());
	}

	@Override
	public int count() {
		return mapper.count();
	}

	// 페이징
	@Override
	public List<Board> findByPage(int offset, int limit) {
		return mapper.selectByPage(offset, limit).stream().map(this::toDomain).collect(Collectors.toList());
	}

	// 검색 처리: 제목, 내용, 작성자 검색
	@Override
    public List<Board> findBySearch(String keyword, String searchType, int offset, int limit) {
        switch (searchType) {
        case "content":
            return mapper.selectByContent(keyword, offset, limit).stream()
                    .map(this::toDomain) // BoardRecord -> Board로 변환
                    .collect(Collectors.toList());
        case "author":
            return mapper.selectByAuthor(keyword, offset, limit).stream()
                    .map(this::toDomain) // BoardRecord -> Board로 변환
                    .collect(Collectors.toList());
        case "title":
        default:
            return mapper.selectByTitle(keyword, offset, limit).stream()
                    .map(this::toDomain) // BoardRecord -> Board로 변환
                    .collect(Collectors.toList());
        }
    }

	// 게시글 수 검색
	@Override
	public int countBySearch(String keyword, String searchType) {
		switch (searchType) {
		case "content":
			return mapper.countByContent(keyword);
		case "author":
			return mapper.countByAuthor(keyword);
		case "title":
		default:
			return mapper.countByTitle(keyword);
		}
	}
	
	@Override
	public void delete(BoardId id) {
	    mapper.deleteById(id.value());
	}


	// toDomain, toRecord
	private Board toDomain(BoardRecord record) {
		return Board.builder().id(BoardId.of(record.getBoardId())).userId(UserId.of(record.getUserId()))
//          .type(BoardType.valueOf(record.getType())) // String → Enum
				.type(record.getType() != null ? BoardType.valueOf(record.getType()) : null).title(record.getTitle())
				.content(record.getContent())
				.visibility(record.getVisibility() != null ? Visibility.valueOf(record.getVisibility()) : null)
				.viewCount(record.getViewCount()).createdAt(record.getCreatedAt()).updatedAt(record.getUpdatedAt())

				.build();
	}

	private BoardRecord toRecord(Board board) {
		return BoardRecord.builder().boardId(board.getId().value()).userId(board.getUserId().value())
				.type(board.getType() != null ? board.getType().name() : null).title(board.getTitle())
				.content(board.getContent())
				.visibility(board.getVisibility() != null ? board.getVisibility().name() : null)
				.viewCount(board.getViewCount()).createdAt(board.getCreatedAt()).updatedAt(board.getUpdatedAt())
//            .type(board.getType().name())          // Enum → String
//            .visibility(board.getVisibility().name()) // Enum → String
				.build();
	}
}