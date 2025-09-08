package com.eggmoney.payv.domain.model.vo;

/**
 * 사용자 역할 Value Object
 * - 시스템에서 정의하는 고정된 역할 값들
 * - 불변(immutable)하며 식별자가 없는 값 객체
 * 
 * @author 정의탁, 강기범
 */
public enum UserRole {
    USER("ROLE_USER", "일반 사용자"),
    PREMIUM("ROLE_PREMIUM", "프리미엄 사용자"),
    ADMIN("ROLE_ADMIN", "관리자");
    
    private final String authority;
    private final String description;
    
    UserRole(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }
    
    /**
     * Spring Security에서 사용할 권한 문자열 반환
     * @return "ROLE_USER", "ROLE_PREMIUM", "ROLE_ADMIN"
     */
    public String getAuthority() {
        return authority;
    }
    
    /**
     * 역할에 대한 설명 반환
     * @return 역할 설명
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 관리자 권한 여부 확인 (정확히 ADMIN인지)
     * @return 관리자면 true
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    /**
     * 프리미엄 권한 여부 확인 (정확히 PREMIUM인지)
     * @return 프리미엄이면 true
     */
    public boolean isPremium() {
        return this == PREMIUM;
    }
    
    /**
     * 일반 사용자 권한 여부 확인 (정확히 USER인지)
     * @return 일반 사용자면 true
     */
    public boolean isUser() {
        return this == USER;
    }
    
    /**
     * 프리미엄 이상의 권한인지 확인 (PREMIUM 또는 ADMIN)
     * @return 프리미엄 이상이면 true
     */
    public boolean isPremiumOrAbove() {
        return this == PREMIUM || this == ADMIN;
    }
    
    /**
     * 특정 권한 이상인지 확인
     * @param requiredRole 필요한 최소 권한
     * @return 권한 충족 여부
     */
    public boolean hasAuthorityOf(UserRole requiredRole) {
        // ADMIN > PREMIUM > USER 순서
        return getLevel() >= requiredRole.getLevel();
    }
    
    /**
     * 권한 레벨 반환 (높을수록 강한 권한)
     * @return 권한 레벨
     */
    private int getLevel() {
        switch (this) {
            case USER: return 1;
            case PREMIUM: return 2;
            case ADMIN: return 3;
            default: throw new IllegalStateException("Unknown role: " + this);
        }
    }
}