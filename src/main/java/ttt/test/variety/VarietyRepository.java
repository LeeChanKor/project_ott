package ttt.test.variety;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ttt.test.drama.Drama;

import java.util.List;

@Repository
public interface VarietyRepository extends JpaRepository<Variety, Integer>, JpaSpecificationExecutor<Variety> {

    // 최근 등록된 5개의 예능을 가져오는 메서드
    List<Variety> findTop5ByOrderByRegisterDateDesc();

    // 최근 등록된 21개의 예능을 가져오는 메서드
    List<Variety> findTop21ByOrderByRegisterDateDesc();

    // 페이징을 사용하여 모든 예능을 가져오는 메서드
    Page<Variety> findAll(Pageable pageable);

    // 조건에 맞는 예능을 페이징을 사용하여 가져오는 메서드
    Page<Variety> findAll(Specification<Variety> spec, Pageable pageable);

    // 제목에 특정 문자열이 포함된 예능을 가져오는 메서드
    List<Variety> findByVarietyNameContaining(String title);
}
