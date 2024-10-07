package ttt.test.mypage;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ttt.test.drama.Drama;
import ttt.test.drama.DramaRepository;
import ttt.test.movie.Movie;
import ttt.test.movie.MovieRepository;
import ttt.test.variety.Variety;
import ttt.test.variety.VarietyRepository;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/watch-history")
public class WatchHistoryController {

    private final WatchHistoryService watchHistoryService; // 시청 기록 서비스를 위한 의존성
    private final DramaRepository dramaRepository; // 드라마 레포지토리
    private final MovieRepository movieRepository; // 영화 레포지토리
    private final VarietyRepository varietyRepository; // 예능 레포지토리

    // 생성자 주입
    public WatchHistoryController(WatchHistoryService watchHistoryService,
                                  DramaRepository dramaRepository,
                                  MovieRepository movieRepository,
                                  VarietyRepository varietyRepository) {
        this.watchHistoryService = watchHistoryService;
        this.dramaRepository = dramaRepository;
        this.movieRepository = movieRepository;
        this.varietyRepository = varietyRepository;
    }

    // 드라마 시청 기록 저장
    @PostMapping("/drama")
    public ResponseEntity<String> saveDramaWatchHistory(Principal principal, @RequestParam("dramaId") int dramaId) {
        Drama drama = dramaRepository.findById(dramaId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 드라마 ID"));

        // 중복된 시청 기록 삭제
        watchHistoryService.deleteDuplicateDramaWatchHistory(principal.getName(), dramaId);

        // 시청 기록 추가
        WatchHistory watchHistory = new WatchHistory();
        watchHistory.setUsername(principal.getName());
        watchHistory.setDrama(drama);
        watchHistory.setWatchedAt(LocalDateTime.now());
        watchHistoryService.saveWatchHistory(watchHistory);

        return ResponseEntity.ok("시청 기록 저장 완료");
    }

    // 영화 시청 기록 저장
    @PostMapping("/movie")
    public ResponseEntity<String> saveMovieWatchHistory(Principal principal, @RequestParam("movieId") int movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 영화 ID"));

        // 중복된 시청 기록 삭제
        watchHistoryService.deleteDuplicateMovieWatchHistory(principal.getName(), movieId);

        // 시청 기록 추가
        WatchHistory watchHistory = new WatchHistory();
        watchHistory.setUsername(principal.getName());
        watchHistory.setMovie(movie);
        watchHistory.setWatchedAt(LocalDateTime.now());
        watchHistoryService.saveWatchHistory(watchHistory);

        return ResponseEntity.ok("시청 기록 저장 완료");
    }

    // 예능 시청 기록 저장
    @PostMapping("/variety")
    public ResponseEntity<String> saveVarietyWatchHistory(Principal principal, @RequestParam("varietyId") int varietyId) {
        Variety variety = varietyRepository.findById(varietyId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 예능 ID"));

        // 중복된 시청 기록 삭제
        watchHistoryService.deleteDuplicateVarietyWatchHistory(principal.getName(), varietyId);

        // 시청 기록 추가
        WatchHistory watchHistory = new WatchHistory();
        watchHistory.setUsername(principal.getName());
        watchHistory.setVariety(variety);
        watchHistory.setWatchedAt(LocalDateTime.now());
        watchHistoryService.saveWatchHistory(watchHistory);

        return ResponseEntity.ok("시청 기록 저장 완료");
    }

    // 사용자 시청 기록 조회
    @GetMapping("/my_watch")
    public String getWatchHistory(Principal principal, Model model) {
        // 사용자의 시청 기록 가져오기
        List<WatchHistory> watchHistoryList = watchHistoryService.getWatchHistoryByUsername(principal.getName());
        model.addAttribute("watchHistoryList", watchHistoryList);
        return "mypage/my_watch"; // 시청 기록을 보여주는 뷰 반환
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteWatchHistory(@RequestBody List<Long> ids) {
        watchHistoryService.deleteWatchHistoryByIds(ids);
        return ResponseEntity.ok("삭제 완료");
    }
}
