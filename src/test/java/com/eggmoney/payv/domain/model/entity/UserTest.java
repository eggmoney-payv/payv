package com.eggmoney.payv.domain.model.entity;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eggmoney.payv.domain.model.vo.UserRole;

/**
 * User 도메인 엔티티 단위 테스트 - 수정된 버전 실제 User 클래스의 권한 체계에 맞춰 테스트 수정
 * 
 * @author 정의탁, 강기범
 */
public class UserTest {

	@Test
	public void 일반_사용자_생성_테스트() {
		// given
		String email = "test@example.com";
		String name = "테스트 사용자";
		String password = "encodedPassword123";

		// when
		User user = User.create(email, name, password);

		// then
		assertNotNull(user);
		assertNotNull(user.getId());
		assertEquals(email, user.getEmail());
		assertEquals(name, user.getName());
		assertEquals(password, user.getPassword());
		assertEquals(UserRole.USER, user.getRole());
		assertNotNull(user.getCreatedAt());

		// 실제 User 클래스의 hasAuthorityOf 메서드에 맞춘 권한 확인
		assertTrue("USER는 USER 권한을 가져야 함", user.hasAuthorityOf(UserRole.USER));
		assertFalse("USER는 PREMIUM 권한을 가지지 않아야 함", user.hasAuthorityOf(UserRole.PREMIUM));
		assertFalse("USER는 ADMIN 권한을 가지지 않아야 함", user.hasAuthorityOf(UserRole.ADMIN));
	}

	@Test
	public void 관리자_생성_테스트() {
		// given
		String email = "admin@example.com";
		String name = "관리자";
		String password = "encodedPassword123";

		// when
		User admin = User.createAdmin(email, name, password);

		// then
		assertNotNull(admin);
		assertEquals(UserRole.ADMIN, admin.getRole());

		// 실제 hasAuthorityOf 메서드 동작: ADMIN은 모든 권한을 가짐
		assertTrue("ADMIN은 USER 권한을 가져야 함", admin.hasAuthorityOf(UserRole.USER));
		assertTrue("ADMIN은 PREMIUM 권한을 가져야 함", admin.hasAuthorityOf(UserRole.PREMIUM));
		assertTrue("ADMIN은 ADMIN 권한을 가져야 함", admin.hasAuthorityOf(UserRole.ADMIN));
	}

	@Test
	public void 프리미엄_사용자_생성_테스트() {
		// given
		String email = "premium@example.com";
		String name = "프리미엄 사용자";
		String password = "encodedPassword123";

		// when
		User premium = User.createPremium(email, name, password);

		// then
		assertNotNull(premium);
		assertEquals(UserRole.PREMIUM, premium.getRole());

		// 실제 hasAuthorityOf 메서드 동작: PREMIUM은 USER 권한도 가짐
		assertTrue("PREMIUM은 USER 권한을 가져야 함", premium.hasAuthorityOf(UserRole.USER));
		assertTrue("PREMIUM은 PREMIUM 권한을 가져야 함", premium.hasAuthorityOf(UserRole.PREMIUM));
		assertFalse("PREMIUM은 ADMIN 권한을 가지지 않아야 함", premium.hasAuthorityOf(UserRole.ADMIN));
	}

	@Test(expected = IllegalArgumentException.class)
	public void 이메일_null_예외_테스트() {
		User.create(null, "name", "password");
	}

	@Test(expected = IllegalArgumentException.class)
	public void 이메일_빈문자열_예외_테스트() {
		User.create("", "name", "password");
	}

	@Test(expected = IllegalArgumentException.class)
	public void 이메일_공백_예외_테스트() {
		User.create("   ", "name", "password");
	}

