package com.eggmoney.payv.domain.model.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Test;

/**
 * User 엔티티 단위 테스트
 * 
 * TDD 순서:
 * 1. 이 테스트를 먼저 작성 (RED 상태)
 * 2. User 엔티티 수정해서 테스트 통과 (GREEN 상태)
 * 3. 코드 리팩토링 (REFACTOR)
 * 
 * @author [작성자명]
 */
public class UserTest {

    /**
     * 테스트 1: 기본 사용자 생성 테스트
     * - 회원가입시 일반 사용자(USER 권한)로 생성되는지 확인
     */
    @Test
    public void test_일반사용자_생성시_USER권한_부여() {
        // Given (준비): 회원가입에 필요한 정보
        String email = "test@example.com";
        String name = "홍길동";
        String encodedPassword = "$2a$10$xxxxx"; // BCrypt로 암호화된 비밀번호
        
        // When (실행): User 생성
        User user = User.create(email, name, encodedPassword);
        
        // Then (검증): 
        assertNotNull("사용자 객체가 null이면 안됨", user);
        assertNotNull("사용자 ID가 자동 생성되어야 함", user.getId());
        assertEquals("이메일이 일치해야 함", email, user.getEmail());
        assertEquals("이름이 일치해야 함", name, user.getName());
        assertEquals("비밀번호가 일치해야 함", encodedPassword, user.getPassword());
        
        // 중요! 일반 사용자는 USER 권한을 가져야 함
        assertEquals("일반 사용자는 USER 권한이어야 함", 
                    UserRole.USER, user.getRole());
        
        // 신규 가입자는 활성화 상태여야 함
        assertTrue("신규 가입자는 활성화 상태여야 함", user.isEnabled());
        
        // 생성 시간은 현재 시간과 가까워야 함 (1초 이내)
        assertNotNull("생성 시간이 설정되어야 함", user.getCreatedAt());
        assertTrue("생성 시간이 현재와 가까워야 함", 
                  user.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
    
    /**
     * 테스트 2: 관리자 생성 테스트
     * - 관리자 계정 생성시 ADMIN 권한 부여되는지 확인
     */
    @Test
    public void test_관리자_생성시_ADMIN권한_부여() {
        // Given
        String email = "admin@example.com";
        String name = "관리자";
        String encodedPassword = "$2a$10$admin";
        
        // When: 관리자 생성 (별도의 팩토리 메서드 사용)
        User admin = User.createAdmin(email, name, encodedPassword);
        
        // Then
        assertEquals("관리자는 ADMIN 권한이어야 함", 
                    UserRole.ADMIN, admin.getRole());
        assertTrue("isAdmin() 메서드가 true를 반환해야 함", 
                  admin.isAdmin());
    }
    
    /**
     * 테스트 3: 필수 필드 검증 테스트
     * - null 값으로 생성시 예외 발생하는지 확인
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_이메일없이_사용자생성시_예외발생() {
        // Given
        String email = null; // 이메일 없음!
        String name = "홍길동";
        String encodedPassword = "$2a$10$xxxxx";
        
        // When & Then: 예외가 발생해야 함
        User.create(email, name, encodedPassword);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_비밀번호없이_사용자생성시_예외발생() {
        // Given
        String email = "test@example.com";
        String name = "홍길동";
        String encodedPassword = null; // 비밀번호 없음!
        
        // When & Then: 예외가 발생해야 함
        User.create(email, name, encodedPassword);
    }
    
    /**
     * 테스트 4: 이메일 변경 테스트
     */
    @Test
    public void test_이메일_변경() {
        // Given: 기존 사용자
        User user = User.create("old@example.com", "홍길동", "$2a$10$xxxxx");
        String newEmail = "new@example.com";
        
        // When: 이메일 변경
        user.changeEmail(newEmail);
        
        // Then: 이메일이 변경되었는지 확인
        assertEquals("이메일이 변경되어야 함", newEmail, user.getEmail());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_null이메일로_변경시_예외발생() {
        // Given
        User user = User.create("test@example.com", "홍길동", "$2a$10$xxxxx");
        
        // When & Then: null로 변경시 예외
        user.changeEmail(null);
    }
    
    /**
     * 테스트 5: 비밀번호 변경 테스트
     */
    @Test
    public void test_비밀번호_변경() {
        // Given
        User user = User.create("test@example.com", "홍길동", "$2a$10$old");
        String newPassword = "$2a$10$new";
        
        // When
        user.changePassword(newPassword);
        
        // Then
        assertEquals("비밀번호가 변경되어야 함", newPassword, user.getPassword());
    }
    
    /**
     * 테스트 6: 로그인 시간 업데이트 테스트
     */
    @Test
    public void test_로그인시간_업데이트() {
        // Given
        User user = User.create("test@example.com", "홍길동", "$2a$10$xxxxx");
        assertNull("처음에는 로그인 시간이 null이어야 함", user.getLastLoginAt());
        
        // When
        user.updateLastLogin();
        
        // Then
        assertNotNull("로그인 후에는 시간이 설정되어야 함", user.getLastLoginAt());
        assertTrue("로그인 시간이 현재 시간과 가까워야 함",
                  user.getLastLoginAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
    
    /**
     * 테스트 7: 계정 활성화/비활성화 테스트
     */
    @Test
    public void test_계정_활성화_비활성화() {
        // Given
        User user = User.create("test@example.com", "홍길동", "$2a$10$xxxxx");
        assertTrue("기본적으로 활성화 상태여야 함", user.isEnabled());
        
        // When: 비활성화
        user.deactivate();
        
        // Then
        assertFalse("비활성화 후 false여야 함", user.isEnabled());
        
        // When: 다시 활성화
        user.activate();
        
        // Then
        assertTrue("재활성화 후 true여야 함", user.isEnabled());
    }
    
    /**
     * 테스트 8: 비활성화된 계정은 가계부 생성 불가
     */
    @Test(expected = IllegalStateException.class)
    public void test_비활성화계정_가계부생성시_예외발생() {
        // Given: 비활성화된 사용자
        User user = User.create("test@example.com", "홍길동", "$2a$10$xxxxx");
        user.deactivate();
        
        // When & Then: 가계부 생성시 예외 발생
        user.createLedger("내 가계부");
    }
    
    /**
     * 테스트 9: 활성화된 계정은 가계부 생성 가능
     */
    @Test
    public void test_활성화계정_가계부생성_성공() {
        // Given: 활성화된 사용자
        User user = User.create("test@example.com", "홍길동", "$2a$10$xxxxx");
        
        // When
        Ledger ledger = user.createLedger("내 가계부");
        
        // Then
        assertNotNull("가계부가 생성되어야 함", ledger);
        assertEquals("가계부 소유자가 일치해야 함", 
                    user.getId(), ledger.getOwnerId());
        assertEquals("가계부 이름이 일치해야 함", 
                    "내 가계부", ledger.getName());
    }
}