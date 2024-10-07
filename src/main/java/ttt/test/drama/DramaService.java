package ttt.test.drama;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import ttt.test.favorite.FavoriteRepository;
import ttt.test.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


@RequiredArgsConstructor
@Service
public class DramaService {

    @Autowired
    private DramaRepository dramaRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserService userService;

    // 검색어로 드라마 제목을 찾기 위한 조건을 설정
    private Specification<Drama> search(String kw) {
        return (Root<Drama> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            query.distinct(true);
            Predicate dramaNamePredicate = cb.like(root.get("dramaName"), "%" + kw + "%");
            return dramaNamePredicate;
        };
    }

    // 최신 드라마 5개를 가져오는 메서드
    public List<Drama> getLatestDramas(int limit) {
        return dramaRepository.findTop5ByOrderByRegisterDateDesc(); // 최신 5개 드라마 반환
    }

    // 최신 드라마 21개를 가져오는 메서드
    public List<Drama> getMainDramas() {
        return dramaRepository.findTop21ByOrderByRegisterDateDesc(); // 최신 21개 드라마 반환
    }

    // 모든 드라마를 가져오는 메서드
    public List<Drama> getAllDramas() {
        return dramaRepository.findAll(); // 전체 드라마 리스트 반환
    }

    // 페이지와 검색어를 사용해서 드라마 리스트를 가져오는 메서드
    public Page<Drama> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("registerDate")); // 등록일 기준으로 내림차순 정렬
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); // 페이지당 10개 항목으로 설정
        Specification<Drama> spec = search(kw); // 검색 조건 적용
        return dramaRepository.findAll(spec, pageable); // 검색 조건과 페이지네이션 적용하여 드라마 반환
    }

    // 선택한 장르에 해당하는 드라마를 반환하는 메서드
    public List<Drama> findDramasByCategory(String category) {
        List<String> genres = Arrays.asList(category.split("/")); // 장르를 리스트로 변환
        return dramaRepository.findByDramaGenreIn(genres); // 장르 리스트에 해당하는 드라마 반환
    }

    // 제목에 검색어가 포함된 드라마를 반환하는 메서드
    public List<Drama> searchDramas(String query) {
        return dramaRepository.findByDramaNameContaining(query); // 제목에 검색어가 포함된 드라마 반환
    }
}