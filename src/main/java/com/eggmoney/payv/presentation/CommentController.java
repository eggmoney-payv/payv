package com.eggmoney.payv.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eggmoney.payv.application.service.CommentAppService;
import com.eggmoney.payv.domain.model.vo.BoardId;
import com.eggmoney.payv.domain.model.vo.UserId;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/boards/{boardId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentAppService commentAppService;

    @PostMapping
    public String addComment(@PathVariable String boardId,
                             @RequestParam String userId,
                             @RequestParam String content) {
        commentAppService.addComment(BoardId.of(boardId), UserId.of(userId), content);
        return "redirect:/boards/" + boardId;
    }
}