package com.eggmoney.payv.infrastructure.mybatis.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.eggmoney.payv.domain.model.entity.User;
import com.eggmoney.payv.domain.model.repository.UserRepository;
import com.eggmoney.payv.domain.model.vo.UserRole;

/**
 * UserRepository 통합 테스트 MyBatis와 DB 연동이 정상적으로 동작하는지 확인 DB에 ROLE 컬럼이 없으므로 관련 테스트
 * 수정 이름 길이 제한 (20자) 고려
 * 
 * @author 강기범, 정의탁
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/root-context.xml")
@Transactional
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	/**
	 * 테스트용 고유 문자열 생성 (짧게)
	 */
	private String generateShortUniqueStr() {
		return String.valueOf(System.nanoTime() % 1000000); // 마지막 6자리만 사용
	}

	@Test
	public void 사용자_저장_및_조회_테스트() {
		// given
		String email = "test" + generateShortUniqueStr() + "@example.com";
		User user = User.create(email, "테스트사용자", "encodedPassword123");

		// when - 저장
		userRepository.save(user);

		// then - ID로 조회
		Optional<User> foundById = userRepository.findById(user.getId());
		assertTrue(foundById.isPresent());
		assertEquals(user.getEmail(), foundById.get().getEmail());
		assertEquals(user.getName(), foundById.get().getName());
		assertEquals(user.getPassword(), foundById.get().getPassword());
		// DB에 role이 저장되지 않으므로 기본값 USER로 설정됨
		assertEquals(UserRole.USER, foundById.get().getRole());

		// then - 이메일로 조회
		Optional<User> foundByEmail = userRepository.findByEmail(email);
		assertTrue(foundByEmail.isPresent());
		assertEquals(user.getId(), foundByEmail.get().getId());
	}

	@Test
	public void 사용자_수정_테스트() {
		// given
		String email = "update" + generateShortUniqueStr() + "@example.com";
		User user = User.create(email, "원래이름", "originalPassword");
		userRepository.save(user);

		// when - 정보 수정 (role 변경은 DB에 저장되지 않음)
		user.changeName("수정된이름");
		user.changePassword("updatedPassword");
		// role 변경은 메모리에서만 유지되고 DB에는 저장되지 않음
		user.changeRole(UserRole.PREMIUM);
		userRepository.save(user);

		// then
		Optional<User> updated = userRepository.findById(user.getId());
		assertTrue(updated.isPresent());
		assertEquals("수정된이름", updated.get().getName());
		assertEquals("updatedPassword", updated.get().getPassword());
		// DB에서 조회하면 role은 기본값 USER로 설정됨 (DB에 저장되지 않으므로)
		assertEquals(UserRole.USER, updated.get().getRole());
	}

	@Test
	public void 사용자_삭제_테스트() {
		// given
		String email = "delete" + generateShortUniqueStr() + "@example.com";
		User user = User.create(email, "삭제될사용자", "password");
		userRepository.save(user);

		// 저장 확인
		assertTrue(userRepository.findById(user.getId()).isPresent());

		// when - 삭제
		userRepository.deleteById(user.getId());

		// then
		assertFalse(userRepository.findById(user.getId()).isPresent());
	}

	@Test
	public void 이메일_중복_확인_테스트() {
		// given
		String email = "dup" + generateShortUniqueStr() + "@example.com";
		User user = User.create(email, "중복테스트", "password");
		userRepository.save(user);

		// then
		assertTrue(userRepository.existsByEmail(email));
		assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
	}

	@Test
	public void 이메일_중복_확인_제외_테스트() {
		// given
		String uniqueStr = generateShortUniqueStr();
		String email = "exclude" + uniqueStr + "@example.com";
		User user1 = User.create(email, "사용자1", "password");
		User user2 = User.create("other" + uniqueStr + "@example.com", "사용자2", "password");
		userRepository.save(user1);
		userRepository.save(user2);

		// then
		// user1은 자기 자신을 제외하므로 중복이 아님
		assertFalse(userRepository.existsByEmailAndIdNot(email, user1.getId()));

		// user2가 user1의 이메일을 사용하려고 하면 중복임
		assertTrue(userRepository.existsByEmailAndIdNot(email, user2.getId()));
	}

	@Test
	public void 역할별_사용자_조회_테스트() {
		// given - DB에 role이 저장되지 않으므로 모든 사용자는 USER role을 가짐
		String uniqueStr = generateShortUniqueStr();
		String baseEmail = "role" + uniqueStr;
		User normalUser = User.create(baseEmail + "1@example.com", "일반사용자", "password");
		User user2 = User.create(baseEmail + "2@example.com", "사용자2", "password");
		User user3 = User.create(baseEmail + "3@example.com", "사용자3", "password");

		userRepository.save(normalUser);
		userRepository.save(user2);
		userRepository.save(user3);

		// when & then - DB에 role 컬럼이 없으므로 모든 사용자는 USER role로 조회됨
		List<User> userRoleUsers = userRepository.findByRole(UserRole.USER);
		List<User> premiumUsers = userRepository.findByRole(UserRole.PREMIUM);
		List<User> adminUsers = userRepository.findByRole(UserRole.ADMIN);

		// 우리가 추가한 사용자들은 모두 USER role로 조회되어야 함
		assertTrue("normalUser가 USER role로 조회되어야 함",
				userRoleUsers.stream().anyMatch(u -> u.getId().equals(normalUser.getId())));
		assertTrue("user2가 USER role로 조회되어야 함", userRoleUsers.stream().anyMatch(u -> u.getId().equals(user2.getId())));
		assertTrue("user3이 USER role로 조회되어야 함", userRoleUsers.stream().anyMatch(u -> u.getId().equals(user3.getId())));

		// PREMIUM, ADMIN role로는 조회되지 않아야 함 (DB에 role이 없으므로)
		assertFalse("PREMIUM role 사용자는 없어야 함", premiumUsers.stream().anyMatch(u -> u.getId().equals(normalUser.getId())
				|| u.getId().equals(user2.getId()) || u.getId().equals(user3.getId())));

		assertFalse("ADMIN role 사용자는 없어야 함", adminUsers.stream().anyMatch(u -> u.getId().equals(normalUser.getId())
				|| u.getId().equals(user2.getId()) || u.getId().equals(user3.getId())));
	}

	@Test
	public void 전체_사용자_조회_테스트() {
		// given
		String uniqueStr = generateShortUniqueStr();
		String baseEmail = "all" + uniqueStr;
		User user1 = User.create(baseEmail + "1@example.com", "사용자1", "password");
		User user2 = User.create(baseEmail + "2@example.com", "사용자2", "password");

		userRepository.save(user1);
		userRepository.save(user2);

		// when
		List<User> allUsers = userRepository.findAll();

		// then
		assertTrue(allUsers.size() >= 2);
		assertTrue(allUsers.stream().anyMatch(u -> u.getId().equals(user1.getId())));
		assertTrue(allUsers.stream().anyMatch(u -> u.getId().equals(user2.getId())));

		// 모든 사용자의 role이 기본값 USER로 설정되어야 함
		allUsers.forEach(user -> assertEquals("모든 사용자의 role은 USER여야 함", UserRole.USER, user.getRole()));
	}

	@Test
	public void 이름_검색_테스트() {
		// given - 이름 길이 제한 고려하여 짧게 생성
		String uniqueStr = String.valueOf(System.nanoTime() % 10000); // 4자리 숫자
		User user1 = User.create("search1" + uniqueStr + "@example.com", "홍길동" + uniqueStr, "password");
		User user2 = User.create("search2" + uniqueStr + "@example.com", "김" + uniqueStr + "철수", "password");
		User user3 = User.create("search3" + uniqueStr + "@example.com", "박영희", "password");

		userRepository.save(user1);
		userRepository.save(user2);
		userRepository.save(user3);

		// when
		List<User> foundUsers = userRepository.findByNameContaining(uniqueStr, 10, 0);

		// then
		assertEquals(2, foundUsers.size());
		assertTrue(foundUsers.stream().anyMatch(u -> u.getId().equals(user1.getId())));
		assertTrue(foundUsers.stream().anyMatch(u -> u.getId().equals(user2.getId())));
		assertFalse(foundUsers.stream().anyMatch(u -> u.getId().equals(user3.getId())));
	}

	@Test
	public void 역할별_개수_조회_테스트() {
		// given - DB에 role이 저장되지 않으므로 모든 사용자는 USER role을 가짐
		String uniqueStr = generateShortUniqueStr();
		String baseEmail = "count" + uniqueStr;
		User user1 = User.create(baseEmail + "1@example.com", "사용자1", "password");
		User user2 = User.create(baseEmail + "2@example.com", "사용자2", "password");
		User user3 = User.create(baseEmail + "3@example.com", "사용자3", "password");

		userRepository.save(user1);
		userRepository.save(user2);
		userRepository.save(user3);

		// when
		long userCount = userRepository.countByRole(UserRole.USER);
		long premiumCount = userRepository.countByRole(UserRole.PREMIUM);
		long adminCount = userRepository.countByRole(UserRole.ADMIN);

		// then - 모든 사용자는 USER role로 계산되어야 함
		assertTrue("USER role 개수가 3 이상이어야 함", userCount >= 3);
		assertEquals("PREMIUM role 개수는 0이어야 함", 0, premiumCount);
		assertEquals("ADMIN role 개수는 0이어야 함", 0, adminCount);
	}

	@Test
	public void 존재하지_않는_사용자_조회_테스트() {
		// when
		Optional<User> notFound = userRepository.findByEmail("nonexistent@example.com");

		// then
		assertFalse(notFound.isPresent());
	}

	@Test
	public void CREATE_AT_컬럼_매핑_확인_테스트() {
		// given
		String email = "createat" + generateShortUniqueStr() + "@example.com";
		User user = User.create(email, "매핑테스트", "password");

		// when - 저장
		userRepository.save(user);

		// then - 조회하여 CREATE_AT 컬럼이 정상적으로 매핑되는지 확인
		Optional<User> foundUser = userRepository.findById(user.getId());
		assertTrue("사용자가 조회되어야 함", foundUser.isPresent());
		assertNotNull("CREATE_AT이 정상적으로 매핑되어야 함", foundUser.get().getCreateAt());

		// 이메일로 조회해도 시간 정보가 있어야 함
		Optional<User> foundByEmail = userRepository.findByEmail(email);
		assertTrue("이메일로 조회되어야 함", foundByEmail.isPresent());
		assertNotNull("이메일 조회 시에도 CREATE_AT이 매핑되어야 함", foundByEmail.get().getCreateAt());
	}

	@Test
	public void 이름_null_허용_확인_테스트() {
		// given
		String email = "nullname" + generateShortUniqueStr() + "@example.com";
		User user = User.create(email, null, "password");

		// when - 저장
		userRepository.save(user);

		// then - 조회
		Optional<User> foundUser = userRepository.findById(user.getId());
		assertTrue("사용자가 조회되어야 함", foundUser.isPresent());
		assertNull("이름이 null이어야 함", foundUser.get().getName());

		// 빈 문자열 테스트
		String email2 = "empty" + generateShortUniqueStr() + "@example.com";
		User user2 = User.create(email2, "", "password");
		userRepository.save(user2);

		Optional<User> foundUser2 = userRepository.findById(user2.getId());
		assertTrue("사용자가 조회되어야 함", foundUser2.isPresent());
		assertNull("빈 문자열은 null로 처리되어야 함", foundUser2.get().getName());
	}

	@Test
	public void 이름_길이_제한_테스트() {
		// given - 20자 정확히 맞는 이름
		String email = "length" + generateShortUniqueStr() + "@example.com";
		String name20Chars = "12345678901234567890"; // 정확히 20자
		User user = User.create(email, name20Chars, "password");

		// when - 저장
		userRepository.save(user);

		// then - 조회
		Optional<User> foundUser = userRepository.findById(user.getId());
		assertTrue("사용자가 조회되어야 함", foundUser.isPresent());
		assertEquals("20자 이름이 정확히 저장되어야 함", name20Chars, foundUser.get().getName());
	}
}