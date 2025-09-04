package com.eggmoney.payv.domain.model.entity;

import java.time.LocalDateTime;

import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.domain.shared.util.EntityIdentifier;

import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 식별(로그인 주체), 기본 속성(email) 보관, 소유자/작성자 기준 키 제공.
 * @author 정의탁
 */
@Getter
public class User {

	private UserId id;
	private String email;
	private String name;
	private LocalDateTime createdAt;
	
	@Builder
	public User(UserId id, String email, String name, LocalDateTime createdAt) {
		this.id = id;
		this.email = email;
		this.name = name;
		this.createdAt = createdAt;
	}
	
	// 가계부 생성.
	public Ledger createLedger(String ledgerName) {
		return Ledger.create(this.id, ledgerName);
	}
	
	private User(UserId id, String email, String name) {
		if (id == null) throw new IllegalArgumentException("id is required");
        if (email == null) throw new IllegalArgumentException("email is required");

        this.id = id;
        this.email = email;
        this.name = name;
        this.createdAt = LocalDateTime.now();
	}
	
	public static User create(String email, String name){
		return new User(UserId.of(EntityIdentifier.generateUuid()), email, name);
	}
	
	// 이메일 변경.
	public void changeEmail(String newEmail){
        if (newEmail == null) {
        	throw new IllegalArgumentException("email is required");
        }
        this.email = newEmail;
    }
	
	public static User of(UserId id) {
        return new User(id, null, null);
    }
}
