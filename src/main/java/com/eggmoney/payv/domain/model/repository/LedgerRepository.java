package com.eggmoney.payv.domain.model.repository;

import java.util.List;
import java.util.Optional;

import com.eggmoney.payv.domain.model.entity.Ledger;
import com.eggmoney.payv.domain.model.vo.LedgerId;
import com.eggmoney.payv.domain.model.vo.UserId;

public interface LedgerRepository {
	Optional<Ledger> findById(LedgerId id);
    List<Ledger> findListByOwner(UserId ownerId);
    
    // 소유자 기준 가계부 이름 중복 체크.
    boolean existsByOwnerAndName(UserId ownerId, String name);
    
    void save(Ledger ledger);
}
