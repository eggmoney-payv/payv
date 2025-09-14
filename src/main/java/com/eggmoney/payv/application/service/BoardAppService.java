package com.eggmoney.payv.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eggmoney.payv.domain.model.entity.Board;
import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.repository.BoardRepository;
import com.eggmoney.payv.domain.model.repository.UserRepository;
import com.eggmoney.payv.domain.model.vo.BoardId;
import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.domain.shared.error.DomainException;

import lombok.RequiredArgsConstructor;

/**
 * Application Service: BoardAppService - 책임: Board 관련 유스케이스(application logic)
 * 제공. - 도메인 엔티티(User, Board)와 Repository를 조합하는 역할. - 트랜잭션 경계로 동작할 수 있음.
 * 
 * @author 한지원
 *
 */
@Service
@RequiredArgsConstructor
public class BoardAppService {

	private final UserRepository userRepository;
	private final BoardRepository boardRepository;

	// 게시글 생성
	// 1) 작성자 존재 확인
	// 2) Board 엔티티 생성
	// 3) 저장 후 반환
	public Board createBoard(User author, String title, String content) {
		// 작성자 존재 유무 확인
		userRepository.findById(author.getId()).orElseThrow(() -> new DomainException("Author not found"));

		Board board = Board.create(author.getId(), title, content);
		boardRepository.save(board);
		return board;
	}
	// BoardAppService 클래스 내부에 추가
	public Board createBoardByUserId(String userId, String title, String content) {
	    User user = userRepository.findById(UserId.of(userId))
	                              .orElseThrow(() -> new DomainException("Author not found"));
	    return createBoard(user, title, content);
	}

	// 게시글 수정
	// 1) 게시글 존재 확인
	// 2) 수정 권한 확인 (작성자만 가능)
	// 3) 제목/내용 업데이트 후 저장
	public Board updateBoard(BoardId boardId, String newTitle, String newContent, UserId editorId) {
		// 게시글 존재 유무 확인
		Board board = boardRepository.findById(boardId).orElseThrow(() -> new DomainException("Board  not found"));

		// 수정 권한 확인 (작성자 본인만)
		if (!board.getUserId().equals(editorId)) {
			throw new DomainException("게시글 작성자만 수정 가능합니다.");
		}

		board.update(newTitle, newContent);
		boardRepository.save(board);

		return board;
	}
	
	// 오버로드 추가 (권한 체크 생략 버전)
	public Board updateBoard(BoardId boardId, String newTitle, String newContent) {
	    Board board = boardRepository.findById(boardId)
	            .orElseThrow(() -> new DomainException("Board not found"));
	    board.update(newTitle, newContent);
	    boardRepository.save(board);
	    return board;
	}

	//삭제
	public void deleteBoard(BoardId boardId) {
	    Board board = boardRepository.findById(boardId)
	            .orElseThrow(() -> new DomainException("Board not found"));
	    boardRepository.delete(boardId);
	}

	// 단건 조회
	public Board getBoard(BoardId id) {
		return boardRepository.findById(id)
				.orElseThrow(() -> new DomainException("board not found"));
	}

	// 전체 조회
	public List<Board> getAllBoards() {
		return boardRepository.findAll();
	}
	
	// 전체 게시글 수 조회 
	public int getBoardCount() {
	    return boardRepository.count();
	}

	// 패이징 조회
	public List<Board> getBoardsByPage(int offset, int limit) {
	    return boardRepository.findByPage(offset, limit);
	}
	
	// 검색된 게시글 목록
	public List<Board> getBoardsBySearch(String keyword, String searchType, int offset, int limit) {
	    List<Board> boards = boardRepository.findBySearch(keyword, searchType, offset, limit);
	    if (boards.isEmpty()) {
	    	 return boards; // 검색된 게시글이 없으면 빈 리스트 반환
	    }
	    return boards;
	}


    // 검색된 게시글 수
    public int getBoardsCountBySearch(String keyword, String searchType) {
        return boardRepository.countBySearch(keyword, searchType);
    }


	// 특정 유저 게시글 조회
	public List<Board> getBoardsByUser(UserId userId) {
		return boardRepository.findByUser(userId);
	}
}