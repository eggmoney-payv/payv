package com.eggmoney.payv.presentation;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

/**
 * 홈페이지 관련 컨트롤러 루트 경로 및 메인 페이지 처리
 * 
 * @author 정의탁, 강기범
 */
@Controller
@Slf4j
@Log4j2
public class HomeController {

	/**
	 * 루트 경로 처리 로그인된 사용자는 대시보드로, 비로그인 사용자는 로그인 페이지로 리다이렉트
	 */
	
	
	@GetMapping("/")
	public String home(Principal principal) {
		log.info("홈페이지 요청: principal={}", principal != null ? principal.getName() : "비로그인");

		if (principal != null) {
			// 로그인된 사용자는 대시보드로
			return "redirect:/dashboard";
		} else {
			// 비로그인 사용자는 로그인 페이지로
			return "redirect:/login";
		}
	}

	/**
	 * 홈 페이지 (선택적)
	 */
	@GetMapping("/home")
	public String homePage(Principal principal) {
		log.info("홈페이지 직접 요청: principal={}", principal != null ? principal.getName() : "비로그인");

		if (principal != null) {
			return "redirect:/dashboard";
		} else {
			return "redirect:/login";
		}
	}
}