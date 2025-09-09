package com.eggmoney.payv.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.eggmoney.payv.application.service.UserAppService;
import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.repository.UserRepository;
import com.eggmoney.payv.domain.shared.error.DomainException;

/**
 * 향상된 UserAppService 통합 테스트
 * 비밀번호 암호화, 개인정보 변경 등의 기능 테스트
 * 
 * @author 정의탁
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	    "classpath:spring/root-context.xml",
	    "classpath:spring/security-context.xml"
	})
@Transactional
public class UserAppServiceTest {

    @Resource 
    UserRepository userRepository;
    
    @Resource
    PasswordEncoder passwordEncoder;

    private String generateUniqueEmail() {
        return "test" + System.nanoTime() + "@example.com";
    }

    @Test
    public void 회원가입_비밀번호_암호화_테스트() {
        // given
        UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
        String email = generateUniqueEmail();
        String rawPassword = "testPassword123";
        String name = "테스트사용자";

        // when
        User user = userAppService.register(email, name, rawPassword);

        // then
        assertNotNull(user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(name, user.getName());
        
        // 비밀번호가 암호화되었는지 확인
        assertNotEquals(rawPassword, user.getPassword());
        assertTrue("암호화된 비밀번호가 원본과 매치되어야 함", 
                  passwordEncoder.matches(rawPassword, user.getPassword()));
    }

    @Test
    public void 이메일_변경_성공_테스트() {
        // given
        UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
        String originalEmail = generateUniqueEmail();
        User user = userAppService.register(originalEmail, "사용자", "password123");
        
        String newEmail = generateUniqueEmail();

        // when
        User updatedUser = userAppService.changeEmail(user.getId(), newEmail);

        // then
        assertEquals(newEmail, updatedUser.getEmail());
        
        // DB에서 다시 조회해서 확인
        User reloadedUser = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(reloadedUser);
        assertEquals(newEmail, reloadedUser.getEmail());
    }

    @Test
    public void 이메일_중복_변경_실패_테스트() {
        // given
        UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
        String email1 = generateUniqueEmail();
        String email2 = generateUniqueEmail();
        
        User user1 = userAppService.register(email1, "사용자1", "password123");
        User user2 = userAppService.register(email2, "사용자2", "password123");

        // when & then
        try {
            userAppService.changeEmail(user2.getId(), email1); // user1의 이메일로 변경 시도
            fail("이메일 중복으로 인한 예외가 발생해야 함");
        } catch (DomainException e) {
            assertTrue(e.getMessage().contains("이미 사용중인 이메일"));
        }
    }

    @Test
    public void 비밀번호_변경_성공_테스트() {
        // given
        UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
        String email = generateUniqueEmail();
        String currentPassword = "currentPassword123";
        String newPassword = "newPassword456";
        
        User user = userAppService.register(email, "사용자", currentPassword);

        // when
        User updatedUser = userAppService.changePassword(user.getId(), currentPassword, newPassword);

        // then
        // 새 비밀번호가 매치되는지 확인
        assertTrue("새 비밀번호가 매치되어야 함", 
                  passwordEncoder.matches(newPassword, updatedUser.getPassword()));
        
        // 이전 비밀번호는 매치되지 않아야 함
        assertFalse("이전 비밀번호는 매치되지 않아야 함", 
                   passwordEncoder.matches(currentPassword, updatedUser.getPassword()));
    }

    @Test
    public void 비밀번호_변경_현재_비밀번호_틀림_실패_테스트() {
        // given
        UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
        String email = generateUniqueEmail();
        String currentPassword = "correctPassword123";
        String wrongPassword = "wrongPassword456";
        String newPassword = "newPassword789";
        
        User user = userAppService.register(email, "사용자", currentPassword);

        // when & then
        try {
            userAppService.changePassword(user.getId(), wrongPassword, newPassword);
            fail("잘못된 현재 비밀번호로 인한 예외가 발생해야 함");
        } catch (DomainException e) {
            assertTrue(e.getMessage().contains("현재 비밀번호가 일치하지 않습니다"));
        }
    }

    @Test
    public void 이름_변경_테스트() {
        // given
        UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
        String email = generateUniqueEmail();
        User user = userAppService.register(email, "원래이름", "password123");
        
        String newName = "새로운이름";

        // when
        User updatedUser = userAppService.changeName(user.getId(), newName);

        // then
        assertEquals(newName, updatedUser.getName());
        
        // DB에서 다시 조회해서 확인
        User reloadedUser = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(reloadedUser);
        assertEquals(newName, reloadedUser.getName());
    }

    @Test
    public void 사용자_조회_이메일로_테스트() {
        // given
        UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
        String email = generateUniqueEmail();
        User user = userAppService.register(email, "사용자", "password123");

        // when
        User foundUser = userAppService.findByEmail(email).orElse(null);

        // then
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
        assertEquals(email, foundUser.getEmail());
    }

    @Test
    public void 사용자_조회_ID로_테스트() {
        // given
        UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
        String email = generateUniqueEmail();
        User user = userAppService.register(email, "사용자", "password123");

        // when
        User foundUser = userAppService.findById(user.getId()).orElse(null);

        // then
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
        assertEquals(email, foundUser.getEmail());
    }

    @Test
    public void 회원_탈퇴_테스트() {
        // given
        UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
        String email = generateUniqueEmail();
        String password = "password123";
        User user = userAppService.register(email, "사용자", password);

        // 사용자가 존재하는지 확인
        assertTrue(userRepository.findById(user.getId()).isPresent());

        // when
        userAppService.withdrawUser(user.getId(), password);

        // then
        assertFalse("회원 탈퇴 후 사용자가 존재하지 않아야 함", 
                   userRepository.findById(user.getId()).isPresent());
    }

    @Test
    public void 회원_탈퇴_비밀번호_틀림_실패_테스트() {
        // given
        UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
        String email = generateUniqueEmail();
        String correctPassword = "correctPassword123";
        String wrongPassword = "wrongPassword456";
        User user = userAppService.register(email, "사용자", correctPassword);

        // when & then
        try {
            userAppService.withdrawUser(user.getId(), wrongPassword);
            fail("잘못된 비밀번호로 인한 예외가 발생해야 함");
        } catch (DomainException e) {
            assertTrue(e.getMessage().contains("비밀번호가 일치하지 않습니다"));
        }

        // 사용자가 여전히 존재하는지 확인
        assertTrue("회원 탈퇴 실패 후 사용자가 여전히 존재해야 함", 
                  userRepository.findById(user.getId()).isPresent());
    }

    @Test
    public void 존재하지_않는_사용자_조회_테스트() {
        // given
        UserAppService userAppService = new UserAppService(userRepository, passwordEncoder);
        String nonExistentEmail = "nonexistent@example.com";

        // when
        boolean found = userAppService.findByEmail(nonExistentEmail).isPresent();

        // then
        assertFalse("존재하지 않는 사용자는 조회되지 않아야 함", found);
    }
}