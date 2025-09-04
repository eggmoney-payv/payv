package com.eggmoney.payv.presentation;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eggmoney.payv.application.service.BoardAppService;
import com.eggmoney.payv.domain.model.entity.Board;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardAppService boardAppService;

    // 게시글 목록 화면
    @GetMapping
    public String list(Model model) {
        List<Board> boardList = boardAppService.getAllBoards();
        model.addAttribute("boardList", boardList);
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

    // 필요시 상세조회, 수정폼, 수정처리도 추가 가능
}
