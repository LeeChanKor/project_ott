package ttt.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ttt.test.user.SiteUser;
import ttt.test.user.UserRepository;

import java.util.Set;

import static org.assertj.core.util.DateUtil.now;

// Spring Boot의 테스트 컨텍스트를 로드하여 통합 테스트를 수행하기 위한 클래스입니다.
@SpringBootTest
class TestApplicationTests {

	// Spring의 의존성 주입을 통해 UserRepository를 주입받습니다.
	@Autowired
	private UserRepository userRepository;

	// 각 테스트 메소드 실행 전에 실행되는 메소드입니다.
	@BeforeEach
	public void setup() {
		// 여러 명의 관리자를 추가합니다.
		addAdminUser("admin1", "admin1@admin.com");
		addAdminUser("admin2", "admin2@admin.com");
		addAdminUser("admin3", "admin3@admin.com");
	}

	private void addAdminUser(String username, String email) {
		// 데이터베이스에 해당 username을 가진 사용자가 존재하지 않는 경우에만 실행됩니다.
		if (userRepository.findByUsername(username).isEmpty()) {
			// SiteUser 객체를 생성합니다.
			SiteUser adminUser = new SiteUser();

			// 사용자 정보를 설정합니다.
			adminUser.setUsername(username);
			adminUser.setPassword("admin"); // 비밀번호는 평문으로 설정되어 있으며, 실제로는 암호화된 비밀번호를 사용해야 합니다.
			adminUser.setEmail(email);
			adminUser.setMembership("프리미엄");
			adminUser.setRegisterDate(now());

			// 사용자에게 역할을 부여합니다. 이 경우 'ROLE_ADMIN' 역할을 부여합니다.
			adminUser.setRoles(Set.of("ROLE_ADMIN"));

			// 설정한 사용자를 데이터베이스에 저장합니다.
			userRepository.save(adminUser);
		}
	}

	// 실제 테스트 메소드입니다. 현재는 아무런 테스트 코드도 작성되어 있지 않습니다.
	@Test
	void contextLoads() {
		// 이 메소드 안에 실제 테스트 코드를 작성합니다. 예를 들어, 애플리케이션 컨텍스트가 정상적으로 로드되는지 확인할 수 있습니다.
	}
}
