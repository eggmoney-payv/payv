package com.eggmoney.payv.presentation;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

import java.security.Principal;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.eggmoney.payv.application.service.UserAppService;
import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.vo.UserId;
import com.eggmoney.payv.domain.model.vo.UserRole;
import com.eggmoney.payv.domain.shared.error.DomainException;
import com.eggmoney.payv.domain.shared.util.EntityIdentifier;

/**
 * UserController 단위 테스트
 * 
 * @author 팀원명
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

	@Mock
	private UserAppService userAppService;

	@Mock
	private Principal principal;

	private MockMvc mockMvc;
	private UserController userController;
	private User mockUser;
	private UserId userId;

	@Before
	public void setUp() {
		userController = new UserController(userAppService);
		
		// EL 의존성 없이 작동하는 Validator 설정
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setMessageInterpolator(new ParameterMessageInterpolator());
		validator.afterPropertiesSet();
		
		// MockMvc에 validator 추가
		mockMvc = MockMvcBuilders.standaloneSetup(userController)
				.setValidator(validator)
				.build();

		// 테스트용 사용자 생성
		userId = UserId.of(EntityIdentifier.generateUuid());
		mockUser = User.builder()
				.id(userId)
				.email("test@example.com")
				.name("테스트사용자")
				.password("encoded_password")
				.role(UserRole.USER)
				.build();

		// Principal Mock 설정
		when(principal.getName()).thenReturn(userId.value());
	}

	// ========== 회원가입 테스트 ==========

	@Test
	public void 회원가입_페이지_요청_테스트() throws Exception {
		// when & then
		mockMvc.perform(get("/signup"))
				.andExpect(status().isOk())
				.andExpect(view().name("auth/signup"))
				.andExpect(model().attributeExists("signupForm"));
	}

	@Test
	public void 회원가입_성공_테스트() throws Exception {
		// given
		when(userAppService.register("test@example.com", "테스트사용자", "password123"))
				.thenReturn(mockUser);

		// when & then
		mockMvc.perform(post("/signup")
				.param("email", "test@example.com")
				.param("name", "테스트사용자")
				.param("password", "password123")
				.param("confirmPassword", "password123"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?signup=success"))
				.andExpect(flash().attributeExists("message"));

		verify(userAppService, times(1)).register("test@example.com", "테스트사용자", "password123");
	}

	@Test
	public void 회원가입_검증_실패_테스트() throws Exception {
		// when & then
		mockMvc.perform(post("/signup")
				.param("email", "invalid-email") // 잘못된 이메일
				.param("name", "") // 빈 이름
				.param("password", "123") // 짧은 비밀번호
				.param("confirmPassword", "456")) // 비밀번호 불일치
				.andExpect(status().isOk())
				.andExpect(view().name("auth/signup"))
				.andExpect(model().hasErrors());

		verify(userAppService, never()).register(anyString(), anyString(), anyString());
	}

	@Test
	public void 회원가입_비밀번호_불일치_테스트() throws Exception {
		// when & then
		mockMvc.perform(post("/signup")
				.param("email", "test@example.com")
				.param("name", "사용자")
				.param("password", "password123")
				.param("confirmPassword", "different123"))
				.andExpect(status().isOk())
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
		mockMvc.perform(post("/signup")
				.param("email", "duplicate@example.com")
				.param("name", "사용자")
				.param("password", "password123")
				.param("confirmPassword", "password123"))
				.andExpect(status().isOk())
				.andExpect(view().name("auth/signup"))
				.andExpect(model().attributeHasFieldErrors("signupForm", "email"));
	}

	// ========== 로그인 페이지 테스트 ==========

	@Test
	public void 로그인_페이지_요청_테스트() throws Exception {
		// when & then
		mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("auth/login"));
	}

	@Test
	public void 로그인_페이지_회원가입_성공_메시지_테스트() throws Exception {
		// when & then
		mockMvc.perform(get("/login").param("signup", "success"))
				.andExpect(status().isOk())
				.andExpect(view().name("auth/login"))
				.andExpect(model().attributeExists("signupMessage"));
	}

	@Test
	public void 로그인_페이지_에러_메시지_테스트() throws Exception {
		// when & then
		mockMvc.perform(get("/login").param("error", "true"))
				.andExpect(status().isOk())
				.andExpect(view().name("auth/login"))
				.andExpect(model().attributeExists("errorMessage"));
	}

	@Test
	public void 로그인_페이지_로그아웃_메시지_테스트() throws Exception {
		// when & then
		mockMvc.perform(get("/login").param("logout", "true"))
				.andExpect(status().isOk())
				.andExpect(view().name("auth/login"))
				.andExpect(model().attributeExists("logoutMessage"));
	}

	// ========== 마이페이지 테스트 ==========

	@Test
	public void 마이페이지_요청_테스트() throws Exception {
		// given
		when(userAppService.findByIdOrThrow(userId)).thenReturn(mockUser);

		// when & then
		mockMvc.perform(get("/user/profile").principal(principal))
				.andExpect(status().isOk())
				.andExpect(view().name("common/layout"))
				.andExpect(model().attribute("pageTitle", "마이페이지"))
				.andExpect(model().attribute("contentPage", "/WEB-INF/views/user/profile.jsp"))
				.andExpect(model().attributeExists("user"))
				.andExpect(model().attributeExists("profileForm"))
				.andExpect(model().attributeExists("passwordForm"));

		verify(userAppService, times(1)).findByIdOrThrow(userId);
	}

	// ========== 개인정보 수정 테스트 ==========

	@Test
	public void 개인정보_수정_성공_테스트() throws Exception {
		// given
		when(userAppService.findByIdOrThrow(userId)).thenReturn(mockUser);
		when(userAppService.changeName(userId, "새이름")).thenReturn(mockUser);
		when(userAppService.changeEmail(userId, "new@example.com")).thenReturn(mockUser);

		// when & then
		mockMvc.perform(post("/user/profile").principal(principal)
				.param("email", "new@example.com")
				.param("name", "새이름"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/profile"))
				.andExpect(flash().attributeExists("message"));

		verify(userAppService, times(1)).changeName(userId, "새이름");
		verify(userAppService, times(1)).changeEmail(userId, "new@example.com");
	}

	@Test
	public void 개인정보_수정_검증_실패_테스트() throws Exception {
		// given
		when(userAppService.findByIdOrThrow(userId)).thenReturn(mockUser);

		// when
		MvcResult result = mockMvc.perform(post("/user/profile").principal(principal)
				.param("email", "invalid-email")
				.param("name", ""))
				.andExpect(status().isOk())
				.andExpect(view().name("common/layout"))
				.andReturn();

		// then
		BindingResult profileBr = (BindingResult) result.getModelAndView().getModelMap()
				.get("org.springframework.validation.BindingResult.profileForm");
		assertTrue("프로필 폼 검증 실패 시 BindingResult에 에러가 있어야 한다 → 테스트 성공", 
				profileBr != null && profileBr.hasErrors());

		verify(userAppService, never()).changeName(any(UserId.class), anyString());
		verify(userAppService, never()).changeEmail(any(UserId.class), anyString());
	}

	@Test
	public void 개인정보_수정_동일한_이메일_테스트() throws Exception {
		// given
		when(userAppService.findByIdOrThrow(userId)).thenReturn(mockUser);
		when(userAppService.changeName(userId, "새이름")).thenReturn(mockUser);
		// 동일한 이메일이므로 changeEmail 호출되지 않음

		// when & then
		mockMvc.perform(post("/user/profile").principal(principal)
				.param("email", "test@example.com") // 기존과 동일한 이메일
				.param("name", "새이름"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/profile"));

		verify(userAppService, times(1)).changeName(userId, "새이름");
		verify(userAppService, never()).changeEmail(any(UserId.class), anyString());
	}

	@Test
	public void 개인정보_수정_이메일_중복_테스트() throws Exception {
		// given
		when(userAppService.findByIdOrThrow(userId)).thenReturn(mockUser);
		when(userAppService.changeEmail(userId, "duplicate@example.com"))
				.thenThrow(new DomainException("이미 사용중인 이메일입니다"));

		// when
		MvcResult result = mockMvc.perform(post("/user/profile").principal(principal)
				.param("email", "duplicate@example.com")
				.param("name", "새이름"))
				.andExpect(status().isOk())
				.andExpect(view().name("common/layout"))
				.andReturn();

		// then
		BindingResult profileBr = (BindingResult) result.getModelAndView().getModelMap()
				.get("org.springframework.validation.BindingResult.profileForm");
		assertTrue("중복 이메일 입력 시 profileForm에 email 필드 에러가 있어야 한다 → 테스트 성공",
				profileBr != null && profileBr.hasFieldErrors("email"));
	}

	// ========== 비밀번호 변경 테스트 ==========

	@Test
	public void 비밀번호_변경_성공_테스트() throws Exception {
		// given
		when(userAppService.findByIdOrThrow(userId)).thenReturn(mockUser);
		when(userAppService.changePassword(userId, "currentPassword", "newPassword123")).thenReturn(mockUser);

		// when & then
		mockMvc.perform(post("/user/change-password").principal(principal)
				.param("currentPassword", "currentPassword")
				.param("newPassword", "newPassword123")
				.param("confirmPassword", "newPassword123"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/profile"))
				.andExpect(flash().attributeExists("message"));

		verify(userAppService, times(1)).changePassword(userId, "currentPassword", "newPassword123");
	}

	@Test
	public void 비밀번호_변경_불일치_테스트() throws Exception {
		// given
		when(userAppService.findByIdOrThrow(userId)).thenReturn(mockUser);

		// when
		MvcResult result = mockMvc.perform(post("/user/change-password").principal(principal)
				.param("currentPassword", "currentPassword")
				.param("newPassword", "newPassword123")
				.param("confirmPassword", "differentPassword"))
				.andExpect(status().isOk())
				.andExpect(view().name("common/layout"))
				.andReturn();

		// then
		BindingResult pwBr = (BindingResult) result.getModelAndView().getModelMap()
				.get("org.springframework.validation.BindingResult.passwordForm");
		assertTrue("비밀번호 확인 불일치 시 passwordForm.confirmPassword에 에러가 있어야 한다 → 테스트 성공",
				pwBr != null && pwBr.hasFieldErrors("confirmPassword"));

		verify(userAppService, never()).changePassword(any(UserId.class), anyString(), anyString());
	}

	@Test
	public void 비밀번호_변경_현재_비밀번호_틀림_테스트() throws Exception {
		// given
		when(userAppService.findByIdOrThrow(userId)).thenReturn(mockUser);
		when(userAppService.changePassword(userId, "wrongPassword", "newPassword123"))
				.thenThrow(new DomainException("현재 비밀번호가 일치하지 않습니다"));

		// when
		MvcResult result = mockMvc.perform(post("/user/change-password").principal(principal)
				.param("currentPassword", "wrongPassword")
				.param("newPassword", "newPassword123")
				.param("confirmPassword", "newPassword123"))
				.andExpect(status().isOk())
				.andExpect(view().name("common/layout"))
				.andReturn();

		// then
		BindingResult pwBr = (BindingResult) result.getModelAndView().getModelMap()
				.get("org.springframework.validation.BindingResult.passwordForm");
		assertTrue("현재 비밀번호 틀림 시 passwordForm.currentPassword에 에러가 있어야 한다 → 테스트 성공",
				pwBr != null && pwBr.hasFieldErrors("currentPassword"));
	}

	@Test
	public void 비밀번호_변경_검증_실패_테스트() throws Exception {
		// given
		when(userAppService.findByIdOrThrow(userId)).thenReturn(mockUser);

		// when
		MvcResult result = mockMvc.perform(post("/user/change-password").principal(principal)
				.param("currentPassword", "") // 빈 현재 비밀번호
				.param("newPassword", "123") // 짧은 새 비밀번호
				.param("confirmPassword", "123"))
				.andExpect(status().isOk())
				.andExpect(view().name("common/layout"))
				.andReturn();

		// then
		BindingResult pwBr = (BindingResult) result.getModelAndView().getModelMap()
				.get("org.springframework.validation.BindingResult.passwordForm");
		assertTrue("비밀번호 검증 실패 시 passwordForm에 에러가 있어야 한다 → 테스트 성공",
				pwBr != null && pwBr.hasErrors());

		verify(userAppService, never()).changePassword(any(UserId.class), anyString(), anyString());
	}
}