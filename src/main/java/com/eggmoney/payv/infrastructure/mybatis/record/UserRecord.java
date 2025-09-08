package com.eggmoney.payv.infrastructure.mybatis.record;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRecord {

	private String userId;
    private String email;
    private String name;
    private String password;
    private String role;          // UserRole enum을 String으로 저장
    private LocalDateTime createdAt;    
}
