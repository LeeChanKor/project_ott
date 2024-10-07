package ttt.test.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import ttt.test.drama.Drama;


public interface UserRepository extends JpaRepository<SiteUser, Long> {

    // 사용자 이름으로 사용자 조회
    Optional<SiteUser> findByUsername(String username);

    // 이메일로 사용자 조회
    Optional<SiteUser> findByEmail(String email);


    // 등록일 기준으로 모든 사용자 조회
    List<SiteUser> findAllByOrderByRegisterDateDesc();

    // 역할(role)에 따라 페이지네이션 처리를 위한 사용자 조회
    Page<SiteUser> findByRolesContaining(String role, Pageable pageable);

    Page<SiteUser> findAll(Specification<SiteUser> spec, Pageable pageable);

    long countByUsernameNotContaining(String keyword);







}
