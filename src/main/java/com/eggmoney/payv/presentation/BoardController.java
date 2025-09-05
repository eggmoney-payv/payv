package com.eggmoney.payv.presentation;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eggmoney.payv.application.service.BoardAppService;
import com.eggmoney.payv.application.service.CommentAppService;
import com.eggmoney.payv.application.service.ReactionAppService;
import com.eggmoney.payv.domain.model.entity.Board;
import com.eggmoney.payv.domain.model.entity.Comment;
import com.eggmoney.payv.domain.model.vo.BoardId;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardAppService boardAppService;
    private final CommentAppService commentAppService;
    private final ReactionAppService reactionAppService;

    // 게시글 목록 화면
    @GetMapping
    public String list(Model model) {
        List<Board> boardList = boardAppService.getAllBoards();
        model.addAttribute("boardList", boardList);
        model.addAttribute("currentPage", "boards"); // 현재 페이지 정보를 모델에 전달(aside에 호버된 상태 표시하기 위함)
        return "board/list"; // WEB-INF/views/board/list.jsp
    }

    // 글쓰기 폼 화면
    @GetMapping("/new")
    public String createForm() {
        return "board/create"; // WEB-INF/views/board/create.jsp
    }

    // 게시글 작성 처리
    @PostMapping
    public String create(@RequestParam String userId,
                         @RequestParam String title,
                         @RequestParam String content) {
        boardAppService.createBoardByUserId(userId, title, content);
        return "redirect:/boards"; // 작성 후 목록 리다이렉트
    }

    // 게시글 상세 조회
    @GetMapping("/{boardId}")
    public String detail(@PathVariable String boardId, Model model) {
        Board board = boardAppService.getBoard(BoardId.of(boardId));
        List<Comment> comments = commentAppService.getComments(BoardId.of(boardId));
        long likeCount = reactionAppService.getLikeCount(BoardId.of(boardId));

        model.addAttribute("board", board);
        model.addAttribute("comments", comments);
        model.addAttribute("likeCount", likeCount);

//        //   model.addAttribute("authorName", "운영자"); // 실제로는 userRepository로 조회

        return "board/detail";
    }
    
    // 수정폼, 수정처리도 추가 예정
}
