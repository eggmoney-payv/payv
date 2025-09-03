package com.eggmoney.payv.infrastructure.mybatis.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.repository.UserRepository;
import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.infrastructure.mybatis.mapper.UserMapper;
import com.eggmoney.payv.infrastructure.mybatis.record.UserRecord;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MyBatisUserRepository implements UserRepository {

	private final UserMapper mapper;

    @Override
    public Optional<User> findById(UserId id) {
        UserRecord userRecord = mapper.selectById(id.value());
        return Optional.ofNullable(userRecord).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        UserRecord userRecord = mapper.selectByEmail(email);
        return Optional.ofNullable(userRecord).map(this::toDomain);
    }

    @Override
    public void save(User user) {
        // 존재하면 UPDATE, 없으면 INSERT
        UserRecord existing = mapper.selectById(user.getId().value());
        if (existing == null) {
            mapper.insert(toRecord(user));
        } else {
            mapper.update(toRecord(user));
        }
    }

    private User toDomain(UserRecord r) {
    	return User.builder()
    			.id(UserId.of(r.getUserId()))
    			.email(r.getEmail())
    			.name(r.getName())
    			.createdAt(r.getCreatedAt())
    			.build();
    }

    private UserRecord toRecord(User user) {
        return UserRecord.builder()
        		.userId(user.getId().value())
        		.email(user.getEmail())
        		.name(user.getName())
        		.createdAt(user.getCreatedAt())
        		.build();
    }
}
