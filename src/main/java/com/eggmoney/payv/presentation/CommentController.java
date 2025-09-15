package com.eggmoney.payv.presentation;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eggmoney.payv.application.service.CommentAppService;
import com.eggmoney.payv.domain.model.vo.BoardId;
import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.domain.shared.error.DomainException;
import com.eggmoney.payv.security.CustomUser;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/boards/{boardId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentAppService commentAppService;

    @PostMapping
    public String addComment(@PathVariable String boardId,
    						 Authentication authentication,
                             @RequestParam String content) {
    	
    	CustomUser customUser = (CustomUser) authentication.getPrincipal();
    	
        // 로그인 없으면 userId 파라미터가 없으므로 임시 ID 할당
        if (customUser == null) {
            throw new DomainException("로그인이 필요한 서비스 입니다.");
        }
        commentAppService.addComment(BoardId.of(boardId), customUser.getUserId(), content);
        return "redirect:/boards/" + boardId;
    }

}