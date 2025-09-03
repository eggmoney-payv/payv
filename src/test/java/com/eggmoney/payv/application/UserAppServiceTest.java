package com.eggmoney.payv.application;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.eggmoney.payv.application.service.UserAppService;
import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.repository.UserRepository;
import com.eggmoney.payv.domain.shared.error.DomainException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/root-context.xml")
@Transactional
public class UserAppServiceTest {

	@Resource 
	UserRepository userRepository;

    @Test
    public void userAppServiceTest() {
    	// 도메인 서비스 조립
        UserAppService userAppService = new UserAppService(userRepository);

        // 1) 사용자 등록
        String email1 = "user+" + System.nanoTime() + "@example.com";
        User user1 = userAppService.register(email1, "Alice");
        assertNotNull(user1.getId());

        // 2) 조회 (by id / by email)
        assertTrue(userRepository.findById(user1.getId()).isPresent());
        assertTrue(userRepository.findByEmail(email1).isPresent());

        // 3) 중복 이메일 등록 실패
        try {
        	userAppService.register(email1, "Alice Dup");
            fail("Expected DomainException for duplicate email");
        } catch (DomainException expected) { /* ok */ }

        // 5) 이메일 변경 (정상)
        String emailNew = "alice.new+" + System.nanoTime() + "@example.com";
        userAppService.changeEmail(user1.getId(), emailNew);
        User reloaded2 = userRepository.findById(user1.getId()).orElseThrow(AssertionError::new);
        assertEquals(emailNew, reloaded2.getEmail());

        // 6) 다른 사용자와 이메일 충돌 검증
        String email2 = "bob+" + System.nanoTime() + "@example.com";
        User user2 = userAppService.register(email2, "Bob");
        try {
        	userAppService.changeEmail(user2.getId(), emailNew); // user1이 이미 사용 중인 이메일
            fail("Expected DomainException for email conflict");
        } catch (DomainException expected) { /* ok */ }

        // u2의 이메일은 여전히 e2 이어야 함
        User reloadedU2 = userRepository.findById(user2.getId()).orElseThrow(AssertionError::new);
        assertEquals(email2, reloadedU2.getEmail());
    }
}
