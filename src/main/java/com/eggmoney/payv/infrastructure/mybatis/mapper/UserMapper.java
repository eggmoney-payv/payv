package com.eggmoney.payv.infrastructure.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.eggmoney.payv.infrastructure.mybatis.record.UserRecord;

/**
 * User MyBatis Mapper 인터페이스
 * 
 * @author 정의탁, 강기범
 */
@Mapper
public interface UserMapper {
    
    // Create
    int insertUser(UserRecord user);
    
    // Read
    UserRecord selectUserById(@Param("userId") String userId);
    UserRecord selectUserByEmail(@Param("email") String email);
    List<UserRecord> selectUserList();
    List<UserRecord> selectUserListByRole(@Param("role") String role);
    
    // Update
    int updateUser(UserRecord user);
    int updateUserEmail(@Param("userId") String userId, @Param("email") String email);
    int updateUserPassword(@Param("userId") String userId, @Param("password") String password);
    int updateUserName(@Param("userId") String userId, @Param("name") String name);
    int updateUserRole(@Param("userId") String userId, @Param("role") String role);
    
    // Delete
    int deleteUserById(@Param("userId") String userId);
    
    // 비즈니스 쿼리
    int existsByEmail(@Param("email") String email);
    int existsByEmailAndUserIdNot(@Param("email") String email, @Param("excludeUserId") String excludeUserId);
    
    // 페이징 및 검색
    List<UserRecord> selectUserListByNameContaining(@Param("name") String name, 
                                                   @Param("limit") int limit, 
                                                   @Param("offset") int offset);
    long countUsersByRole(@Param("role") String role);
}