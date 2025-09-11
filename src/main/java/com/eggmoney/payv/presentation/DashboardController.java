package com.eggmoney.payv.presentation;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eggmoney.payv.application.service.UserAppService;
import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.vo.UserId;

import lombok.RequiredArgsConstructor;

/**
 * 대시보드 컨트롤러
 * 
 * @author 강기범
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

	private final UserAppService userAppService;

	@GetMapping("/dashboard")
	public String dashboard(Principal principal, Model model) {
		UserId userId = UserId.of(principal.getName());
		User user = userAppService.findByIdOrThrow(userId);

		model.addAttribute("user", user);
		model.addAttribute("pageTitle", "대시보드");
		model.addAttribute("contentPage", "/WEB-INF/views/dashboard/main.jsp");

		return "common/layout";
	}
}