package com.eggmoney.payv.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eggmoney.payv.domain.model.entity.Account;
import com.eggmoney.payv.domain.model.entity.Ledger;
import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.repository.LedgerRepository;
import com.eggmoney.payv.domain.model.repository.UserRepository;
import com.eggmoney.payv.domain.model.vo.LedgerId;
import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.domain.shared.error.DomainException;

import lombok.RequiredArgsConstructor;

/**
 * 가계부 애플리케이션 서비스
 * @author 정의탁
 */
@Service
@RequiredArgsConstructor
public class LedgerAppService {

	private final LedgerRepository ledgerRepository;
    private final UserRepository userRepository;
    
    // 가계부 생성.
    public Ledger createLedger(UserId ownerId, String name){
        // 소유자 존재 유무 확인.
        User owner = userRepository.findById(ownerId)
        		.orElseThrow(() -> new DomainException("owner not found"));

        // 가계부 이름 중복 확인.
        if (ledgerRepository.existsByOwnerAndName(ownerId, name)) {
            throw new DomainException("동일한 이름의 가계부가 존재합니다.");
        }

        Ledger ledger = owner.createLedger(name);
        ledgerRepository.save(ledger);
        
        return ledger;
    }

    // 가계부 이름 수정.
    public Ledger rename(LedgerId ledgerId, String newName, UserId userId){

    	// 가계부 존재 유무 확인.
    	Ledger ledger = ledgerRepository.findById(ledgerId)
                .orElseThrow(() -> new DomainException("ledger not found"));
        
        if (!ledger.getOwnerId().equals(userId)) {
        	throw new DomainException("가계부 소유자 권한 필요.");
        }

        // 가계부 이름 중복 확인.
        if (ledgerRepository.existsByOwnerAndName(userId, newName)) {
            throw new DomainException("동일한 이름의 가계부가 존재합니다.");
        }
        
        ledger.rename(newName);
        ledgerRepository.save(ledger);
        
        return ledger;
    }
    
    // 사용자 소유 가계부 목록 조회.
    @Transactional(readOnly = true)
    public List<Ledger> listByOwner(UserId onwerId) {
        return ledgerRepository.findListByOwner(onwerId);
    }
}
