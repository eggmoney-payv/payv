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
		return userMapper.selectUserListByRole(role.name()).stream().map(this::toDomain).collect(Collectors.toList());
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
		return userMapper.countUsersByRole(role.name());
	}

	/**
	 * Record를 Domain으로 변환
	 */
	private User toDomain(UserRecord record) {
		return User.builder().id(UserId.of(record.getUserId())).email(record.getEmail()).name(record.getName())
				.password(record.getPassword()).role(UserRole.valueOf(record.getRole()))
				.createdAt(record.getCreatedAt()).build();
	}

	/**
	 * Domain을 Record로 변환
	 */
	private UserRecord toRecord(User user) {
		return UserRecord.builder().userId(user.getId().value()).email(user.getEmail()).name(user.getName())
				.password(user.getPassword()).role(user.getRole().name()).createdAt(user.getCreatedAt()).build();
	}
}