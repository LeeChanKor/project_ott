package ttt.test.movie;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Integer>, JpaSpecificationExecutor<Movie> {

    // 최신 등록일 기준으로 상위 5개의 영화를 반환
    List<Movie> findTop5ByOrderByRegisterDateDesc();

    // 최신 등록일 기준으로 상위 21개의 영화를 반환
    List<Movie> findTop21ByOrderByRegisterDateDesc();

    // 페이징 처리된 모든 영화 목록을 반환
    Page<Movie> findAll(Pageable pageable);

    // 주어진 사양(specification)과 페이징 처리로 영화 목록을 반환
    Page<Movie> findAll(Specification<Movie> spec, Pageable pageable);

    // 주어진 장르 목록에 해당하는 영화들을 반환
    List<Movie> findByMovieGenreIn(List<String> genres);

    // 제목에 특정 문자열을 포함하는 영화들을 반환
    List<Movie> findByMovieNameContaining(String title);

}
