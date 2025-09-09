package com.eggmoney.payv.domain.model.repository;

import java.util.List;
import java.util.Optional;

import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.domain.model.vo.UserRole;

/**
 * User 도메인 리포지토리 인터페이스
 * 
 * @author 정의탁, 강기범
 */
public interface UserRepository {
    
    // Create
    void save(User user);
    
    // Read
    Optional<User> findById(UserId id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    List<User> findByRole(UserRole role);
    
    // Update - 도메인 엔티티의 변경 감지를 통해 save()로 처리
    
    // Delete
    void deleteById(UserId id);
    
    // 비즈니스 쿼리
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, UserId excludeId);
    
    // 페이징 및 검색 (필요시)
    List<User> findByNameContaining(String name, int limit, int offset);
    long countByRole(UserRole role);
}