package com.eggmoney.payv.infrastructure.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.eggmoney.payv.infrastructure.mybatis.record.UserRecord;

@Mapper
public interface UserMapper {

	UserRecord selectById(@Param("userId") String userId);
    UserRecord selectByEmail(@Param("email") String email);
    int insert(UserRecord rec);
    int update(UserRecord rec);
}
