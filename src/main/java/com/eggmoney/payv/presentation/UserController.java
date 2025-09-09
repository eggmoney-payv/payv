package com.eggmoney.payv.presentation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eggmoney.payv.application.service.UserAppService;
import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.shared.error.DomainException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 인증 관련 컨트롤러
 * - 회원가입, 로그인, 로그아웃 기능 제공
 * 
 * @author 정의탁, 강기범
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserAppService userAppService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인 페이지 표시
     */
    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           @RequestParam(value = "expired", required = false) String expired,
                           Model model) {
        
        if (error != null) {
            model.addAttribute("errorMessage", "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        
        if (logout != null) {
            model.addAttribute("logoutMessage", "성공적으로 로그아웃되었습니다.");
        }
        
        if (expired != null) {
            model.addAttribute("expiredMessage", "세션이 만료되었습니다. 다시 로그인해주세요.");
        }
        
        return "auth/login";
    }

    /**
     * 회원가입 페이지 표시
     */
    @GetMapping("/signup")
    public String signupForm() {
        return "auth/signup";
    }

    /**
     * 회원가입 처리 (폼 파라미터를 직접 받아서 처리)
     */
    @PostMapping("/signup")
    public String signup(@RequestParam("email") String email,
                        @RequestParam(value = "name", required = false) String name,
                        @RequestParam("password") String password,
                        @RequestParam("passwordConfirm") String passwordConfirm,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        
        log.info("회원가입 시도: {}", email);
        
        try {
            // 기본 유효성 검사
            if (email == null || email.trim().isEmpty()) {
                model.addAttribute("errorMessage", "이메일을 입력해주세요.");
                return "auth/signup";
            }
            
            if (password == null || password.length() < 6) {
                model.addAttribute("errorMessage", "비밀번호는 6자 이상이어야 합니다.");
                return "auth/signup";
            }
            
            if (password.length() > 20) {
                model.addAttribute("errorMessage", "비밀번호는 20자를 초과할 수 없습니다.");
                return "auth/signup";
            }
            
            if (!password.equals(passwordConfirm)) {
                model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
                return "auth/signup";
            }
            
            if (email.length() > 50) {
                model.addAttribute("errorMessage", "이메일은 50자를 초과할 수 없습니다.");
                return "auth/signup";
            }
            
            if (name != null && name.length() > 20) {
                model.addAttribute("errorMessage", "이름은 20자를 초과할 수 없습니다.");
                return "auth/signup";
            }
            
            // 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(password);
            
            // 사용자 등록
            User newUser = userAppService.register(email.trim(), 
                                                  name != null ? name.trim() : null, 
                                                  encodedPassword);
            
            log.info("회원가입 성공: {} (ID: {})", newUser.getEmail(), newUser.getId());
            redirectAttributes.addFlashAttribute("signupSuccess", 
                                                "회원가입이 완료되었습니다. 로그인해주세요.");
            
            return "redirect:/login";
            
        } catch (DomainException e) {
            log.warn("회원가입 실패: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/signup";
            
        } catch (Exception e) {
            log.error("회원가입 중 예상치 못한 오류 발생", e);
            model.addAttribute("errorMessage", "회원가입 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            return "auth/signup";
        }
    }

    /**
     * 대시보드 페이지 (로그인 후 메인 페이지)
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            model.addAttribute("userEmail", email);
            log.info("사용자 대시보드 접근: {}", email);
        }
        return "dashboard/main";
    }

    /**
     * 로그아웃 처리
     */
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            String email = auth.getName();
            log.info("사용자 로그아웃: {}", email);
            
            // Spring Security 로그아웃 처리
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        
        return "redirect:/login?logout";
    }

    /**
     * 접근 거부 페이지
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403";
    }
}