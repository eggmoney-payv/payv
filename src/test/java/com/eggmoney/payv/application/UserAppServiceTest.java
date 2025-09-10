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
import com.eggmoney.payv.domain.shared.error.DomainException;

/**
 * 향상된 UserAppService 통합 테스트 비밀번호 암호화, 개인정보 변경 등의 기능 테스트
 * 
 * @author 정의탁
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/root-context.xml"
		// security-context.xml 제거! (root-context.xml에서 이미 import함)
})
@Transactional
public class UserAppServiceTest {

	@Resource
	UserRepository userRepository;

	@Resource
	PasswordEncoder passwordEncoder;

	private String generateUniqueEmail() {
		return "test" + System.nanoTime() + "@example.com";
	}

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

		// 비밀번호가 암호화되었는지 확인
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

		// DB에서 다시 조회해서 확인
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

		// DB에서 다시 조회해서 확인
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
}