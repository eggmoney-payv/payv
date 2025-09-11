package com.eggmoney.payv.infrastructure.mybatis.record;

import java.sql.Timestamp; // ⭐ LocalDateTime 대신 Timestamp

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRecord {

	private String userId;
	private String email;
	private String name;
	private String password;
	private Timestamp createAt; // ⭐ LocalDateTime → Timestamp로 변경
}