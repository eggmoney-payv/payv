package com.eggmoney.payv.domain.model.entity;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eggmoney.payv.domain.model.vo.UserRole;

/**
 * User 도메인 엔티티 단위 테스트
 * 
 * @author 정의탁
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

		// 권한 확인
		assertTrue(user.isUser());
		assertFalse(user.isPremium());
		assertFalse(user.isAdmin());
		assertFalse(user.hasPremiumOrAbove());
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

		// 권한 확인 - 정확히 구분
		assertFalse(admin.isUser()); // ADMIN은 USER가 아님
		assertFalse(admin.isPremium()); // ADMIN은 PREMIUM이 아님
		assertTrue(admin.isAdmin()); // ADMIN은 ADMIN임
		assertTrue(admin.hasPremiumOrAbove()); // ADMIN은 PREMIUM 이상의 권한
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

		// 권한 확인
		assertFalse(premium.isUser()); // PREMIUM은 USER가 아님
		assertTrue(premium.isPremium()); // PREMIUM은 PREMIUM임
		assertFalse(premium.isAdmin()); // PREMIUM은 ADMIN이 아님
		assertTrue(premium.hasPremiumOrAbove()); // PREMIUM은 PREMIUM 이상의 권한
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
		assertTrue(user.isPremium());
		assertFalse(user.isAdmin());
		assertFalse(user.isUser());
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

		// 권한 확인
		assertTrue(normalUser.hasAuthorityOf(UserRole.USER));
		assertFalse(normalUser.hasAuthorityOf(UserRole.PREMIUM));
		assertFalse(normalUser.hasAuthorityOf(UserRole.ADMIN));

		assertTrue(premiumUser.hasAuthorityOf(UserRole.USER));
		assertTrue(premiumUser.hasAuthorityOf(UserRole.PREMIUM));
		assertFalse(premiumUser.hasAuthorityOf(UserRole.ADMIN));

		assertTrue(adminUser.hasAuthorityOf(UserRole.USER));
		assertTrue(adminUser.hasAuthorityOf(UserRole.PREMIUM));
		assertTrue(adminUser.hasAuthorityOf(UserRole.ADMIN));
	}

	@Test
	public void 권한_변경_가능_여부_테스트() {
		// given
		User admin = User.createAdmin("admin@example.com", "admin", "password");
		User normalUser = User.create("user@example.com", "user", "password");
		User otherAdmin = User.createAdmin("admin2@example.com", "admin2", "password");

		// then
		assertTrue(admin.canChangeRoleOf(normalUser)); // 관리자는 일반 사용자 권한 변경 가능
		assertFalse(admin.canChangeRoleOf(admin)); // 자기 자신의 권한은 변경할 수 없음
		assertTrue(admin.canChangeRoleOf(otherAdmin)); // 다른 관리자의 권한은 변경 가능

		assertFalse(normalUser.canChangeRoleOf(admin)); // 일반 사용자는 관리자 권한 변경 불가
		assertFalse(normalUser.canChangeRoleOf(normalUser)); // 자기 자신도 변경 불가 (관리자가 아니므로)
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