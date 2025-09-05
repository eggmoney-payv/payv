//package com.eggmoney.payv.presentation;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.eggmoney.payv.application.service.CommentAppService;
//import com.eggmoney.payv.domain.model.vo.BoardId;
//import com.eggmoney.payv.domain.model.vo.UserId;
//
//import lombok.RequiredArgsConstructor;
//
//@Controller
//@RequestMapping("/boards/{boardId}/like")
//@RequiredArgsConstructor
//public class ReactionController {
//    private final ReactionAppService reactionAppService;
//
//    @PostMapping
//    public String toggleLike(@PathVariable String boardId,
//                             @RequestParam String userId) {
//        reactionAppService.toggleLike(BoardId.of(boardId), UserId.of(userId));
//        return "redirect:/boards/" + boardId;
//    }
//}