package com.eggmoney.payv.web.auth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.eggmoney.payv.application.service.UserAppService;
import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.domain.model.vo.UserRole;
import com.eggmoney.payv.domain.shared.error.DomainException;
import com.eggmoney.payv.domain.shared.util.EntityIdentifier;

/**
 * AuthController 단위 테스트
 * 
 * @author 팀원명
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthControllerTest {

	@Mock
	private UserAppService userAppService;

	private MockMvc mockMvc;
	private AuthController authController;

	@Before
	public void setUp() {
		authController = new AuthController(userAppService);
		mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
	}

	// ========== 회원가입 페이지 테스트 ==========

	@Test
	public void 회원가입_페이지_요청_테스트() throws Exception {
		// when & then
		mockMvc.perform(get("/auth/signup")).andExpect(status().isOk()).andExpect(view().name("auth/signup"))
				.andExpect(model().attributeExists("signupForm"));
	}

	@Test
	public void 회원가입_성공_테스트() throws Exception {
		// given
		User mockUser = User.builder().id(UserId.of(EntityIdentifier.generateUuid())).email("test@example.com")
				.name("테스트사용자").password("encoded_password").role(UserRole.USER).build();

		when(userAppService.register("test@example.com", "테스트사용자", "password123")).thenReturn(mockUser);

		// when & then
		mockMvc.perform(post("/auth/signup").param("email", "test@example.com").param("name", "테스트사용자")
				.param("password", "password123").param("confirmPassword", "password123"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/auth/login?signup=success"))
				.andExpect(flash().attributeExists("message"));

		verify(userAppService, times(1)).register("test@example.com", "테스트사용자", "password123");
	}

	@Test
	public void 회원가입_검증_실패_테스트() throws Exception {
		// when & then
		mockMvc.perform(post("/auth/signup").param("email", "invalid-email") // 잘못된 이메일
				.param("name", "") // 빈 이름
				.param("password", "123") // 짧은 비밀번호
				.param("confirmPassword", "456")) // 비밀번호 불일치
				.andExpect(status().isOk()).andExpect(view().name("auth/signup")).andExpect(model().hasErrors());

		verify(userAppService, never()).register(anyString(), anyString(), anyString());
	}

	@Test
	public void 회원가입_비밀번호_불일치_테스트() throws Exception {
		// when & then
		mockMvc.perform(post("/auth/signup").param("email", "test@example.com").param("name", "사용자")
				.param("password", "password123").param("confirmPassword", "different123")).andExpect(status().isOk())
				.andExpect(view().name("auth/signup"))
				.andExpect(model().attributeHasFieldErrors("signupForm", "confirmPassword"));

		verify(userAppService, never()).register(anyString(), anyString(), anyString());
	}

	@Test
	public void 회원가입_이메일_중복_테스트() throws Exception {
		// given
		when(userAppService.register("duplicate@example.com", "사용자", "password123"))
				.thenThrow(new DomainException("이미 사용중인 이메일입니다"));

		// when & then
		mockMvc.perform(post("/auth/signup").param("email", "duplicate@example.com").param("name", "사용자")
				.param("password", "password123").param("confirmPassword", "password123")).andExpect(status().isOk())
				.andExpect(view().name("auth/signup"))
				.andExpect(model().attributeHasFieldErrors("signupForm", "email"));
	}

	@Test
	public void 회원가입_입력_검증_오류_테스트() throws Exception {
		// given
		when(userAppService.register("invalid@example.com", "사용자", "weak"))
				.thenThrow(new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다"));

		// when & then
		mockMvc.perform(post("/auth/signup").param("email", "invalid@example.com").param("name", "사용자")
				.param("password", "weak").param("confirmPassword", "weak")).andExpect(status().isOk())
				.andExpect(view().name("auth/signup")).andExpect(model().hasErrors());
	}

	// ========== 로그인 페이지 테스트 ==========

	@Test
	public void 로그인_페이지_요청_테스트() throws Exception {
		// when & then
		mockMvc.perform(get("/auth/login")).andExpect(status().isOk()).andExpect(view().name("auth/login"));
	}

	@Test
	public void 로그인_페이지_성공_메시지_테스트() throws Exception {
		// when & then
		mockMvc.perform(get("/auth/login").param("signup", "success")).andExpect(status().isOk())
				.andExpect(view().name("auth/login")).andExpect(model().attributeExists("message"));
	}

	@Test
	public void 로그인_페이지_에러_메시지_테스트() throws Exception {
		// when & then
		mockMvc.perform(get("/auth/login").param("error", "true")).andExpect(status().isOk())
				.andExpect(view().name("auth/login")).andExpect(model().attributeExists("error"));
	}

	@Test
	public void 로그인_페이지_로그아웃_메시지_테스트() throws Exception {
		// when & then
		mockMvc.perform(get("/auth/login").param("logout", "true")).andExpect(status().isOk())
				.andExpect(view().name("auth/login")).andExpect(model().attributeExists("message"));
	}
}