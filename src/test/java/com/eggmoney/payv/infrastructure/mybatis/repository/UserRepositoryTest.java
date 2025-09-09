package com.eggmoney.payv.infrastructure.mybatis.repository;

import static org.junit.Assert.*;

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
 * UserRepository 통합 테스트
 * MyBatis와 DB 연동이 정상적으로 동작하는지 확인
 * 
 * @author 강기범
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/root-context.xml")
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 사용자_저장_및_조회_테스트() {
        // given
        String email = "test" + System.nanoTime() + "@example.com";
        User user = User.create(email, "테스트 사용자", "encodedPassword123");

        // when - 저장
        userRepository.save(user);

        // then - ID로 조회
        Optional<User> foundById = userRepository.findById(user.getId());
        assertTrue(foundById.isPresent());
        assertEquals(user.getEmail(), foundById.get().getEmail());
        assertEquals(user.getName(), foundById.get().getName());
        assertEquals(user.getPassword(), foundById.get().getPassword());
        assertEquals(user.getRole(), foundById.get().getRole());

        // then - 이메일로 조회
        Optional<User> foundByEmail = userRepository.findByEmail(email);
        assertTrue(foundByEmail.isPresent());
        assertEquals(user.getId(), foundByEmail.get().getId());
    }

    @Test
    public void 사용자_수정_테스트() {
        // given
        String email = "update" + System.nanoTime() + "@example.com";
        User user = User.create(email, "원래 이름", "originalPassword");
        userRepository.save(user);

        // when - 정보 수정
        user.changeName("수정된 이름");
        user.changePassword("updatedPassword");
        user.changeRole(UserRole.PREMIUM);
        userRepository.save(user);

        // then
        Optional<User> updated = userRepository.findById(user.getId());
        assertTrue(updated.isPresent());
        assertEquals("수정된 이름", updated.get().getName());
        assertEquals("updatedPassword", updated.get().getPassword());
        assertEquals(UserRole.PREMIUM, updated.get().getRole());
    }

    @Test
    public void 사용자_삭제_테스트() {
        // given
        String email = "delete" + System.nanoTime() + "@example.com";
        User user = User.create(email, "삭제될 사용자", "password");
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
        String email = "duplicate" + System.nanoTime() + "@example.com";
        User user = User.create(email, "사용자", "password");
        userRepository.save(user);

        // then
        assertTrue(userRepository.existsByEmail(email));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    public void 이메일_중복_확인_제외_테스트() {
        // given
        String email = "exclude" + System.nanoTime() + "@example.com";
        User user1 = User.create(email, "사용자1", "password");
        User user2 = User.create("other" + System.nanoTime() + "@example.com", "사용자2", "password");
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
        // given
        String baseEmail = "role" + System.nanoTime();
        User normalUser = User.create(baseEmail + "1@example.com", "일반사용자", "password");
        User premiumUser = User.createPremium(baseEmail + "2@example.com", "프리미엄사용자", "password");
        User adminUser = User.createAdmin(baseEmail + "3@example.com", "관리자", "password");

        userRepository.save(normalUser);
        userRepository.save(premiumUser);
        userRepository.save(adminUser);

        // when & then
        List<User> normalUsers = userRepository.findByRole(UserRole.USER);
        List<User> premiumUsers = userRepository.findByRole(UserRole.PREMIUM);
        List<User> adminUsers = userRepository.findByRole(UserRole.ADMIN);

        // 적어도 우리가 추가한 사용자들은 포함되어야 함
        assertTrue(normalUsers.stream().anyMatch(u -> u.getId().equals(normalUser.getId())));
        assertTrue(premiumUsers.stream().anyMatch(u -> u.getId().equals(premiumUser.getId())));
        assertTrue(adminUsers.stream().anyMatch(u -> u.getId().equals(adminUser.getId())));
    }

    @Test
    public void 전체_사용자_조회_테스트() {
        // given
        String baseEmail = "all" + System.nanoTime();
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
    }

    @Test
    public void 이름_검색_테스트() {
        // given
        String uniqueStr = "search" + System.nanoTime();
        User user1 = User.create(uniqueStr + "1@example.com", "홍길동" + uniqueStr, "password");
        User user2 = User.create(uniqueStr + "2@example.com", "김" + uniqueStr + "철수", "password");
        User user3 = User.create(uniqueStr + "3@example.com", "박영희", "password");

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
        // given
        String baseEmail = "count" + System.nanoTime();
        User user1 = User.create(baseEmail + "1@example.com", "사용자1", "password");
        User user2 = User.create(baseEmail + "2@example.com", "사용자2", "password");
        User premium = User.createPremium(baseEmail + "3@example.com", "프리미엄", "password");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(premium);

        // when
        long userCount = userRepository.countByRole(UserRole.USER);
        long premiumCount = userRepository.countByRole(UserRole.PREMIUM);

        // then
        assertTrue(userCount >= 2);
        assertTrue(premiumCount >= 1);
    }

    @Test
    public void 존재하지_않는_사용자_조회_테스트() {
        // when
        Optional<User> notFound = userRepository.findByEmail("nonexistent@example.com");

        // then
        assertFalse(notFound.isPresent());
    }
}