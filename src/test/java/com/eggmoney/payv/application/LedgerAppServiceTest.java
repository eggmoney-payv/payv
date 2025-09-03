package com.eggmoney.payv.application;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.eggmoney.payv.application.service.LedgerAppService;
import com.eggmoney.payv.application.service.UserAppService;
import com.eggmoney.payv.domain.model.entity.Ledger;
import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.repository.LedgerRepository;
import com.eggmoney.payv.domain.model.repository.UserRepository;
import com.eggmoney.payv.domain.shared.error.DomainException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/root-context.xml")
@Transactional
public class LedgerAppServiceTest {

	@Resource 
	LedgerRepository ledgerRepository;
	
	@Resource
	UserRepository userRepository;
	
    @Test
    public void ledgerAppServiceTest() {
    	
    	// --- 준비: 도메인 서비스 조립 (순수 도메인) ---
        UserAppService userService   = new UserAppService(userRepository);
        LedgerAppService ledgerService = new LedgerAppService(ledgerRepository, userRepository);

        // --- 사용자 등록 ---
        User owner = userService.register("owner+" + System.nanoTime() + "@example.com", "Owner");
        assertNotNull(owner.getId());

        // --- 개인 가계부 개설 (User가 Creator, 서비스가 검증/저장) ---
        Ledger ledger = ledgerService.createLedger(owner, "My Ledger");
        assertNotNull(ledger.getId());
        assertTrue(ledgerRepository.findById(ledger.getId()).isPresent());

        // --- 이름 변경(권한 OK) ---
        ledgerService.rename(ledger.getId(), "My Ledger 2025", owner.getId());
        Ledger reloaded = ledgerRepository.findById(ledger.getId()).orElseThrow(AssertionError::new);
        assertEquals("My Ledger 2025", reloaded.getName());

        // --- 동일 소유자+이름 중복 금지 ---
        try {
        	ledgerService.createLedger(owner, "My Ledger 2025");
            fail("Expected DomainException for duplicate ledger name");
        } catch (DomainException expected) {
            // ok
        }

        // --- 권한 없는 사용자의 이름 변경 시도 ---
        User stranger = userService.register("stranger+" + System.nanoTime() + "@example.com", "Stranger");
        try {
        	ledgerService.rename(ledger.getId(), "Hacked!", stranger.getId());
            fail("Expected DomainException for permission check");
        } catch (DomainException expected) {
            // ok
        }
    }
}
