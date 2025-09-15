package com.eggmoney.payv.presentation;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eggmoney.payv.application.service.BoardAppService;
import com.eggmoney.payv.application.service.CommentAppService;
import com.eggmoney.payv.domain.model.entity.Board;
import com.eggmoney.payv.domain.model.entity.Comment;
import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.repository.UserRepository;
import com.eggmoney.payv.domain.model.vo.BoardId;
import com.eggmoney.payv.domain.shared.error.DomainException;
import com.eggmoney.payv.presentation.dto.BoardItemDto;
import com.eggmoney.payv.presentation.dto.CommentItemDto;
import com.eggmoney.payv.presentation.dto.PageInfo;
import com.eggmoney.payv.security.CustomUser;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardAppService boardAppService;
    private final CommentAppService commentAppService;
    private final UserRepository userRepository;

    // 게시글 목록 화면
    @GetMapping
    public String list(@RequestParam(defaultValue = "1") int page, Model model) {
        int pageSize = 10; // 한 페이지에 보여줄 개수
        int blockSize = 5;	// 한 블럭에 들어갈 페이지 수
        
        int totalCount = boardAppService.getBoardCount(); // 전체 게시글 수
        int totalPage = (int) Math.ceil((double) totalCount / pageSize);
        
        // 현재 페이지에 보여줄 시작 offset
        int offset = (page - 1) * pageSize;

        List<BoardItemDto> boardDtoList = boardAppService.getBoardsByPage(offset, pageSize)
        		.stream().map(b -> {
        			BoardItemDto dto = new BoardItemDto();
        			User user = userRepository.findById(b.getUserId())
        					.orElseThrow(() -> new DomainException("작성자를 찾을 수 없습니다."));
        			dto.setId(b.getId().toString());
        			dto.setUserId(b.getUserId().toString());
        			dto.setTitle(b.getTitle());
        			dto.setContent(b.getContent());
        			dto.setOwner(user.getEmail());
        			dto.setViewCount(b.getViewCount());
        			dto.setCreatedAt(b.getCreatedAt());
        			dto.setUpdatedAt(b.getUpdatedAt());        			
        			return dto;
        		})
        		.collect(Collectors.toList());
        
        int currentBlock = (int) Math.ceil((double) page / blockSize);
        int startPage = (currentBlock - 1) * blockSize + 1;
        int endPage = Math.min(startPage + blockSize - 1, totalPage);

        PageInfo pageInfo = new PageInfo(
                page, totalPage, startPage, endPage,
                startPage > 1, endPage < totalPage
            );
        
        model.addAttribute("boardList", boardDtoList);
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
    public String create(Authentication authentication,
                         @RequestParam String title,
                         @RequestParam String content) {
    	CustomUser customUser = (CustomUser) authentication.getPrincipal();  
        boardAppService.createBoardByUserId(customUser.getUserId().toString(), title, content);
        return "redirect:/boards"; // 작성 후 목록 리다이렉트
    }

    // 게시글 상세 조회
    @GetMapping("/{boardId}")
    public String detail(Authentication authentication, @PathVariable String boardId, Model model) {
    	CustomUser customUser = (CustomUser) authentication.getPrincipal();
    	DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    	
        Board board = boardAppService.getBoard(BoardId.of(boardId));
        User owner = userRepository.findById(board.getUserId())
        		.orElseThrow(() -> new DomainException("작성자를 찾을 수 없습니다."));

        BoardItemDto boardDto = new BoardItemDto(board.getId().toString(), board.getUserId().toString(), board.getTitle(), board.getContent(), 
        		owner.getEmail(), board.getViewCount(), board.getCreatedAt(), board.getUpdatedAt());
        
        List<CommentItemDto> comments = commentAppService.getComments(BoardId.of(boardId))
        		.stream().map(c -> {
        			CommentItemDto dto = new CommentItemDto();
        			User user = userRepository.findById(c.getUserId())
        					.orElseThrow(() -> new DomainException("작성자를 찾을 수 없습니다."));
        			
        			dto.setId(c.getId().toString());
        			dto.setBoardId(c.getBoardId().toString());
        			dto.setUserId(c.getUserId().toString());
        			dto.setWriter(user.getEmail());
        			dto.setContent(c.getContent());
        			dto.setCreatedAt(c.getCreatedAt() != null ? c.getCreatedAt().format(fmt) : "");
        			dto.setUpdatedAt(c.getUpdatedAt());
        			return dto;
        		})
        		.collect(Collectors.toList());        

        
        model.addAttribute("boardCreatedAtText",
            board.getCreatedAt() != null ? board.getCreatedAt().format(fmt) : "");
        
        model.addAttribute("currentPage", "boards"); // 현재 페이지 정보를 모델에 전달(aside에 호버된 상태 표시하기 위함)
        model.addAttribute("loginUserId", customUser.getUserId().toString());
        model.addAttribute("board", boardDto);
        model.addAttribute("comments", comments);

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
        
        // List<Board> boardList = boardAppService.getBoardsBySearch(keyword, searchType, offset, pageSize);
        List<BoardItemDto> boardDtoList = boardAppService.getBoardsBySearch(keyword, searchType, offset, pageSize)
        		.stream().map(b -> {
        			BoardItemDto dto = new BoardItemDto();
        			User user = userRepository.findById(b.getUserId())
        					.orElseThrow(() -> new DomainException("작성자를 찾을 수 없습니다."));
        			dto.setId(b.getId().toString());
        			dto.setUserId(b.getUserId().toString());
        			dto.setTitle(b.getTitle());
        			dto.setContent(b.getContent());
        			dto.setOwner(user.getEmail());
        			dto.setViewCount(b.getViewCount());
        			dto.setCreatedAt(b.getCreatedAt());
        			dto.setUpdatedAt(b.getUpdatedAt());        			
        			return dto;
        		})
        		.collect(Collectors.toList());
        
        int currentBlock = (int) Math.ceil((double) page / blockSize);
        int startPage = (currentBlock - 1) * blockSize + 1;
        int endPage = Math.min(startPage + blockSize - 1, totalPage);

        PageInfo pageInfo = new PageInfo(
                page, totalPage, startPage, endPage,
                startPage > 1, endPage < totalPage
            );
        
        model.addAttribute("boardList", boardDtoList);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("currentPage", "boards"); // 현재 페이지 정보를 모델에 전달(aside에 호버된 상태 표시하기 위함)
        model.addAttribute("searchKeyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("totalCount", totalCount);  // 검색된 게시글 수 추가
        model.addAttribute("noResults", boardDtoList.isEmpty()); // 결과가 없을 때 noResults = true

     // 페이지네이션 정보 전달
        model.addAttribute("pageInfo", pageInfo);
        
        return "board/list";
    }
    
    /** ===== 수정 폼 ===== */
    @GetMapping("/{boardId}/edit")
    public String editForm(@PathVariable String boardId, Model model,
                           RedirectAttributes ra) {
        try {
            Board board = boardAppService.getBoard(BoardId.of(boardId));
            User owner = userRepository.findById(board.getUserId())
            		.orElseThrow(() -> new DomainException("작성자를 찾을 수 없습니다."));

            BoardItemDto boardDto = new BoardItemDto(board.getId().toString(), board.getUserId().toString(), board.getTitle(), board.getContent(), 
            		owner.getEmail(), board.getViewCount(), board.getCreatedAt(), board.getUpdatedAt());
            
            model.addAttribute("board", boardDto);
            model.addAttribute("currentPage", "boards");
            return "board/edit"; // 📄 WEB-INF/views/board/edit.jsp
        } catch (DomainException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/boards";
        }
    }

    /** ===== 수정 처리 ===== */
    @PostMapping("/{boardId}")
    public String update(@PathVariable String boardId,
                         @RequestParam String title,
                         @RequestParam String content,
                         RedirectAttributes ra) {
        try {
            if (title == null || title.trim().isEmpty()) {
                ra.addFlashAttribute("error", "제목은 필수입니다.");
                return "redirect:/boards/" + boardId + "/edit";
            }
            boardAppService.updateBoard(BoardId.of(boardId), title.trim(), content.trim());
            ra.addFlashAttribute("message", "게시글을 수정했습니다.");
            return "redirect:/boards/" + boardId;
        } catch (DomainException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/boards/" + boardId + "/edit";
        }
    }

    /** ===== 삭제 ===== */
    @PostMapping("/{boardId}/delete")
    public String delete(@PathVariable String boardId,
                         RedirectAttributes ra) {
        try {
            boardAppService.deleteBoard(BoardId.of(boardId));
            ra.addFlashAttribute("message", "게시글을 삭제했습니다.");
            return "redirect:/boards";
        } catch (DomainException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/boards/" + boardId;
        }
    }	
    
}
