package com.eggmoney.payv.domain.model.repository;

import java.util.Optional;

import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.vo.UserId;

public interface UserRepository {
	Optional<User> findById(UserId id);
    Optional<User> findByEmail(String email);
    void save(User user);
}
