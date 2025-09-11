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
 * 사용자 인증 및 프로필 관리 컨트롤러 회원가입, 로그인, 로그아웃, 마이페이지, 프로필 수정, 비밀번호 변경
 * 
 * @author 정의탁, 강기범
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class UserController {

	private final UserAppService userAppService;

	// ========== 인증 관련 메서드들 ==========

	/**
	 * 로그인 페이지 표시
	 */
	@GetMapping("/login")
	public String loginForm(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout,
			@RequestParam(value = "expired", required = false) String expired,
			@RequestParam(value = "signup", required = false) String signup, Model model) {

		log.info("로그인 페이지 요청");

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

		return "user/login";
	}

	/**
	 * 회원가입 페이지 표시
	 */
	@GetMapping("/signup")
	public String signupForm(Model model) {
		log.info("회원가입 페이지 요청");
		model.addAttribute("signupForm", new SignupForm());
		return "user/signup";
	}

	/**
	 * 회원가입 처리
	 */
	@PostMapping("/signup")
	public String signup(@Valid @ModelAttribute SignupForm signupForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model) {

		log.info("회원가입 처리 요청: email={}, name={}", signupForm.getEmail(), signupForm.getName());

		// 기본 유효성 검사 오류가 있으면 회원가입 페이지로 돌아감
		if (bindingResult.hasErrors()) {
			log.warn("회원가입 유효성 검사 실패: {}", bindingResult.getAllErrors());
			return "user/signup";
		}

		// 비밀번호 확인 검사
		if (!signupForm.getPassword().equals(signupForm.getConfirmPassword())) {
			bindingResult.rejectValue("confirmPassword", "signup.password.mismatch", "비밀번호가 일치하지 않습니다.");
			return "user/signup";
		}

		try {
			userAppService.register(signupForm.getEmail(), signupForm.getName(), signupForm.getPassword());
			log.info("회원가입 성공: email={}", signupForm.getEmail());
			redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
			return "redirect:/login?signup=success";

		} catch (DomainException e) {
			log.warn("회원가입 실패: {}", e.getMessage());

			// 이메일 중복 등의 도메인 오류는 email 필드 오류로 처리
			if (e.getMessage().contains("이메일")) {
				bindingResult.rejectValue("email", "signup.email.duplicate", e.getMessage());
			} else {
				model.addAttribute("errorMessage", e.getMessage());
			}
			return "user/signup";

		} catch (IllegalArgumentException e) {
			log.warn("회원가입 입력값 오류: {}", e.getMessage());
			model.addAttribute("errorMessage", e.getMessage());
			return "user/signup";

		} catch (Exception e) {
			log.error("회원가입 중 예상치 못한 오류 발생", e);
			model.addAttribute("errorMessage", "회원가입 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
			return "user/signup";
		}
	}

	/**
	 * 로그아웃 처리 (GET 방식)
	 */
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
			log.info("사용자 로그아웃: {}", auth.getName());
		}
		return "redirect:/login?logout";
	}

	// ========== 마이페이지 관련 메서드들 ==========

	/**
	 * 마이페이지 표시
	 */
	@GetMapping("/user/profile")
	public String profile(Principal principal, Model model) {
		log.info("마이페이지 요청: principal={}", principal.getName());

		try {
			User user = getCurrentUser(principal);

			UserProfileForm profileForm = new UserProfileForm();
			profileForm.setEmail(user.getEmail());
			profileForm.setName(user.getName());

			model.addAttribute("user", user);
			model.addAttribute("profileForm", profileForm);

			return "user/profile";

		} catch (Exception e) {
			log.error("마이페이지 조회 중 오류 발생", e);
			model.addAttribute("errorMessage", "사용자 정보를 불러오는 중 오류가 발생했습니다.");
			return "user/profile";
		}
	}

	/**
	 * 프로필 수정 처리
	 */
	@PostMapping("/user/profile")
	public String updateProfile(@Valid @ModelAttribute UserProfileForm profileForm, BindingResult bindingResult,
			Principal principal, RedirectAttributes redirectAttributes, Model model) {

		log.info("프로필 수정 요청: principal={}", principal.getName());

		try {
			User user = getCurrentUser(principal);

			if (bindingResult.hasErrors()) {
				log.warn("프로필 수정 유효성 검사 실패: {}", bindingResult.getAllErrors());
				model.addAttribute("user", user);
				return "user/profile";
			}

			// 이메일 변경
			if (!user.getEmail().equals(profileForm.getEmail())) {
				userAppService.changeEmail(user.getId(), profileForm.getEmail());
			}

			// 이름 변경
			if (!user.getName().equals(profileForm.getName())) {
				userAppService.changeName(user.getId(), profileForm.getName());
			}

			log.info("프로필 수정 성공: userId={}", user.getId());
			redirectAttributes.addFlashAttribute("successMessage", "프로필이 성공적으로 수정되었습니다.");
			return "redirect:/user/profile";

		} catch (DomainException e) {
			log.warn("프로필 수정 실패: {}", e.getMessage());

			if (e.getMessage().contains("이메일")) {
				bindingResult.rejectValue("email", "profile.email.error", e.getMessage());
			} else {
				model.addAttribute("errorMessage", e.getMessage());
			}

			try {
				User user = getCurrentUser(principal);
				model.addAttribute("user", user);
			} catch (Exception ex) {
				log.error("사용자 조회 실패", ex);
			}
			return "user/profile";

		} catch (Exception e) {
			log.error("프로필 수정 중 예상치 못한 오류 발생", e);
			model.addAttribute("errorMessage", "프로필 수정 중 오류가 발생했습니다.");

			try {
				User user = getCurrentUser(principal);
				model.addAttribute("user", user);
			} catch (Exception ex) {
				log.error("사용자 조회 실패", ex);
			}
			return "user/profile";
		}
	}

	/**
	 * 비밀번호 변경 페이지 표시
	 */
	@GetMapping("/user/change-password")
	public String changePasswordForm(Model model) {
		log.info("비밀번호 변경 페이지 요청");
		model.addAttribute("changePasswordForm", new ChangePasswordForm());
		return "user/change-password";
	}

	/**
	 * 비밀번호 변경 처리
	 */
	@PostMapping("/user/change-password")
	public String changePassword(@Valid @ModelAttribute ChangePasswordForm changePasswordForm,
			BindingResult bindingResult, Principal principal, RedirectAttributes redirectAttributes, Model model) {

		log.info("비밀번호 변경 요청: principal={}", principal.getName());

		if (bindingResult.hasErrors()) {
			log.warn("비밀번호 변경 유효성 검사 실패: {}", bindingResult.getAllErrors());
			return "user/change-password";
		}

		// 새 비밀번호 확인
		if (!changePasswordForm.getNewPassword().equals(changePasswordForm.getConfirmPassword())) {
			bindingResult.rejectValue("confirmPassword", "password.mismatch", "새 비밀번호가 일치하지 않습니다.");
			return "user/change-password";
		}

		try {
			User user = getCurrentUser(principal);

			userAppService.changePassword(user.getId(), changePasswordForm.getCurrentPassword(),
					changePasswordForm.getNewPassword());

			log.info("비밀번호 변경 성공: userId={}", user.getId());
			redirectAttributes.addFlashAttribute("successMessage", "비밀번호가 성공적으로 변경되었습니다.");
			return "redirect:/user/profile";

		} catch (DomainException e) {
			log.warn("비밀번호 변경 실패: {}", e.getMessage());

			if (e.getMessage().contains("현재 비밀번호")) {
				bindingResult.rejectValue("currentPassword", "password.current.invalid", e.getMessage());
			} else {
				model.addAttribute("errorMessage", e.getMessage());
			}
			return "user/change-password";

		} catch (Exception e) {
			log.error("비밀번호 변경 중 예상치 못한 오류 발생", e);
			model.addAttribute("errorMessage", "비밀번호 변경 중 오류가 발생했습니다.");
			return "user/change-password";
		}
	}

	/**
	 * Principal에서 현재 사용자 조회 Spring Security 설정에 따라 email 또는 userId를 사용
	 */
	private User getCurrentUser(Principal principal) {
		String principalName = principal.getName();

		// email 형식인지 확인
		if (principalName.contains("@")) {
			return userAppService.findByEmail(principalName).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
		} else {
			// UserId 형식으로 간주
			return userAppService.findByIdOrThrow(UserId.of(principalName));
		}
	}
}