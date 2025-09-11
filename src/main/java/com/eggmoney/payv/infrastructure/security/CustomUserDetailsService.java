package com.eggmoney.payv.infrastructure.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Spring Security UserDetailsService 구현체 DB에 ROLE 컬럼이 없으므로 모든 사용자에게 ROLE_USER
 * 권한 부여
 * 
 * @author 정의탁, 강기범
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

		List<GrantedAuthority> authorities = new ArrayList<>();
		// DB에 role이 없으므로 모든 사용자에게 ROLE_USER 권한 부여
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		// 특정 이메일에 대해서는 관리자 권한 부여 (운영 시에는 별도 테이블로 관리 권장)
		if ("admin@payv.com".equals(email)) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}

		return new org.springframework.security.core.userdetails.User(user.getEmail(), // username으로 email 사용
				user.getPassword(), authorities);
	}
}