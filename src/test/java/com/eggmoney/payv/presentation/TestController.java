package com.eggmoney.payv.presentation;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@GetMapping("/password-test")
	public String testPassword(@RequestParam String email, @RequestParam String rawPassword) {
		try {
			com.eggmoney.payv.domain.model.entity.User user = userRepository.findByEmail(email).orElse(null);
			if (user == null) {
				return "사용자를 찾을 수 없음: " + email;
			}

			boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());

			return String.format("이메일: %s%n" + "입력 비밀번호: %s%n" + "DB 비밀번호: %s%n" + "비밀번호 일치: %s", email, rawPassword,
					user.getPassword(), matches);

		} catch (Exception e) {
			log.error("테스트 중 오류", e);
			return "오류: " + e.getMessage();
		}
	}

	@GetMapping("/create-test-user")
	public String createTestUser() {
		try {
			String testEmail = "test@test.com";
			String testPassword = "test123";
			String encodedPassword = passwordEncoder.encode(testPassword);

			// 기존 사용자 삭제
			userRepository.findByEmail(testEmail).ifPresent(user -> userRepository.deleteById(user.getId()));

			// 새 사용자 생성
			User testUser = User.create(testEmail, "테스트사용자", encodedPassword);
			userRepository.save(testUser);

			return String.format("테스트 사용자 생성됨:%n" + "이메일: %s%n" + "비밀번호: %s%n" + "암호화된 비밀번호: %s", testEmail,
					testPassword, encodedPassword);

		} catch (Exception e) {
			log.error("테스트 사용자 생성 중 오류", e);
			return "오류: " + e.getMessage();
		}
	}
}