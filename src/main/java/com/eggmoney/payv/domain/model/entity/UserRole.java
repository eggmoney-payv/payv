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
	
	/**
     * Spring Security에서 사용할 권한 문자열 반환
     * @return "ROLE_USER" 또는 "ROLE_ADMIN"
     */
	public String getAuthority() {
		return authority;
	}
}
