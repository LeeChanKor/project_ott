package ttt.test.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ttt.test.drama.Drama;
import ttt.test.drama.DramaService;
import ttt.test.movie.Movie;
import ttt.test.movie.MovieService;
import ttt.test.variety.Variety;
import ttt.test.variety.VarietyService;


import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private DramaService dramaService; // DramaService 의존성 주입

    @Autowired
    private MovieService movieService; // MovieService 의존성 주입

    @Autowired
    private VarietyService varietyService; // VarietyService 의존성 주입

    // 검색 결과를 처리하고 결과를 모델에 추가하여 검색 결과 페이지로 이동
    @PostMapping("/search_results")
    public String searchResults(@RequestParam(value = "query", defaultValue = "") String query,
                                Model model,
                                HttpSession session) {
        if (query.isEmpty()) {
            return "common/search_results"; // 검색어가 비어있으면 검색 결과 페이지 반환
        }

        System.out.println("검색어: " + query); // 검색어를 콘솔에 출력
        List<Drama> dramas = dramaService.searchDramas(query); // 드라마 검색
        List<Movie> movies = movieService.searchMovies(query); // 영화 검색
        List<Variety> varieties = varietyService.searchVarieties(query); // 예능 검색

        // 검색 결과를 세션에 저장
        session.setAttribute("searchResultsDramas", dramas);
        session.setAttribute("searchResultsMovies", movies);
        session.setAttribute("searchResultsVarieties", varieties);
        session.setAttribute("searchQuery", query);

        model.addAttribute("dramas", dramas); // 드라마 결과를 모델에 추가
        model.addAttribute("movies", movies); // 영화 결과를 모델에 추가
        model.addAttribute("varieties", varieties); // 예능 결과를 모델에 추가
        model.addAttribute("query", query); // 검색어를 모델에 추가

        return "common/search_results"; // 검색 결과 페이지로 이동
    }

    // 검색 결과 페이지에 세션에서 결과를 불러오기 위한 메서드
    @GetMapping("/search_results")
    public String getSearchResults(HttpSession session, Model model) {
        List<Drama> dramas = (List<Drama>) session.getAttribute("searchResultsDramas");
        List<Movie> movies = (List<Movie>) session.getAttribute("searchResultsMovies");
        List<Variety> varieties = (List<Variety>) session.getAttribute("searchResultsVarieties");
        String query = (String) session.getAttribute("searchQuery");

        if (dramas != null) {
            model.addAttribute("dramas", dramas);
        }
        if (movies != null) {
            model.addAttribute("movies", movies);
        }
        if (varieties != null) {
            model.addAttribute("varieties", varieties);
        }
        model.addAttribute("query", query); // 검색어를 모델에 추가

        return "common/search_results"; // 검색 결과 페이지로 이동
    }
}
