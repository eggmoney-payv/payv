package com.eggmoney.payv.domain.model.entity;

import java.time.LocalDateTime;

import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.domain.model.vo.UserRole;
import com.eggmoney.payv.domain.shared.util.EntityIdentifier;

import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 엔티티 - 사용자 식별(로그인 주체), 기본 속성(email) 보관, 소유자/작성자 기준 키 제공.
 * 
 * @author 정의탁, 강기범
 */
@Getter
public class User {

	private UserId id;
	private String email;
	private String name;
	private String password; // 암호화된 비밀번호
	private UserRole role;   // VO 패키지의 UserRole 사용
	private LocalDateTime createdAt;

	@Builder
	public User(UserId id, String email, String name, String password, UserRole role, LocalDateTime createdAt) {
		this.id = id;
		this.email = email;
		this.name = name;
		this.password = password;
		this.role = role;
		this.createdAt = createdAt;
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
		if (role == null)
			throw new IllegalArgumentException("role is required");

		this.id = id;
		this.email = email.trim();
		this.name = name != null ? name.trim() : null;
		this.password = password;
		this.role = role;
		this.createdAt = LocalDateTime.now();
	}

	// 일반유저 회원가입
	public static User create(String email, String name, String encodedPassword) {
		return new User(UserId.of(EntityIdentifier.generateUuid()), email, name, encodedPassword, UserRole.USER);
	}

	// 관리자 생성
	public static User createAdmin(String email, String name, String encodedPassword) {
		return new User(UserId.of(EntityIdentifier.generateUuid()), email, name, encodedPassword, UserRole.ADMIN);
	}

	// 프리미엄 사용자 생성
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

	// 역할 변경 (관리자용)
	public void changeRole(UserRole newRole) {
		if (newRole == null) {
			throw new IllegalArgumentException("role is required");
		}
		this.role = newRole;
	}

	// === 도메인 비즈니스 로직 ===
	
	/**
	 * 관리자 권한 확인 (정확히 ADMIN인지)
	 */
	public boolean isAdmin() {
		return role.isAdmin();
	}

	/**
	 * 프리미엄 사용자 확인 (정확히 PREMIUM인지)
	 */
	public boolean isPremium() {
		return role.isPremium();
	}
	
	/**
	 * 일반 사용자 확인 (정확히 USER인지)
	 */
	public boolean isUser() {
		return role.isUser();
	}
	
	/**
	 * 프리미엄 이상의 권한인지 확인 (PREMIUM 또는 ADMIN)
	 */
	public boolean hasPremiumOrAbove() {
		return role.isPremiumOrAbove();
	}
	
	/**
	 * 특정 권한 이상인지 확인
	 */
	public boolean hasAuthorityOf(UserRole requiredRole) {
		return role.hasAuthorityOf(requiredRole);
	}
	
	/**
	 * 다른 사용자에 대한 권한 변경 가능 여부
	 */
	public boolean canChangeRoleOf(User targetUser) {
		// 관리자만 다른 사용자의 권한 변경 가능
		// 단, 자기 자신의 관리자 권한은 해제할 수 없음
		return this.isAdmin() && !targetUser.getId().equals(this.getId());
	}
	
	/**
	 * 특정 가계부에 대한 접근 권한이 있는지 확인
	 */
	public boolean canAccessLedger(Ledger ledger) {
		// 소유자이거나 관리자인 경우 접근 가능
		return ledger.getOwnerId().equals(this.id) || this.isAdmin();
	}
}