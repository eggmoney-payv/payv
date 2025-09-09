package com.eggmoney.payv.infrastructure.mybatis.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.eggmoney.payv.domain.model.entity.Transaction;
import com.eggmoney.payv.domain.model.entity.TransactionType;
import com.eggmoney.payv.domain.model.repository.TransactionRepository;
import com.eggmoney.payv.domain.model.vo.AccountId;
import com.eggmoney.payv.domain.model.vo.CategoryId;
import com.eggmoney.payv.domain.model.vo.LedgerId;
import com.eggmoney.payv.domain.model.vo.Money;
import com.eggmoney.payv.domain.model.vo.TransactionId;
import com.eggmoney.payv.infrastructure.mybatis.mapper.TransactionMapper;
import com.eggmoney.payv.infrastructure.mybatis.record.TransactionRecord;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MyBatisTransactionRepository  implements TransactionRepository {

	private final TransactionMapper mapper;
	
	@Override
    public Optional<Transaction> findById(TransactionId id) {
        TransactionRecord transactionRecord = mapper.selectById(id.value());
        return Optional.ofNullable(transactionRecord).map(this::toDomain);
    }

    @Override
    public void save(Transaction Transaction) {
        // upsert 스타일: 존재하면 update, 없으면 insert
        TransactionRecord existing = mapper.selectById(Transaction.getId().value());
        if (existing == null) {
            mapper.insert(toRecord(Transaction));
        } else {
            mapper.update(toRecord(Transaction));
        }
    }

    @Override
    public void delete(TransactionId id) {
        mapper.delete(id.value());
    }

    @Override
    public List<Transaction> findByLedgerAndDateRange(LedgerId ledgerId, 
    		LocalDate from, LocalDate to, int limit, int offset) {
    	
        return mapper.selectByLedgerAndDateRange(ledgerId.value(), from, to, offset, limit)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    // ---- 변환부 ----
    private Transaction toDomain(TransactionRecord record) {
        return Transaction.reconstruct(
            TransactionId.of(record.getTransactionId()),
            LedgerId.of(record.getLedgerId()),
            AccountId.of(record.getAccountId()),
            TransactionType.valueOf(record.getType()),
            record.getDate(),
            Money.of(record.getAmount()),                 // KRW scale=0
            CategoryId.of(record.getCategoryId()),
            record.getMemo(),
            "Y".equals(record.getPosted()),
            record.getPostedAt(),
            record.getCreatedAt()
        );
    }

	private TransactionRecord toRecord(Transaction transaction) {
		return TransactionRecord.builder()
				.transactionId(transaction.getId().value())
				.ledgerId(transaction.getLedgerId().value())
				.accountId(transaction.getAccountId().value())
				.date(transaction.getDate())
				.type(transaction.getType().name())
				.amount(transaction.getAmount().toBigDecimal())
				.categoryId(transaction.getCategoryId().value())
				.memo(transaction.getMemo())
				.posted(transaction.isPosted() ? "Y" : "N")
				.postedAt(transaction.getPostedAt())
				.createdAt(transaction.getCreatedAt())
				.build();
	}
}
