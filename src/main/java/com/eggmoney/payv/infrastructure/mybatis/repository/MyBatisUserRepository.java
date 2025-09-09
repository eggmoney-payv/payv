package com.eggmoney.payv.infrastructure.mybatis.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.repository.UserRepository;
import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.domain.model.vo.UserRole;
import com.eggmoney.payv.infrastructure.mybatis.mapper.UserMapper;
import com.eggmoney.payv.infrastructure.mybatis.record.UserRecord;

import lombok.RequiredArgsConstructor;

/**
 * UserRepository MyBatis 구현체
 * 
 * @author 정의탁, 강기범
 */
@Repository
@RequiredArgsConstructor
public class MyBatisUserRepository implements UserRepository {

	private final UserMapper userMapper;

	@Override
	public void save(User user) {
		UserRecord existing = userMapper.selectUserById(user.getId().value());
		if (existing == null) {
			// 새 사용자 삽입
			userMapper.insertUser(toRecord(user));
		} else {
			// 기존 사용자 업데이트
			userMapper.updateUser(toRecord(user));
		}
	}

	@Override
	public Optional<User> findById(UserId id) {
		UserRecord record = userMapper.selectUserById(id.value());
		return Optional.ofNullable(record).map(this::toDomain);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		UserRecord record = userMapper.selectUserByEmail(email);
		return Optional.ofNullable(record).map(this::toDomain);
	}

	@Override
	public List<User> findAll() {
		return userMapper.selectUserList().stream().map(this::toDomain).collect(Collectors.toList());
	}

	@Override
	public List<User> findByRole(UserRole role) {
		// DB에 role 컬럼이 없으므로 모든 사용자를 조회하고 메모리에서 필터링
		return userMapper.selectUserList().stream()
				.map(this::toDomain)
				.filter(user -> user.getRole().equals(role))
				.collect(Collectors.toList());
	}

	@Override
	public void deleteById(UserId id) {
		userMapper.deleteUserById(id.value());
	}

	@Override
	public boolean existsByEmail(String email) {
		return userMapper.existsByEmail(email) > 0;
	}

	@Override
	public boolean existsByEmailAndIdNot(String email, UserId excludeId) {
		return userMapper.existsByEmailAndUserIdNot(email, excludeId.value()) > 0;
	}

	@Override
	public List<User> findByNameContaining(String name, int limit, int offset) {
		return userMapper.selectUserListByNameContaining(name, limit, offset).stream().map(this::toDomain)
				.collect(Collectors.toList());
	}

	@Override
	public long countByRole(UserRole role) {
		// DB에 role 컬럼이 없으므로 모든 사용자를 조회하고 메모리에서 카운트
		return userMapper.selectUserList().stream()
				.map(this::toDomain)
				.filter(user -> user.getRole().equals(role))
				.count();
	}

	/**
	 * Record를 Domain으로 변환 (role은 기본값 USER로 설정)
	 */
	private User toDomain(UserRecord record) {
		return User.builder()
				.id(UserId.of(record.getUserId()))
				.email(record.getEmail())
				.name(record.getName())
				.password(record.getPassword())
				.role(UserRole.USER)  // DB에 role 컬럼이 없으므로 기본값으로 USER 설정
				.createdAt(record.getCreateAt())  // createAt 사용
				.build();
	}

	/**
	 * Domain을 Record로 변환 (role 정보는 저장하지 않음)
	 */
	private UserRecord toRecord(User user) {
		return UserRecord.builder()
				.userId(user.getId().value())
				.email(user.getEmail())
				.name(user.getName())
				.password(user.getPassword())
				.createAt(user.getCreatedAt())  // createAt 사용
				.build();
	}
}