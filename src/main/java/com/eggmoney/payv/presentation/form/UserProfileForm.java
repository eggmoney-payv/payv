package com.eggmoney.payv.presentation.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 프로필 수정 폼 데이터를 담는 클래스
 * 
 * @author 정의탁, 강기범
 */
@Getter
@Setter
public class UserProfileForm {

	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@Size(max = 50, message = "이메일은 50자를 초과할 수 없습니다.")
	private String email;

	@NotBlank(message = "이름은 필수입니다.")
	@Size(max = 20, message = "이름은 20자를 초과할 수 없습니다.")
	private String name;
}