package com.eggmoney.payv.domain.model.entity;

import java.time.LocalDateTime;

import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.domain.shared.util.EntityIdentifier;

import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 식별(로그인 주체), 기본 속성(email) 보관, 소유자/작성자 기준 키 제공.
 * 
 * @author 정의탁
 */
@Getter
public class User {

	private UserId id;
	private String email;
	private String name;
	private String password; // 암호화된 비밀번호
	private LocalDateTime createdAt;

	@Builder
	public User(UserId id, String email, String name, String password, LocalDateTime createdAt) {
		this.id = id;
		this.email = email;
		this.name = name;
		this.password = password;
		this.createdAt = createdAt;
	}

	// 가계부 생성.
	public Ledger createLedger(String ledgerName) {
		
		return Ledger.create(this.id, ledgerName);
	}

	private User(UserId id, String email, String name, String password, UserRole role) {
		if (id == null)
			throw new IllegalArgumentException("id is required");
		if (email == null)
			throw new IllegalArgumentException("email is required");
		if (password == null)
			throw new IllegalArgumentException("password is required");
		if (role == null)
			throw new IllegalArgumentException("rele is required");

		this.id = id;
		this.email = email;
		this.name = name;
		this.password = password;
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

	// 이메일 변경.
	public void changeEmail(String newEmail) {
		if (newEmail == null) {
			throw new IllegalArgumentException("email is required");
		}
		this.email = newEmail;
	}

	// 비밀번호 변경
	public void changePassword(String encodedPassword) {
		if (encodedPassword == null) {
			throw new IllegalArgumentException("password is required");
		}
		this.password = encodedPassword;
	}
}
