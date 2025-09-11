package com.eggmoney.payv.presentation;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@Log4j2
public class DashboardController {

	@GetMapping("/dashboard")
	public String dashboard(Principal principal, Model model) {
		log.info("대시보드 접근: {}", principal.getName());
		model.addAttribute("username", principal.getName());
		return "dashboard/main";
	}
}