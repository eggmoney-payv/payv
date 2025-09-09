package com.eggmoney.payv.application.service;

import java.util.Optional;

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
 * @author 정의탁
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserAppService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 회원가입 - 이메일 유일성 보장
	 */
	public User register(String email, String name, String rawPassword) {
		log.info("회원가입 시도: email={}, name={}", email, name);

		// 이메일 중복 검사
		if (userRepository.existsByEmail(email)) {
			throw new DomainException("이미 사용중인 이메일입니다: " + email);
		}

		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(rawPassword);

		// 사용자 생성 및 저장
		User user = User.create(email, name, encodedPassword);
		userRepository.save(user);

		log.info("회원가입 완료: userId={}, email={}", user.getId(), user.getEmail());
		return user;
	}

	/**
	 * 이메일 변경 - 유일성 보장
	 */
	public User changeEmail(UserId userId, String newEmail) {
		log.info("이메일 변경 시도: userId={}, newEmail={}", userId, newEmail);

		// 사용자 조회
		User user = userRepository.findById(userId).orElseThrow(() -> new DomainException("사용자를 찾을 수 없습니다."));

		// 이메일 중복 검사 (자신 제외)
		if (userRepository.existsByEmailAndIdNot(newEmail, userId)) {
			throw new DomainException("이미 사용중인 이메일입니다: " + newEmail);
		}

		// 이메일 변경 및 저장
		user.changeEmail(newEmail);
		userRepository.save(user);

		log.info("이메일 변경 완료: userId={}, newEmail={}", userId, newEmail);
		return user;
	}

	/**
	 * 비밀번호 변경
	 */
	public User changePassword(UserId userId, String currentPassword, String newPassword) {
		log.info("비밀번호 변경 시도: userId={}", userId);

		// 사용자 조회
		User user = userRepository.findById(userId).orElseThrow(() -> new DomainException("사용자를 찾을 수 없습니다."));

		// 현재 비밀번호 확인
		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new DomainException("현재 비밀번호가 일치하지 않습니다.");
		}

		// 새 비밀번호 암호화 및 변경
		String encodedNewPassword = passwordEncoder.encode(newPassword);
		user.changePassword(encodedNewPassword);
		userRepository.save(user);

		log.info("비밀번호 변경 완료: userId={}", userId);
		return user;
	}

	/**
	 * 이름 변경
	 */
	public User changeName(UserId userId, String newName) {
		log.info("이름 변경 시도: userId={}, newName={}", userId, newName);

		User user = userRepository.findById(userId).orElseThrow(() -> new DomainException("사용자를 찾을 수 없습니다."));

		user.changeName(newName);
		userRepository.save(user);

		log.info("이름 변경 완료: userId={}, newName={}", userId, newName);
		return user;
	}

	/**
	 * 사용자 조회 (ID로)
	 */
	@Transactional(readOnly = true)
	public Optional<User> findById(UserId userId) {
		return userRepository.findById(userId);
	}

	/**
	 * 사용자 조회 (이메일로) - 로그인용
	 */
	@Transactional(readOnly = true)
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	/**
	 * 회원 탈퇴
	 */
	public void withdrawUser(UserId userId, String password) {
		log.info("회원 탈퇴 시도: userId={}", userId);

		User user = userRepository.findById(userId).orElseThrow(() -> new DomainException("사용자를 찾을 수 없습니다."));

		// 비밀번호 확인
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new DomainException("비밀번호가 일치하지 않습니다.");
		}

		// 회원 탈퇴 (실제로는 상태 변경이나 소프트 삭제를 고려할 수 있음)
		userRepository.deleteById(userId);

		log.info("회원 탈퇴 완료: userId={}", userId);
	}
}