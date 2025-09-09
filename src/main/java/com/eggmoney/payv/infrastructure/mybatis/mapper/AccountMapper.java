package com.eggmoney.payv.infrastructure.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.eggmoney.payv.infrastructure.mybatis.record.AccountRecord;

@Mapper
public interface AccountMapper {

	AccountRecord selectById(@Param("id") String id);
	List<AccountRecord> selectListByLedger(@Param("ledgerId") String ledgerId);
	
    int insert(AccountRecord rec);   // 새로 생성된 자산
    int update(AccountRecord rec);   // 기존 자산
}
