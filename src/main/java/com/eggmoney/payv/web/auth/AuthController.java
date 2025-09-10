package com.eggmoney.payv.web.auth;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eggmoney.payv.application.service.UserAppService;
import com.eggmoney.payv.domain.shared.error.DomainException;
import com.eggmoney.payv.web.auth.form.SignupForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증 관련 컨트롤러 회원가입, 로그인 페이지 제공
 * 
 * @author 팀원명
 */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final UserAppService userAppService;

	/**
	 * 회원가입 페이지
	 */
	@GetMapping("/signup")
	public String signupForm(Model model) {
		model.addAttribute("signupForm", new SignupForm());
		return "auth/signup";
	}

	/**
	 * 회원가입 처리
	 */
	@PostMapping("/signup")
	public String signup(@Valid @ModelAttribute SignupForm signupForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		log.info("회원가입 요청: email={}", signupForm.getEmail());

		// 유효성 검사 실패
		if (bindingResult.hasErrors()) {
			log.warn("회원가입 유효성 검사 실패: {}", bindingResult.getAllErrors());
			return "auth/signup";
		}

		// 비밀번호 확인
		if (!signupForm.getPassword().equals(signupForm.getConfirmPassword())) {
			bindingResult.rejectValue("confirmPassword", "password.mismatch", "비밀번호가 일치하지 않습니다.");
			return "auth/signup";
		}

		try {
			// 회원가입 처리
			userAppService.register(signupForm.getEmail(), signupForm.getName(), signupForm.getPassword());

			log.info("회원가입 성공: email={}", signupForm.getEmail());
			redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
			return "redirect:/auth/login?signup=success";

		} catch (DomainException e) {
			log.warn("회원가입 실패: {}", e.getMessage());

			if (e.getMessage().contains("이메일")) {
				bindingResult.rejectValue("email", "email.duplicate", e.getMessage());
			} else {
				bindingResult.reject("signup.failed", e.getMessage());
			}
			return "auth/signup";

		} catch (IllegalArgumentException e) {
			log.warn("회원가입 입력 오류: {}", e.getMessage());
			bindingResult.reject("validation.failed", e.getMessage());
			return "auth/signup";
		}
	}

	/**
	 * 로그인 페이지
	 */
	@GetMapping("/login")
	public String loginForm(@RequestParam(value = "signup", required = false) String signup,
			@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout, Model model) {

		if (signup != null) {
			model.addAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
		}

		if (error != null) {
			model.addAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
		}

		if (logout != null) {
			model.addAttribute("message", "로그아웃되었습니다.");
		}

		return "auth/login";
	}
}