	@Test(expected = IllegalArgumentException.class)
	public void 비밀번호_null_예외_테스트() {
		User.create("test@example.com", "name", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void 비밀번호_빈문자열_예외_테스트() {
		User.create("test@example.com", "name", "");
	}

	@Test
	public void 이메일_변경_테스트() {
		// given
		User user = User.create("old@example.com", "name", "password");
		String newEmail = "new@example.com";

		// when
		user.changeEmail(newEmail);

		// then
		assertEquals(newEmail, user.getEmail());
	}

	@Test
	public void 이메일_변경시_공백_제거_테스트() {
		// given
		User user = User.create("old@example.com", "name", "password");
		String newEmail = "  new@example.com  ";

		// when
		user.changeEmail(newEmail);

		// then
		assertEquals("new@example.com", user.getEmail());
	}

	@Test(expected = IllegalArgumentException.class)
	public void 이메일_변경_null_예외_테스트() {
		User user = User.create("old@example.com", "name", "password");
		user.changeEmail(null);
	}

	@Test
	public void 비밀번호_변경_테스트() {
		// given
		User user = User.create("test@example.com", "name", "oldPassword");
		String newPassword = "newEncodedPassword";

		// when
		user.changePassword(newPassword);

		// then
		assertEquals(newPassword, user.getPassword());
	}

	@Test(expected = IllegalArgumentException.class)
	public void 비밀번호_변경_null_예외_테스트() {
		User user = User.create("test@example.com", "name", "password");
		user.changePassword(null);
	}

	@Test
	public void 이름_변경_테스트() {
		// given
		User user = User.create("test@example.com", "oldName", "password");
		String newName = "newName";

		// when
		user.changeName(newName);

		// then
		assertEquals(newName, user.getName());
	}

	@Test
	public void 이름_변경_null_허용_테스트() {
		// given
		User user = User.create("test@example.com", "oldName", "password");

		// when
		user.changeName(null);

		// then
		assertNull(user.getName());
	}

	@Test
	public void 역할_변경_테스트() {
		// given
		User user = User.create("test@example.com", "name", "password");

		// when
		user.changeRole(UserRole.PREMIUM);

		// then
		assertEquals(UserRole.PREMIUM, user.getRole());
		assertTrue("PREMIUM으로 변경 후 PREMIUM 권한 확인", user.hasAuthorityOf(UserRole.PREMIUM));
		assertTrue("PREMIUM으로 변경 후 USER 권한도 확인", user.hasAuthorityOf(UserRole.USER));
		assertFalse("PREMIUM으로 변경 후 ADMIN 권한은 없어야 함", user.hasAuthorityOf(UserRole.ADMIN));
	}

	@Test(expected = IllegalArgumentException.class)
	public void 역할_변경_null_예외_테스트() {
		User user = User.create("test@example.com", "name", "password");
		user.changeRole(null);
	}

	@Test
	public void 권한_체계_테스트() {
		// given
		User normalUser = User.create("user@example.com", "user", "password");
		User premiumUser = User.createPremium("premium@example.com", "premium", "password");
		User adminUser = User.createAdmin("admin@example.com", "admin", "password");

		// USER 권한 체계 확인
		assertTrue("일반 사용자는 USER 권한을 가져야 함", normalUser.hasAuthorityOf(UserRole.USER));
		assertFalse("일반 사용자는 PREMIUM 권한을 가지지 않아야 함", normalUser.hasAuthorityOf(UserRole.PREMIUM));
		assertFalse("일반 사용자는 ADMIN 권한을 가지지 않아야 함", normalUser.hasAuthorityOf(UserRole.ADMIN));

		// PREMIUM 권한 체계 확인
		assertTrue("프리미엄 사용자는 USER 권한을 가져야 함", premiumUser.hasAuthorityOf(UserRole.USER));
		assertTrue("프리미엄 사용자는 PREMIUM 권한을 가져야 함", premiumUser.hasAuthorityOf(UserRole.PREMIUM));
		assertFalse("프리미엄 사용자는 ADMIN 권한을 가지지 않아야 함", premiumUser.hasAuthorityOf(UserRole.ADMIN));

		// ADMIN 권한 체계 확인
		assertTrue("관리자는 USER 권한을 가져야 함", adminUser.hasAuthorityOf(UserRole.USER));
		assertTrue("관리자는 PREMIUM 권한을 가져야 함", adminUser.hasAuthorityOf(UserRole.PREMIUM));
		assertTrue("관리자는 ADMIN 권한을 가져야 함", adminUser.hasAuthorityOf(UserRole.ADMIN));
	}

	@Test
	public void 권한_변경_가능_여부_테스트() {
		// given
		User admin = User.createAdmin("admin@example.com", "admin", "password");
		User normalUser = User.create("user@example.com", "user", "password");
		User otherAdmin = User.createAdmin("admin2@example.com", "admin2", "password");

		// then
		assertTrue("관리자는 일반 사용자 권한 변경 가능", admin.canChangeRoleOf(normalUser));
		assertFalse("자기 자신의 권한은 변경할 수 없음", admin.canChangeRoleOf(admin));
		assertTrue("다른 관리자의 권한은 변경 가능", admin.canChangeRoleOf(otherAdmin));

		assertFalse("일반 사용자는 관리자 권한 변경 불가", normalUser.canChangeRoleOf(admin));
		assertFalse("일반 사용자는 자기 자신도 변경 불가 (관리자가 아니므로)", normalUser.canChangeRoleOf(normalUser));
	}

	@Test
	public void 가계부_생성_테스트() {
		// given
		User user = User.create("test@example.com", "name", "password");
		String ledgerName = "내 가계부";

		// when
		Ledger ledger = user.createLedger(ledgerName);

		// then
		assertNotNull(ledger);
		assertEquals(user.getId(), ledger.getOwnerId());
		assertEquals(ledgerName, ledger.getName());
	}
}