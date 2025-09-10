package com.eggmoney.payv.web.auth.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * 회원가입 폼 데이터
 * 
 * @author 팀원명
 */
@Data
public class SignupForm {

	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다.")
	private String email;

	@NotBlank(message = "이름은 필수입니다.")
	@Size(max = 50, message = "이름은 50자를 초과할 수 없습니다.")
	private String name;

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다.")
	private String password;

	@NotBlank(message = "비밀번호 확인은 필수입니다.")
	private String confirmPassword;
}