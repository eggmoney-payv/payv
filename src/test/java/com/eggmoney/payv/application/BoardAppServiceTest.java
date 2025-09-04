package com.eggmoney.payv.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.eggmoney.payv.application.service.BoardAppService;
import com.eggmoney.payv.domain.model.entity.Board;
import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.repository.BoardRepository;
import com.eggmoney.payv.domain.model.repository.UserRepository;
import com.eggmoney.payv.domain.model.vo.BoardId;
import com.eggmoney.payv.domain.model.vo.UserId;

/**
 * Test: BoardAppServiceTest
 * - 책임: BoardAppService 유스케이스 단위 테스트.
 * - DB 접근 없이 FakeRepository 사용.
 * 
 * 테스트 시나리오:
 *   - testCreateBoard(): 게시글 생성 성공
 *   - testUpdateBoard(): 게시글 수정 성공
 *   - testGetBoard(): 단일 게시글 조회 성공
 *   - testGetBoardsByUser(): 특정 사용자 게시글 조회
 *   - testGetAllBoards(): 전체 게시글 조회
 * 
 * @author 한지원
 *
 */

class FakeBoardRepository implements BoardRepository {
    private Map<String, Board> storage = new HashMap<>();

    @Override
    public Optional<Board> findById(BoardId id) {
        return Optional.ofNullable(storage.get(id.value()));
    }

    @Override
    public void save(Board board) {
        storage.put(board.getId().value(), board);
    }

    @Override
    public List<Board> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Board> findByUser(UserId userId) {
        List<Board> result = new ArrayList<>();
        for (Board b : storage.values()) {
            if (b.getUserId().equals(userId)) {
                result.add(b);
            }
        }
        return result;
    }
}

class FakeUserRepository implements UserRepository {
    private Map<String, User> storage = new HashMap<>();

    public FakeUserRepository() {
        User user = User.create("test@example.com", "테스트유저");
        storage.put(user.getId().value(), user);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return Optional.ofNullable(storage.get(id.value()));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return storage.values().stream()
                .filter(u -> email.equals(u.getEmail()))
                .findFirst();
    }

    @Override
    public void save(User user) {
        storage.put(user.getId().value(), user);
    }
}


public class BoardAppServiceTest {

    private BoardAppService service;
    private User testUser;

    // 각 테스트 메서드(@Test)가 실행되기 전에 항상 먼저 실행되는 메서드
    // 테스트에 필요한 환경을 설정 (객체 생성, 초기화 등)
    @Before
    public void setUp() {
        BoardRepository boardRepo = new FakeBoardRepository();
        UserRepository userRepo = new FakeUserRepository();
        service = new BoardAppService(userRepo, boardRepo);
        testUser = User.create("test@example.com", "테스트유저");
        userRepo.save(testUser);
    }

    @Test
    public void testCreateBoard() {
        Board board = service.createBoard(testUser, "제목", "내용");

        assertNotNull(board);
        assertEquals("제목", board.getTitle());
    }

    @Test
    public void testUpdateBoard() {
        Board board = service.createBoard(testUser, "제목", "내용");

        Board updated = service.updateBoard(board.getId(), "수정제목", "수정내용", testUser.getId());

        assertEquals("수정제목", updated.getTitle());
        assertEquals("수정내용", updated.getContent());
    }

    @Test
    public void testGetBoard() {
        Board board = service.createBoard(testUser, "제목", "내용");

        Board found = service.getBoard(board.getId());

        assertEquals(board.getId(), found.getId());
    }

    @Test
    public void testGetBoardsByUser() {
        service.createBoard(testUser, "제목1", "내용1");
        service.createBoard(testUser, "제목2", "내용2");

        assertEquals(2, service.getBoardsByUser(testUser.getId()).size());
    }

    @Test
    public void testGetAllBoards() {
        service.createBoard(testUser, "제목1", "내용1");
        service.createBoard(testUser, "제목2", "내용2");

        assertEquals(2, service.getAllBoards().size());
    }
}
