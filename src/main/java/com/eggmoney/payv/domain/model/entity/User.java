package com.eggmoney.payv.domain.model.entity;

import java.sql.Timestamp;

import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.domain.model.vo.UserRole;
import com.eggmoney.payv.domain.shared.util.EntityIdentifier;

import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 엔티티 - 사용자 식별(로그인 주체), 기본 속성(email) 보관, 소유자/작성자 기준 키 제공. DB에 ROLE 컬럼이 없으므로
 * 기본적으로 USER role로 설정
 * 
 * @author 정의탁, 강기범
 */
@Getter
public class User {

	private UserId id;
	private String email;
	private String name;
	private String password; // 암호화된 비밀번호
	private UserRole role; // DB에 저장되지 않음, 메모리상에서만 사용
	private Timestamp createAt; // ⭐ 이미 Timestamp로 되어있음

	@Builder
	public User(UserId id, String email, String name, String password, UserRole role, Timestamp createAt) {
		this.id = id;
		this.email = email;
		this.name = name;
		this.password = password;
		this.role = role != null ? role : UserRole.USER; // null이면 기본값 USER
		this.createAt = createAt != null ? createAt : new Timestamp(System.currentTimeMillis()); // ⭐ null 체크 추가
	}

	// 가계부 생성.
	public Ledger createLedger(String ledgerName) {
		return Ledger.create(this.id, ledgerName);
	}

	private User(UserId id, String email, String name, String password, UserRole role) {
		if (id == null)
			throw new IllegalArgumentException("id is required");
		if (email == null || email.trim().isEmpty())
			throw new IllegalArgumentException("email is required");
		if (password == null || password.trim().isEmpty())
			throw new IllegalArgumentException("password is required");

		this.id = id;
		this.email = email.trim();
		this.name = name != null ? name.trim() : null;
		this.password = password;
		this.role = role != null ? role : UserRole.USER; // 기본값 USER
		this.createAt = new Timestamp(System.currentTimeMillis()); // ⭐ Timestamp로 수정
	}

	// 일반유저 회원가입 - DB에 ROLE이 없으므로 모두 USER로 생성
	public static User create(String email, String name, String encodedPassword) {
		return new User(UserId.of(EntityIdentifier.generateUuid()), email, name, encodedPassword, UserRole.USER);
	}

	// 관리자 생성 - 메모리상에서만 ADMIN으로 설정 (DB에는 저장되지 않음)
	public static User createAdmin(String email, String name, String encodedPassword) {
		return new User(UserId.of(EntityIdentifier.generateUuid()), email, name, encodedPassword, UserRole.ADMIN);
	}

	// 프리미엄 사용자 생성 - 메모리상에서만 PREMIUM으로 설정 (DB에는 저장되지 않음)
	public static User createPremium(String email, String name, String encodedPassword) {
		return new User(UserId.of(EntityIdentifier.generateUuid()), email, name, encodedPassword, UserRole.PREMIUM);
	}

	// 이메일 변경.
	public void changeEmail(String newEmail) {
		if (newEmail == null || newEmail.trim().isEmpty()) {
			throw new IllegalArgumentException("email is required");
		}
		this.email = newEmail.trim();
	}

	// 비밀번호 변경
	public void changePassword(String encodedPassword) {
		if (encodedPassword == null || encodedPassword.trim().isEmpty()) {
			throw new IllegalArgumentException("password is required");
		}
		this.password = encodedPassword;
	}

	// 이름 변경
	public void changeName(String newName) {
		this.name = newName != null ? newName.trim() : null;
	}

	// 역할 변경 - 메모리상에서만 변경됨 (DB에는 저장되지 않음)
	public void changeRole(UserRole newRole) {
		if (newRole == null) {
			throw new IllegalArgumentException("role is required");
		}
		this.role = newRole;
	}

	// 권한 확인 메서드들 - DB에 role이 없으므로 기본적으로 모든 사용자는 USER
	public boolean hasAuthorityOf(UserRole requiredRole) {
		// DB에서 조회된 사용자는 항상 USER role이므로
		if (requiredRole == UserRole.USER) {
			return true;
		}
		// 메모리상에서 role이 설정된 경우에만 추가 권한 확인
		if (this.role == UserRole.ADMIN) {
			return true; // 관리자는 모든 권한을 가짐
		}
		if (this.role == UserRole.PREMIUM && requiredRole == UserRole.USER) {
			return true; // 프리미엄은 일반 사용자 권한도 가짐
		}
		return this.role == requiredRole;
	}

	public boolean canChangeRoleOf(User targetUser) {
		return this.role == UserRole.ADMIN && !this.id.equals(targetUser.getId());
	}
}