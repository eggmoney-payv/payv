package com.eggmoney.payv.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.eggmoney.payv.application.service.UserAppService;
import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.repository.UserRepository;
import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.domain.shared.error.DomainException;
import com.eggmoney.payv.domain.shared.util.EntityIdentifier;

/**
 * UserAppService 통합 테스트 - 수정된 버전 실제 UserAppService의 검증 로직에 맞춘 테스트
 * 
 * @author 정의탁, 강기범
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/root-context.xml" })
@Transactional
public class UserAppServiceTest {

	@Resource
	UserRepository userRepository;

	@Resource
	PasswordEncoder passwordEncoder;

	// 실제 UserAppService의 제약 조건에 맞춰 수정
	private static final int MAX_EMAIL_LENGTH = 50; // UserAppService.MAX_EMAIL_LENGTH
	private static final int MAX_NAME_LENGTH = 20; // UserAppService.MAX_NAME_LENGTH
	private static final int MIN_PASSWORD_LENGTH = 8; // UserAppService.MIN_PASSWORD_LENGTH
	private static final int MAX_PASSWORD_LENGTH = 100; // UserAppService.MAX_PASSWORD_LENGTH

	private String generateUniqueEmail() {
		return "test" + System.nanoTime() + "@example.com";
	}

	// Java 8 호환 문자열 반복 메서드
	private String repeatString(String str, int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append(str);
		}
		return sb.toString();
	}

	// 기존 테스트들 (그대로 유지)
	@Test
	public void 회원가입_비밀번호_암호화_테스트() {
		// given
		UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
		String email = generateUniqueEmail();
		String rawPassword = "testPassword123";
		String name = "테스트사용자";

		// when
		User user = userAppService.register(email, name, rawPassword);

		// then
		assertNotNull(user.getId());
		assertEquals(email, user.getEmail());
		assertEquals(name, user.getName());
		assertNotEquals(rawPassword, user.getPassword());
		assertTrue("암호화된 비밀번호가 원본과 매치되어야 함", passwordEncoder.matches(rawPassword, user.getPassword()));
	}

	@Test
	public void 이메일_변경_테스트() {
		// given
		UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
		String email = generateUniqueEmail();
		User user = userAppService.register(email, "사용자", "password123");

		String newEmail = "new" + generateUniqueEmail();

		// when
		User updatedUser = userAppService.changeEmail(user.getId(), newEmail);

		// then
		assertEquals(newEmail, updatedUser.getEmail());

		User reloadedUser = userRepository.findById(user.getId()).orElse(null);
		assertNotNull(reloadedUser);
		assertEquals(newEmail, reloadedUser.getEmail());
	}

	@Test
	public void 비밀번호_변경_성공_테스트() {
		// given
		UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
		String email = generateUniqueEmail();
		String currentPassword = "currentPassword123";
		String newPassword = "newPassword456";

		User user = userAppService.register(email, "사용자", currentPassword);

		// when
		User updatedUser = userAppService.changePassword(user.getId(), currentPassword, newPassword);

		// then
		assertNotEquals(currentPassword, updatedUser.getPassword());
		assertTrue("새 비밀번호가 암호화되어 저장되어야 함", passwordEncoder.matches(newPassword, updatedUser.getPassword()));
		assertFalse("기존 비밀번호는 더 이상 매치되지 않아야 함", passwordEncoder.matches(currentPassword, updatedUser.getPassword()));
	}

	@Test
	public void 비밀번호_변경_현재_비밀번호_틀림_실패_테스트() {
		// given
		UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
		String email = generateUniqueEmail();
		String currentPassword = "correctPassword123";
		String wrongPassword = "wrongPassword456";
		String newPassword = "newPassword789";

		User user = userAppService.register(email, "사용자", currentPassword);

		// when & then
		try {
			userAppService.changePassword(user.getId(), wrongPassword, newPassword);
			fail("잘못된 현재 비밀번호로 인한 예외가 발생해야 함");
		} catch (DomainException e) {
			assertTrue(e.getMessage().contains("현재 비밀번호가 일치하지 않습니다"));
		}
	}

	@Test
	public void 이름_변경_테스트() {
		// given
		UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
		String email = generateUniqueEmail();
		User user = userAppService.register(email, "원래이름", "password123");

		String newName = "새로운이름";

		// when
		User updatedUser = userAppService.changeName(user.getId(), newName);

		// then
		assertEquals(newName, updatedUser.getName());

		User reloadedUser = userRepository.findById(user.getId()).orElse(null);
		assertNotNull(reloadedUser);
		assertEquals(newName, reloadedUser.getName());
	}

	@Test
	public void 사용자_조회_이메일로_테스트() {
		// given
		UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
		String email = generateUniqueEmail();
		User user = userAppService.register(email, "사용자", "password123");

		// when
		User foundUser = userAppService.findByEmail(email).orElse(null);

		// then
		assertNotNull(foundUser);
		assertEquals(user.getId(), foundUser.getId());
		assertEquals(email, foundUser.getEmail());
	}

	@Test
	public void 사용자_조회_ID로_테스트() {
		// given
		UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
		String email = generateUniqueEmail();
		User user = userAppService.register(email, "사용자", "password123");

		// when
		User foundUser = userAppService.findById(user.getId()).orElse(null);

		// then
		assertNotNull(foundUser);
		assertEquals(user.getId(), foundUser.getId());
		assertEquals(email, foundUser.getEmail());
	}

	// 새로 추가된 테스트들
	@Test
	public void 로그인_성공_테스트() {
		// given
		UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
		String email = generateUniqueEmail();
		String password = "password123";
		User user = userAppService.register(email, "사용자", password);

		// when
		User authenticatedUser = userAppService.authenticate(email, password);

		// then
		assertNotNull(authenticatedUser);
		assertEquals(user.getId(), authenticatedUser.getId());
		assertEquals(email, authenticatedUser.getEmail());
	}

	@Test
	public void 로그인_실패_잘못된_비밀번호_테스트() {
		// given
		UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
		String email = generateUniqueEmail();
		String correctPassword = "password123";
		String wrongPassword = "wrongPassword";
		userAppService.register(email, "사용자", correctPassword);

		// when & then
		try {
			userAppService.authenticate(email, wrongPassword);
			fail("잘못된 비밀번호로 인한 예외가 발생해야 함");
		} catch (DomainException e) {
			assertTrue(e.getMessage().contains("이메일 또는 비밀번호가 올바르지 않습니다"));
		}
	}

	@Test
	public void 로그인_실패_존재하지_않는_사용자_테스트() {
		// given
		UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
		String nonExistentEmail = "nonexistent@example.com";
		String password = "password123";

		// when & then
		try {
			userAppService.authenticate(nonExistentEmail, password);
			fail("존재하지 않는 사용자로 인한 예외가 발생해야 함");
		} catch (DomainException e) {
			assertTrue(e.getMessage().contains("이메일 또는 비밀번호가 올바르지 않습니다"));
		}
	}

	@Test
	public void 회원가입_입력_검증_테스트() {
		// given
		UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);

		// when & then - 잘못된 이메일 형식
		try {
			userAppService.register("invalid-email", "사용자", "password123");
			fail("잘못된 이메일 형식으로 인한 예외가 발생해야 함");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("올바른 이메일 형식이 아닙니다"));
		}

		// when & then - 짧은 비밀번호
		try {
			userAppService.register("test@example.com", "사용자", "123");
			fail("짧은 비밀번호로 인한 예외가 발생해야 함");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("비밀번호는 최소"));
		}

		// when & then - 빈 이름
		try {
			userAppService.register("test@example.com", "", "password123");
			fail("빈 이름으로 인한 예외가 발생해야 함");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("이름은 필수입니다"));
		}
	}

	@Test
	public void 존재하지_않는_사용자_조회_테스트() {
		// given
		UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
		UserId nonExistentUserId = UserId.of(EntityIdentifier.generateUuid());

		// when & then
		try {
			userAppService.findByIdOrThrow(nonExistentUserId);
			fail("존재하지 않는 사용자 조회 시 예외가 발생해야 함");
		} catch (DomainException e) {
			assertTrue(e.getMessage().contains("사용자를 찾을 수 없습니다"));
		}
	}

	@Test
	public void 긴_이메일_검증_테스트() {
		// given
		UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
		// MAX_EMAIL_LENGTH(50)을 초과하는 이메일 생성
		String longEmailPrefix = repeatString("a", MAX_EMAIL_LENGTH - "@test.com".length() + 1);
		String longEmail = longEmailPrefix + "@test.com";

		// when & then
		try {
			userAppService.register(longEmail, "사용자", "password123");
			fail("긴 이메일로 인한 예외가 발생해야 함");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("이메일은 " + MAX_EMAIL_LENGTH + "자를 초과할 수 없습니다"));
		}
	}

	@Test
	public void 긴_이름_검증_테스트() {
		// given
		UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
		String email = generateUniqueEmail();
		// MAX_NAME_LENGTH(20)을 초과하는 이름 생성
		String longName = repeatString("가", MAX_NAME_LENGTH + 1);

		// when & then
		try {
			userAppService.register(email, longName, "password123");
			fail("긴 이름으로 인한 예외가 발생해야 함");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("이름은 " + MAX_NAME_LENGTH + "자를 초과할 수 없습니다"));
		}
	}

	@Test
	public void 긴_비밀번호_검증_테스트() {
		// given
		UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
		String email = generateUniqueEmail();
		// MAX_PASSWORD_LENGTH(100)을 초과하는 비밀번호 생성
		String longPassword = repeatString("a", MAX_PASSWORD_LENGTH + 1);

		// when & then
		try {
			userAppService.register(email, "사용자", longPassword);
			fail("긴 비밀번호로 인한 예외가 발생해야 함");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("비밀번호는 " + MAX_PASSWORD_LENGTH + "자를 초과할 수 없습니다"));
		}
	}

	@Test
	public void 전체_플로우_통합_테스트() {
		// given
		UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
		String email = generateUniqueEmail();
		String password = "password123";
		String name = "통합테스트사용자";

		// 1. 회원가입
		User registeredUser = userAppService.register(email, name, password);
		assertNotNull("회원가입 성공", registeredUser);
		assertTrue("비밀번호 암호화 확인", passwordEncoder.matches(password, registeredUser.getPassword()));

		// 2. 로그인
		User authenticatedUser = userAppService.authenticate(email, password);
		assertEquals("로그인 성공", registeredUser.getId(), authenticatedUser.getId());

		// 3. 이메일 변경
		String newEmail = "new" + generateUniqueEmail();
		User emailChangedUser = userAppService.changeEmail(registeredUser.getId(), newEmail);
		assertEquals("이메일 변경 성공", newEmail, emailChangedUser.getEmail());

		// 4. 이름 변경
		String newName = "변경된이름";
		User nameChangedUser = userAppService.changeName(registeredUser.getId(), newName);
		assertEquals("이름 변경 성공", newName, nameChangedUser.getName());

		// 5. 비밀번호 변경
		String newPassword = "newPassword456";
		User passwordChangedUser = userAppService.changePassword(registeredUser.getId(), password, newPassword);
		assertTrue("새 비밀번호 적용 확인", passwordEncoder.matches(newPassword, passwordChangedUser.getPassword()));

		// 6. 새 정보로 로그인
		User reAuthenticatedUser = userAppService.authenticate(newEmail, newPassword);
		assertNotNull("새 정보로 로그인 성공", reAuthenticatedUser);
		assertEquals("사용자 ID 일치", registeredUser.getId(), reAuthenticatedUser.getId());
	}
}