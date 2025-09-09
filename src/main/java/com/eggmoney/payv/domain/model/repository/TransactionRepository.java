package com.eggmoney.payv.domain.model.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.eggmoney.payv.domain.model.entity.Transaction;
import com.eggmoney.payv.domain.model.vo.LedgerId;
import com.eggmoney.payv.domain.model.vo.TransactionId;

/**
 * 거래 내역 레포지토리
 * @author 정의탁
 */
public interface TransactionRepository {

	Optional<Transaction> findById(TransactionId id);

    // UPSERT = 새로 생성된 거래는 insert, 기존이면 update
    void save(Transaction tx);

    // 게시되지 않은 거래만 삭제 가능(규칙은 서비스에서 검사)
    void delete(TransactionId id);

    // 조회 유틸: 가계부/기간 기준 페이징
    List<Transaction> findByLedgerAndDateRange(LedgerId ledgerId, LocalDate from, LocalDate to,
                                               int limit, int offset);
    
    // 자산별, 카테고리별 등 쿼리 메서드 추가.
}
