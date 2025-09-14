package com.eggmoney.payv.presentation;

import java.time.format.DateTimeFormatter;
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
import com.eggmoney.payv.presentation.dto.PageInfo;

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
    public String list(@RequestParam(defaultValue = "1") int page, Model model) {
        int pageSize = 10; // 한 페이지에 보여줄 개수
        int blockSize = 5;	// 한 블럭에 들어갈 페이지 수
        
        int totalCount = boardAppService.getBoardCount(); // 전체 게시글 수
        int totalPage = (int) Math.ceil((double) totalCount / pageSize);
        
        // 현재 페이지에 보여줄 시작 offset
        int offset = (page - 1) * pageSize;

        List<Board> boardList = boardAppService.getBoardsByPage(offset, pageSize);
//        List<Board> boardList = boardAppService.getAllBoards();
        
        int currentBlock = (int) Math.ceil((double) page / blockSize);
        int startPage = (currentBlock - 1) * blockSize + 1;
        int endPage = Math.min(startPage + blockSize - 1, totalPage);

        PageInfo pageInfo = new PageInfo(
                page, totalPage, startPage, endPage,
                startPage > 1, endPage < totalPage
            );
        
        model.addAttribute("boardList", boardList);
        model.addAttribute("currentPage", "boards"); // 현재 페이지 정보를 모델에 전달(aside에 호버된 상태 표시하기 위함)
        
        // 페이지네이션 정보 전달
        model.addAttribute("pageInfo", pageInfo);
        
        return "board/list"; // WEB-INF/views/board/list.jsp
    }

    // 글쓰기 폼 화면
    @GetMapping("/new")
    public String createForm(Model model) {
    	model.addAttribute("currentPage", "boards"); // 현재 페이지 정보를 모델에 전달(aside에 호버된 상태 표시하기 위함)
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

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        model.addAttribute("boardCreatedAtText",
            board.getCreatedAt() != null ? board.getCreatedAt().format(fmt) : "");
        
        model.addAttribute("currentPage", "boards"); // 현재 페이지 정보를 모델에 전달(aside에 호버된 상태 표시하기 위함)
        
        model.addAttribute("board", board);
        model.addAttribute("comments", comments);
        model.addAttribute("likeCount", likeCount);

//        //   model.addAttribute("authorName", "운영자"); // 실제로는 userRepository로 조회

        return "board/detail";
    }
    
    @GetMapping("/search")
    public String search(@RequestParam String keyword, @RequestParam String searchType, 
                         @RequestParam(defaultValue = "1") int page, Model model) {
    	
    	// 검색어가 없을 때는 기본 값 설정
        if (keyword == null) {
            keyword = "";  // 기본 검색어를 빈 문자열로 설정
        }
        if (searchType == null) {
            searchType = "title";  // 기본 검색타입을 "title"로 설정
        }
        
        int pageSize = 10; // 한 페이지에 보여줄 개수
        int blockSize = 5;	// 한 블럭에 들어갈 페이지 수
        
        int totalCount = boardAppService.getBoardsCountBySearch(keyword, searchType);
        int totalPage = (int) Math.ceil((double) totalCount / pageSize);
        
        int offset = (page - 1) * pageSize;
        
        List<Board> boardList = boardAppService.getBoardsBySearch(keyword, searchType, offset, pageSize);

        int currentBlock = (int) Math.ceil((double) page / blockSize);
        int startPage = (currentBlock - 1) * blockSize + 1;
        int endPage = Math.min(startPage + blockSize - 1, totalPage);

        PageInfo pageInfo = new PageInfo(
                page, totalPage, startPage, endPage,
                startPage > 1, endPage < totalPage
            );
        
        model.addAttribute("boardList", boardList);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("currentPage", "boards"); // 현재 페이지 정보를 모델에 전달(aside에 호버된 상태 표시하기 위함)
        model.addAttribute("searchKeyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("totalCount", totalCount);  // 검색된 게시글 수 추가
        model.addAttribute("noResults", boardList.isEmpty()); // 결과가 없을 때 noResults = true

     // 페이지네이션 정보 전달
        model.addAttribute("pageInfo", pageInfo);
        
        return "board/list";
        

    }
    
    
}
