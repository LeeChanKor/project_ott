package ttt.test.movie;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    // 최신 영화 5개를 가져오는 메서드 (관리자 대시보드에서 사용)
    public List<Movie> getLatestMovies(int limit) {
        return movieRepository.findTop5ByOrderByRegisterDateDesc(); // 최신 5개 영화 반환
    }

    // 최신 영화 21개를 가져오는 메서드
    public List<Movie> getMainMovies() {
        return movieRepository.findTop21ByOrderByRegisterDateDesc(); // 최신 21개 영화 반환
    }

    // 모든 영화를 가져오는 메서드
    public List<Movie> getAllMovies() {
        return movieRepository.findAll(); // 전체 영화 리스트 반환
    }

    // 검색 조건을 생성하는 메서드
    private Specification<Movie> search(String kw) {
        return (Root<Movie> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            query.distinct(true); // 중복된 결과 제거
            Predicate movieNamePredicate = cb.like(root.get("movieName"), "%" + kw + "%"); // 영화 제목에 검색어가 포함된 경우
            return movieNamePredicate;
        };
    }

    // 페이징과 검색 기능을 추가하여 영화 목록을 반환하는 메서드
    public Page<Movie> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("registerDate")); // 등록일 기준으로 내림차순 정렬
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); // 페이지당 10개 항목 설정
        Specification<Movie> spec = search(kw); // 검색 조건 적용
        return movieRepository.findAll(spec, pageable); // 페이징 및 검색된 영화 목록 반환
    }

    // 장르를 기준으로 영화를 검색하는 메서드
    public List<Movie> findMoviesByCategory(String category) {
        List<String> genres = Arrays.asList(category.split("/")); // 장르 목록 생성
        return movieRepository.findByMovieGenreIn(genres); // 장르 목록에 해당하는 영화 반환
    }

    // 제목에 검색어가 포함된 영화 목록을 반환하는 메서드
    public List<Movie> searchMovies(String query) {
        return movieRepository.findByMovieNameContaining(query); // 제목에 검색어가 포함된 영화 목록 반환
    }

}
