package com.eggmoney.payv.domain.model.repository;

import java.util.List;
import java.util.Optional;

import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.domain.model.vo.UserRole;

/**
 * 사용자 리포지토리 인터페이스 Spring Security 호환 메서드 포함
 * 
 * @author 정의탁, 강기범
 */
public interface UserRepository {

	// 기본 CRUD 메서드들
	void save(User user);

	Optional<User> findById(UserId id); // Optional 반환으로 수정

	Optional<User> findByEmail(String email); // Spring Security에서 사용

	List<User> findAll();

	List<User> findByRole(UserRole role);

	void deleteById(UserId id);

	// 존재 여부 확인 메서드들
	boolean existsByEmail(String email);

	boolean existsByEmailAndIdNot(String email, UserId excludeId);

	// 검색 및 페이징 메서드들
	List<User> findByNameContaining(String name, int limit, int offset);

	long countByRole(UserRole role);

	// Spring Security 호환성을 위한 추가 메서드
	default User findByIdOrThrow(UserId id) {
		return findById(id).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + id.value()));
	}
}