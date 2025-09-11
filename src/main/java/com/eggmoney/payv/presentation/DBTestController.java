package com.eggmoney.payv.presentation;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class DBTestController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@GetMapping("/db")
	@ResponseBody
	public String testDB() {
		try {
			long userCount = userRepository.findAll().size();
			return "DB 연결 성공! 총 사용자 수: " + userCount;
		} catch (Exception e) {
			return "DB 연결 실패: " + e.getMessage();
		}
	}

	@GetMapping("/create-test-user")
	@ResponseBody
	public String createTestUser() {
		try {
			String testEmail = "test@test.com";
			String testPassword = "test123";
			String encodedPassword = passwordEncoder.encode(testPassword);

			// 기존 사용자 삭제 (있다면)
			Optional<User> existingUser = userRepository.findByEmail(testEmail);
			if (existingUser.isPresent()) {
				userRepository.deleteById(existingUser.get().getId());
				log.info("기존 테스트 사용자 삭제됨");
			}

			// 새 사용자 생성
			User testUser = User.create(testEmail, "테스트사용자", encodedPassword);
			userRepository.save(testUser);

			return String.format(
					"테스트 사용자 생성 완료!<br>" + "이메일: %s<br>" + "비밀번호: %s<br>" + "암호화된 비밀번호: %s<br>" + "이제 로그인해보세요!",
					testEmail, testPassword, encodedPassword);

		} catch (Exception e) {
			log.error("테스트 사용자 생성 중 오류", e);
			return "오류 발생: " + e.getMessage();
		}
	}

	@GetMapping("/password-test")
	@ResponseBody
	public String testPassword(@RequestParam String email, @RequestParam String rawPassword) {
		try {
			Optional<User> userOpt = userRepository.findByEmail(email);
			if (!userOpt.isPresent()) {
				return "사용자를 찾을 수 없음: " + email;
			}

			User user = userOpt.get();
			boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());

			return String.format("이메일: %s<br>" + "입력 비밀번호: %s<br>" + "DB 비밀번호 (처음 20자): %s...<br>" + "비밀번호 일치: %s",
					email, rawPassword, user.getPassword().substring(0, Math.min(20, user.getPassword().length())),
					matches);

		} catch (Exception e) {
			log.error("비밀번호 테스트 중 오류", e);
			return "오류: " + e.getMessage();
		}
	}

	@GetMapping("/all-users")
	@ResponseBody
	public String getAllUsers() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<h3>전체 사용자 목록</h3>");

			userRepository.findAll().forEach(user -> {
				sb.append(String.format("ID: %s<br>이메일: %s<br>이름: %s<br>비밀번호: %s...<br><br>", user.getId().value(),
						user.getEmail(), user.getName(),
						user.getPassword().substring(0, Math.min(10, user.getPassword().length()))));
			});

			return sb.toString();
		} catch (Exception e) {
			return "오류: " + e.getMessage();
		}
	}
}