package com.eggmoney.payv.presentation;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eggmoney.payv.application.service.UserAppService;
import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.domain.shared.error.DomainException;
import com.eggmoney.payv.presentation.form.ChangePasswordForm;
import com.eggmoney.payv.presentation.form.SignupForm;
import com.eggmoney.payv.presentation.form.UserProfileForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 인증 및 프로필 관리 컨트롤러 - 회원가입, 로그인, 로그아웃, 마이페이지, 프로필 수정, 비밀번호 변경
 * 
 * @author 정의탁, 강기범
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class UserController {

	private final UserAppService userAppService;

	// ========== 기존 인증 관련 메서드들 ==========

	/**
	 * 로그인 페이지 표시
	 */
	@GetMapping("/login")
	public String loginForm(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout,
			@RequestParam(value = "expired", required = false) String expired,
			@RequestParam(value = "signup", required = false) String signup, Model model) {

		if (error != null) {
			model.addAttribute("errorMessage", "이메일 또는 비밀번호가 올바르지 않습니다.");
		}

		if (logout != null) {
			model.addAttribute("logoutMessage", "성공적으로 로그아웃되었습니다.");
		}

		if (expired != null) {
			model.addAttribute("expiredMessage", "세션이 만료되었습니다. 다시 로그인해주세요.");
		}

		if (signup != null) {
			model.addAttribute("signupMessage", "회원가입이 완료되었습니다. 로그인해주세요.");
		}

		return "auth/login";
	}

	/**
	 * 회원가입 페이지 표시
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

		log.info("회원가입 시도: {}", signupForm.getEmail());

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

			log.info("회원가입 성공: {}", signupForm.getEmail());
			redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
			return "redirect:/login?signup=success";

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
	 * 로그아웃 처리
	 */
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/login?logout";
	}

	// ========== 추가된 마이페이지 관련 메서드들 ==========

	/**
	 * 마이페이지 표시
	 */
	@GetMapping("/user/profile")
	public String profile(Principal principal, Model model) {
		log.info("마이페이지 요청: userId={}", principal.getName());

		UserId userId = UserId.of(principal.getName());
		User user = userAppService.findByIdOrThrow(userId);

		// 프로필 폼 초기화
		UserProfileForm profileForm = new UserProfileForm();
		profileForm.setEmail(user.getEmail());
		profileForm.setName(user.getName());

		model.addAttribute("user", user);
		model.addAttribute("profileForm", profileForm);
		model.addAttribute("passwordForm", new ChangePasswordForm());
		model.addAttribute("pageTitle", "마이페이지");
		model.addAttribute("contentPage", "/WEB-INF/views/user/profile.jsp");

		return "common/layout";
	}

	/**
	 * 개인정보 수정 처리
	 */
	@PostMapping("/user/profile")
	public String updateProfile(@Valid @ModelAttribute UserProfileForm profileForm, BindingResult profileBindingResult,
			Principal principal, Model model, RedirectAttributes redirectAttributes) {

		log.info("개인정보 수정 요청: userId={}", principal.getName());

		UserId userId = UserId.of(principal.getName());
		User user = userAppService.findByIdOrThrow(userId);

		// 유효성 검사 실패
		if (profileBindingResult.hasErrors()) {
			log.warn("개인정보 수정 유효성 검사 실패: {}", profileBindingResult.getAllErrors());

			model.addAttribute("user", user);
			model.addAttribute("passwordForm", new ChangePasswordForm());
			model.addAttribute("pageTitle", "마이페이지");
			model.addAttribute("contentPage", "/WEB-INF/views/user/profile.jsp");
			return "common/layout";
		}

		try {
			// 이름 변경 (기존과 다른 경우에만)
			if (!user.getName().equals(profileForm.getName())) {
				userAppService.changeName(userId, profileForm.getName());
			}

			// 이메일 변경 (기존과 다른 경우에만)
			if (!user.getEmail().equals(profileForm.getEmail())) {
				userAppService.changeEmail(userId, profileForm.getEmail());
			}

			log.info("개인정보 수정 성공: userId={}", userId);
			redirectAttributes.addFlashAttribute("message", "개인정보가 성공적으로 수정되었습니다.");
			return "redirect:/user/profile";

		} catch (DomainException e) {
			log.warn("개인정보 수정 실패: {}", e.getMessage());

			if (e.getMessage().contains("이메일")) {
				profileBindingResult.rejectValue("email", "email.duplicate", e.getMessage());
			} else {
				profileBindingResult.reject("profile.update.failed", e.getMessage());
			}

			// 에러 발생 시 모델 설정하고 폼 페이지로 돌아가기
			model.addAttribute("user", user);
			model.addAttribute("passwordForm", new ChangePasswordForm());
			model.addAttribute("pageTitle", "마이페이지");
			model.addAttribute("contentPage", "/WEB-INF/views/user/profile.jsp");
			return "common/layout";
		}
	}

	/**
	 * 비밀번호 변경 처리
	 */
	@PostMapping("/user/change-password")
	public String changePassword(@Valid @ModelAttribute ChangePasswordForm passwordForm,
			BindingResult passwordBindingResult, Principal principal, Model model,
			RedirectAttributes redirectAttributes) {

		log.info("비밀번호 변경 요청: userId={}", principal.getName());

		UserId userId = UserId.of(principal.getName());
		User user = userAppService.findByIdOrThrow(userId);

		// 유효성 검사 실패
		if (passwordBindingResult.hasErrors()) {
			log.warn("비밀번호 변경 유효성 검사 실패: {}", passwordBindingResult.getAllErrors());

			// 프로필 폼 초기화
			UserProfileForm profileForm = new UserProfileForm();
			profileForm.setEmail(user.getEmail());
			profileForm.setName(user.getName());

			model.addAttribute("user", user);
			model.addAttribute("profileForm", profileForm);
			model.addAttribute("pageTitle", "마이페이지");
			model.addAttribute("contentPage", "/WEB-INF/views/user/profile.jsp");
			return "common/layout";
		}

		// 비밀번호 확인 검증
		if (!passwordForm.getNewPassword().equals(passwordForm.getConfirmPassword())) {
			passwordBindingResult.rejectValue("confirmPassword", "password.mismatch", "새 비밀번호가 일치하지 않습니다.");

			// 프로필 폼 초기화
			UserProfileForm profileForm = new UserProfileForm();
			profileForm.setEmail(user.getEmail());
			profileForm.setName(user.getName());

			model.addAttribute("user", user);
			model.addAttribute("profileForm", profileForm);
			model.addAttribute("pageTitle", "마이페이지");
			model.addAttribute("contentPage", "/WEB-INF/views/user/profile.jsp");
			return "common/layout";
		}

		try {
			userAppService.changePassword(userId, passwordForm.getCurrentPassword(), passwordForm.getNewPassword());

			log.info("비밀번호 변경 성공: userId={}", userId);
			redirectAttributes.addFlashAttribute("message", "비밀번호가 성공적으로 변경되었습니다.");
			return "redirect:/user/profile";

		} catch (DomainException e) {
			log.warn("비밀번호 변경 실패: {}", e.getMessage());

			passwordBindingResult.rejectValue("currentPassword", "password.invalid", e.getMessage());

			// 프로필 폼 초기화
			UserProfileForm profileForm = new UserProfileForm();
			profileForm.setEmail(user.getEmail());
			profileForm.setName(user.getName());

			model.addAttribute("user", user);
			model.addAttribute("profileForm", profileForm);
			model.addAttribute("pageTitle", "마이페이지");
			model.addAttribute("contentPage", "/WEB-INF/views/user/profile.jsp");
			return "common/layout";
		}
	}
}