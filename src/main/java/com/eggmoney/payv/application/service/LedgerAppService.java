package com.eggmoney.payv.application.service;

import org.springframework.stereotype.Service;

import com.eggmoney.payv.domain.model.entity.Ledger;
import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.repository.LedgerRepository;
import com.eggmoney.payv.domain.model.repository.UserRepository;
import com.eggmoney.payv.domain.model.vo.LedgerId;
import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.domain.shared.error.DomainException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LedgerAppService {

	private final LedgerRepository ledgerRepository;
    private final UserRepository userRepository;
    
    // 가계부 생성.
    public Ledger createLedger(User owner, String name){
        // 소유자 존재 유무 확인.
        userRepository.findById(owner.getId())
            .orElseThrow(() -> new DomainException("owner not found"));

        // 가계부 이름 중복 확인.
        if (ledgerRepository.existsByOwnerAndName(owner.getId(), name)) {
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
}
