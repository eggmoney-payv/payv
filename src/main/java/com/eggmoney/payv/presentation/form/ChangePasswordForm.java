package com.eggmoney.payv.presentation.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * 비밀번호 변경 폼 데이터를 담는 클래스
 * 
 * @author 정의탁, 강기범
 */
@Getter
@Setter
public class ChangePasswordForm {

	@NotBlank(message = "현재 비밀번호는 필수입니다.")
	private String currentPassword;

	@NotBlank(message = "새 비밀번호는 필수입니다.")
	@Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다.")
	private String newPassword;

	@NotBlank(message = "새 비밀번호 확인은 필수입니다.")
	private String confirmPassword;
}