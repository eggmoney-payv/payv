package com.eggmoney.payv.infrastructure.mybatis.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.eggmoney.payv.infrastructure.mybatis.record.TransactionRecord;

@Mapper
public interface TransactionMapper {

	TransactionRecord selectById(@Param("transactionId") String transactionId);

    int insert(TransactionRecord rec);
    int update(TransactionRecord rec);
    int delete(@Param("transactionId") String transactionId);

    List<TransactionRecord> selectByLedgerAndDateRange(
            @Param("ledgerId") String ledgerId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

}
