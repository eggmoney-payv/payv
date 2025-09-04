package com.eggmoney.payv.domain.model.entity;

/**
 * 사용자 권한 유형
 * @author 강기범
 */
public enum UserRole {

	USER("ROLE_USER"),		// 일반 사용자
	ADMIN("ROLE_ADMIN");	// 관리자

	private final String authority;
	
	UserRole(String authority) {
		this.authority = authority;
	}
	
	public String getAuthority() {
		return authority;
	}
}
