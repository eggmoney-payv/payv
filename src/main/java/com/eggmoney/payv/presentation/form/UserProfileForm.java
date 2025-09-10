package com.eggmoney.payv.presentation.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * 사용자 프로필 수정 폼 데이터
 * 
 * @author 강기범
 */
@Data
public class UserProfileForm {

	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다.")
	private String email;

	@NotBlank(message = "이름은 필수입니다.")
	@Size(max = 50, message = "이름은 50자를 초과할 수 없습니다.")
	private String name;
}