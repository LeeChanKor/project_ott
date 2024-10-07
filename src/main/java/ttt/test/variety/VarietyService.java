package ttt.test.variety;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class VarietyService {

    @Autowired
    private VarietyRepository varietyRepository;

    // 예능 제목에 검색어가 포함된 항목을 찾기 위한 조건을 만드는 메서드
    private Specification<Variety> search(String kw) {
        return (Root<Variety> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            query.distinct(true); // 중복 제거
            // 제목에 검색어가 포함된 예능을 찾기 위한 조건
            Predicate varietyNamePredicate = cb.like(root.get("varietyName"), "%" + kw + "%");
            return varietyNamePredicate;
        };
    }

    // 최신 예능 5개를 가져오는 메서드
    public List<Variety> getLatestVarieties(int limit) {
        return varietyRepository.findTop5ByOrderByRegisterDateDesc(); // 최근 등록된 5개 예능
    }

    // 페이지와 검색어를 이용해 예능 목록을 가져오는 메서드
    public Page<Variety> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("registerDate")); // 최신 등록일 기준으로 정렬
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); // 페이지당 10개 예능
        Specification<Variety> spec = search(kw); // 검색 조건 적용
        return varietyRepository.findAll(spec, pageable); // 검색 조건과 페이지 정보로 예능 목록 반환
    }

    // 최신 예능 21개를 가져오는 메서드
    public List<Variety> getMainVarieties() {
        return varietyRepository.findTop21ByOrderByRegisterDateDesc(); // 최근 등록된 21개 예능
    }

    // 모든 예능을 가져오는 메서드
    public List<Variety> getAllVarieties() {
        return varietyRepository.findAll(); // 전체 예능 리스트
    }

    // 제목에 검색어가 포함된 예능을 찾는 메서드
    public List<Variety> searchVarieties(String query) {
        return varietyRepository.findByVarietyNameContaining(query); // 제목에 검색어가 포함된 예능 목록 반환
    }
}
