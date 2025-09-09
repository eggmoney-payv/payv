package com.eggmoney.payv.application.service;

import org.springframework.stereotype.Service;

import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.repository.UserRepository;
import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.domain.shared.error.DomainException;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 애플리케이션 서비스
 * @author 정의탁
 */
@Service
@RequiredArgsConstructor
public class UserAppService {

	private final UserRepository userRepository;
	
	// 이메일 유일성 보장 하에 새 사용자 등록.
    public User register(String email, String password, String name){
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DomainException("이미 사용중인 이메일 입니다. (" + email + ")");
        }
        User user = User.create(email, password, name);
        userRepository.save(user);
        return user;
    }

    public User changeEmail(UserId userId, String newEmail){
    	// 이메일 유일성 보장.
        userRepository.findByEmail(newEmail).ifPresent(existing -> {
            if (!existing.getId().equals(userId)) {
            	throw new DomainException("이미 사용중인 이메일 입니다. (" + newEmail + ")");
            }
        });
        
        User user = userRepository.findById(userId)
        		.orElseThrow(() -> new DomainException("사용자를 찾을 수 없습니다."));
        
        user.changeEmail(newEmail);
        userRepository.save(user);
        return user;
    }
}
