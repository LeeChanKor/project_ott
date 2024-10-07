package ttt.test.user;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ttt.test.favorite.Favorite;
import ttt.test.favorite.FavoriteRepository;
import ttt.test.mypage.WatchHistoryRepository;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final FavoriteRepository favoriteRepository;

    private final WatchHistoryRepository watchHistoryRepository;

    // 검색어로 사용자 이름을 찾기 위한 조건을 설정
    private Specification<SiteUser> search(String kw) {
        return (Root<SiteUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            query.distinct(true);
            if (kw == null || kw.isEmpty()) {
                return cb.conjunction(); // 모든 사용자 반환
            }
            return cb.like(root.get("username"), "%" + kw + "%");
        };
    }


    // 새 사용자 생성 메서드
    public SiteUser create(String username, String email, String password, boolean isAdmin) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password); // 평문 비밀번호를 그대로 저장
        user.setRegisterDate(new Date());

        // 관리자 계정이 아닌 경우 ROLE_USER만 부여
        if (isAdmin) {
            user.setRoles(Set.of("ROLE_ADMIN"));
        } else {
            user.setRoles(Set.of("ROLE_USER"));
        }

        this.userRepository.save(user); // 사용자 저장
        return user;
    }

    // 이용권 해지 메서드
    public void cancelMembership(Long userId) {
        SiteUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setMembership(null); // 이용권 해지
        userRepository.save(user); // 사용자 정보 저장
    }

    // 구독 유효성 확인 메서드
    public boolean isSubscriptionValid(String username) {
        Optional<SiteUser> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            SiteUser user = userOpt.get();
            List<String> validMemberships = Arrays.asList("베이직", "스탠다드", "프리미엄");
            boolean isValid = user.getMembership() != null && validMemberships.contains(user.getMembership());

            // 디버깅 로그
            System.out.println("User Membership: " + user.getMembership());
            System.out.println("Is Subscription Valid: " + isValid);

            return isValid;
        }
        System.out.println("User not found or membership is null");
        return false;
    }

    @Transactional
    public void deleteUser(Long userId) {
        // 사용자 이름을 사용하여 관련된 watch_history 레코드를 삭제
        SiteUser user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found")); // 사용자 가져오기

        String username = user.getUsername(); // 사용자 이름 가져오기

        // watch_history에서 사용자와 관련된 레코드 삭제
        watchHistoryRepository.deleteAllByUserName(username);

        // 사용자의 즐겨찾기 항목 삭제
        favoriteRepository.deleteByUsername(username); // SiteUser 객체로 전달

        // 사용자 삭제
        System.out.println("Deleting user with ID: " + userId); // 로그 추가
        userRepository.deleteById(userId); // 사용자 ID로 삭제
        System.out.println("User deleted"); // 로그 추가
    }

    // 사용자 이름을 ID로 찾는 메서드
    public String findUsernameById(Long userId) {
        SiteUser user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        return user.getUsername();
    }



    // 사용자 이름으로 사용자 정보 조회
    public SiteUser getUserByUsername(String username) {
        Optional<SiteUser> user = userRepository.findByUsername(username);
        return user.orElse(null); // 사용자 정보 반환, 없으면 null 반환
    }

    // 사용자 ID로 사용자 정보 조회
    public SiteUser getUserById(Long userId) {
        Optional<SiteUser> userOpt = userRepository.findById(userId);
        return userOpt.orElse(null); // 사용자 정보 반환, 없으면 null 반환
    }

    // 최신 등록된 사용자 5명을 가져오는 메서드
    public List<SiteUser> getLatestUsers(int limit) {
        return userRepository.findAllByOrderByRegisterDateDesc().stream()
                .filter(user -> user.getRoles() == null || user.getRoles().isEmpty() || !user.getRoles().contains("ROLE_ADMIN"))  // roles가 null이거나 비어있거나 ROLE_ADMIN이 아닌 사용자 필터링
                .limit(limit)
                .collect(Collectors.toList()); // 제한된 사용자 목록 반환
    }

    // 모든 사용자 조회
    public List<SiteUser> getAllUsers() {
        return userRepository.findAll(); // 전체 유저 리스트 반환
    }

    // 관리자 권한이 있는 사용자 목록을 페이지별로 가져오는 메서드
    public Page<SiteUser> getAdminspaging(Pageable pageable) {
        return userRepository.findByRolesContaining("ROLE_ADMIN", pageable); // ROLE_ADMIN을 가진 사용자 페이지네이션 조회
    }

    public Page<SiteUser> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("registerDate")); // 등록일 기준으로 내림차순 정렬
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); // 페이지당 10개 항목으로 설정

        Specification<SiteUser> spec = search(kw); // 검색 조건 적용
        // admin 사용자를 제외한 쿼리 추가
        Specification<SiteUser> adminExcludedSpec = (Root<SiteUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate adminPredicate = cb.notLike(root.get("username"), "%admin%");
            return cb.and(spec.toPredicate(root, query, cb), adminPredicate);
        };

        return userRepository.findAll(adminExcludedSpec, pageable); // 검색 조건과 페이지네이션 적용
    }


}
