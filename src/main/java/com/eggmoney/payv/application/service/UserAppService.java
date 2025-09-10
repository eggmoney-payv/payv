package com.eggmoney.payv.application.service;

import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.repository.UserRepository;
import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.domain.shared.error.DomainException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 관련 애플리케이션 서비스 회원가입, 로그인, 개인정보 관리 등의 기능을 제공
 * 
 * @author 정의탁, 강기범
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserAppService { // @Transactional 제거

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	// 이메일 정규식 패턴
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	private static final int MIN_PASSWORD_LENGTH = 8;
	private static final int MAX_NAME_LENGTH = 50;

	@Transactional // 메서드별로 트랜잭션 적용
	public User register(String email, String name, String rawPassword) {
		log.info("회원가입 시도: email={}, name={}", email, name);

		validateEmailFormat(email);
		validatePasswordStrength(rawPassword);
		validateName(name);

		if (userRepository.existsByEmail(email)) {
			log.warn("이메일 중복: {}", email);
			throw new DomainException("이미 사용중인 이메일입니다: " + email);
		}

		String encodedPassword = passwordEncoder.encode(rawPassword);
		User user = User.create(email, name, encodedPassword);
		userRepository.save(user);

		log.info("회원가입 완료: userId={}, email={}", user.getId(), user.getEmail());
		return user;
	}

	@Transactional(readOnly = true)
	public User authenticate(String email, String rawPassword) {
		log.info("로그인 시도: email={}", email);

		if (email == null || email.trim().isEmpty()) {
			throw new IllegalArgumentException("이메일은 필수입니다.");
		}
		if (rawPassword == null || rawPassword.trim().isEmpty()) {
			throw new IllegalArgumentException("비밀번호는 필수입니다.");
		}

		User user = userRepository.findByEmail(email).orElseThrow(() -> {
			log.warn("존재하지 않는 사용자: {}", email);
			return new DomainException("이메일 또는 비밀번호가 올바르지 않습니다.");
		});

		if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
			log.warn("비밀번호 불일치: email={}", email);
			throw new DomainException("이메일 또는 비밀번호가 올바르지 않습니다.");
		}

		log.info("로그인 성공: userId={}, email={}", user.getId(), email);
		return user;
	}

	@Transactional
	public User changeEmail(UserId userId, String newEmail) {
		log.info("이메일 변경 시도: userId={}, newEmail={}", userId, newEmail);

		validateEmailFormat(newEmail);
		User user = findByIdOrThrow(userId);

		if (user.getEmail().equals(newEmail)) {
			log.info("동일한 이메일로 변경 요청, 변경하지 않음: userId={}", userId);
			return user;
		}

		if (userRepository.existsByEmailAndIdNot(newEmail, userId)) {
			log.warn("이메일 중복: {}", newEmail);
			throw new DomainException("이미 사용중인 이메일입니다: " + newEmail);
		}

		user.changeEmail(newEmail);
		userRepository.save(user);

		log.info("이메일 변경 완료: userId={}, newEmail={}", userId, newEmail);
		return user;
	}

	@Transactional
	public User changePassword(UserId userId, String currentPassword, String newPassword) {
		log.info("비밀번호 변경 시도: userId={}", userId);

		if (currentPassword == null || currentPassword.trim().isEmpty()) {
			throw new IllegalArgumentException("현재 비밀번호는 필수입니다.");
		}
		validatePasswordStrength(newPassword);

		User user = findByIdOrThrow(userId);

		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			log.warn("현재 비밀번호 불일치: userId={}", userId);
			throw new DomainException("현재 비밀번호가 일치하지 않습니다.");
		}

		if (passwordEncoder.matches(newPassword, user.getPassword())) {
			log.warn("새 비밀번호가 기존과 동일: userId={}", userId);
			throw new DomainException("새 비밀번호는 기존 비밀번호와 달라야 합니다.");
		}

		String encodedNewPassword = passwordEncoder.encode(newPassword);
		user.changePassword(encodedNewPassword);
		userRepository.save(user);

		log.info("비밀번호 변경 완료: userId={}", userId);
		return user;
	}

	@Transactional
	public User changeName(UserId userId, String newName) {
		log.info("이름 변경 시도: userId={}, newName={}", userId, newName);

		validateName(newName);
		User user = findByIdOrThrow(userId);

		if (user.getName().equals(newName)) {
			log.info("동일한 이름으로 변경 요청, 변경하지 않음: userId={}", userId);
			return user;
		}

		user.changeName(newName);
		userRepository.save(user);

		log.info("이름 변경 완료: userId={}, newName={}", userId, newName);
		return user;
	}

	@Transactional(readOnly = true)
	public Optional<User> findById(UserId userId) {
		return userRepository.findById(userId);
	}

	@Transactional(readOnly = true)
	public User findByIdOrThrow(UserId userId) {
		return userRepository.findById(userId).orElseThrow(() -> new DomainException("사용자를 찾을 수 없습니다: " + userId));
	}

	@Transactional(readOnly = true)
	public Optional<User> findByEmail(String email) {
		if (email == null || email.trim().isEmpty()) {
			return Optional.empty();
		}
		return userRepository.findByEmail(email);
	}

	@Transactional(readOnly = true)
	public boolean existsByEmail(String email) {
		if (email == null || email.trim().isEmpty()) {
			return false;
		}
		return userRepository.existsByEmail(email);
	}

	@Transactional(readOnly = true)
	public boolean isActiveUser(UserId userId) {
		return userRepository.findById(userId).isPresent();
	}

	@Transactional
	public void withdrawUser(UserId userId, String password) {
		log.info("회원 탈퇴 시도: userId={}", userId);

		if (password == null || password.trim().isEmpty()) {
			throw new IllegalArgumentException("비밀번호는 필수입니다.");
		}

		User user = findByIdOrThrow(userId);

		if (!passwordEncoder.matches(password, user.getPassword())) {
			log.warn("회원 탈퇴 비밀번호 불일치: userId={}", userId);
			throw new DomainException("비밀번호가 일치하지 않습니다.");
		}

		userRepository.deleteById(userId);
		log.info("회원 탈퇴 완료: userId={}", userId);
	}

	// 검증 메서드들은 그대로 유지
	private void validateEmailFormat(String email) {
		if (email == null || email.trim().isEmpty()) {
			throw new IllegalArgumentException("이메일은 필수입니다.");
		}

		String trimmedEmail = email.trim();
		if (trimmedEmail.length() > 100) {
			throw new IllegalArgumentException("이메일은 100자를 초과할 수 없습니다.");
		}

		if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
			throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다: " + email);
		}
	}

	private void validatePasswordStrength(String password) {
		if (password == null) {
			throw new IllegalArgumentException("비밀번호는 필수입니다.");
		}

		if (password.length() < MIN_PASSWORD_LENGTH) {
			throw new IllegalArgumentException(String.format("비밀번호는 최소 %d자 이상이어야 합니다.", MIN_PASSWORD_LENGTH));
		}

		if (password.length() > 100) {
			throw new IllegalArgumentException("비밀번호는 100자를 초과할 수 없습니다.");
		}

		if (password.trim().isEmpty()) {
			throw new IllegalArgumentException("비밀번호는 공백만으로 구성될 수 없습니다.");
		}
	}

	private void validateName(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("이름은 필수입니다.");
		}

		String trimmedName = name.trim();
		if (trimmedName.length() > MAX_NAME_LENGTH) {
			throw new IllegalArgumentException(String.format("이름은 %d자를 초과할 수 없습니다.", MAX_NAME_LENGTH));
		}
	}
}