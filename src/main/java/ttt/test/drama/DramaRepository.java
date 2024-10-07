package ttt.test.drama;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DramaRepository extends JpaRepository<Drama, Integer>, JpaSpecificationExecutor<Drama> {

    // 최신 등록일 기준으로 상위 5개 드라마를 조회
    List<Drama> findTop5ByOrderByRegisterDateDesc();

    // 최신 등록일 기준으로 상위 21개 드라마를 조회
    List<Drama> findTop21ByOrderByRegisterDateDesc();

    // 페이지별로 나누어 모든 드라마를 조회
    Page<Drama> findAll(Pageable pageable);

    // 검색 조건을 적용하고, 페이지별로 나누어 드라마 목록을 조회
    Page<Drama> findAll(Specification<Drama> spec, Pageable pageable);

    // 주어진 장르 리스트에 포함된 드라마를 조회
    List<Drama> findByDramaGenreIn(List<String> genres);

    // 드라마 이름에 주어진 문자열이 포함된 드라마를 조회
    List<Drama> findByDramaNameContaining(String title);

//    // 최대 드라마 번호를 조회하는 메서드 추가
//    @Query("SELECT COALESCE(MAX(d.dramaNo), 1000) FROM Drama d")
//    Integer findMaxDramaNo();


//    // 최신 등록일 기준으로 모든 드라마를 조회
//    List<Drama> findAllByOrderByRegisterDateDesc();

}
