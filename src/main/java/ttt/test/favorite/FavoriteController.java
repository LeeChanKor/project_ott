package ttt.test.favorite;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ttt.test.drama.Drama;
import ttt.test.drama.DramaRepository;
import ttt.test.movie.Movie;
import ttt.test.movie.MovieRepository;
import ttt.test.mypage.WatchHistoryService;
import ttt.test.user.SiteUser;
import ttt.test.user.UserRepository;
import ttt.test.variety.Variety;
import ttt.test.variety.VarietyRepository;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")  // API 요청을 처리하는 컨트롤러
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final DramaRepository dramaRepository;
    private final MovieRepository movieRepository;
    private final VarietyRepository varietyRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;

    // 생성자 주입
    public FavoriteController(FavoriteService favoriteService,
                              DramaRepository dramaRepository,
                              MovieRepository movieRepository,
                              VarietyRepository varietyRepository,
                              UserRepository userRepository,
                              FavoriteRepository favoriteRepository) {
        this.favoriteService = favoriteService;
        this.dramaRepository = dramaRepository;
        this.movieRepository = movieRepository;
        this.varietyRepository = varietyRepository;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);

    // 특정 항목의 찜 상태를 토글하는 메서드
    @PostMapping("/{id}/toggle-heart")
    public ResponseEntity<Map<String, Object>> toggleHeart(
            @PathVariable("id") int id,  // 찜 상태를 토글할 드라마, 영화, 예능의 ID
            Principal principal,  // 현재 로그인한 사용자 정보를 담고 있는 객체
            @RequestParam(required = false) String type  // 항목의 타입: 'drama', 'movie', 'variety'
    ) {
        Map<String, Object> response = new HashMap<>();

        // 사용자가 로그인했는지 확인
        if (principal == null || principal.getName() == null) {
            response.put("success", false);
            response.put("message", "사용자 인증이 필요합니다.");
            return ResponseEntity.status(401).body(response); // 401 Unauthorized
        }

        String username = principal.getName();
        logger.info("Received toggleHeart request for id: {} and username: {}", id, username);

        // 로그인한 사용자 정보 가져오기
        SiteUser user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            response.put("success", false);
            response.put("message", "사용자를 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(response); // 404 Not Found
        }

        boolean isHearted = false;

        if ("drama".equals(type)) {
            // 드라마 타입인 경우
            Drama drama = dramaRepository.findById(id).orElse(null);
            if (drama == null) {
                response.put("success", false);
                response.put("message", "드라마를 찾을 수 없습니다.");
                return ResponseEntity.status(404).body(response); // 404 Not Found
            }
            isHearted = favoriteService.toggleFavorite(user, drama, null, null); // 드라마의 찜 상태를 바꾸고 그 결과를 `isHearted`에 저장합니다.
        } else if ("movie".equals(type)) {
            // 영화 타입인 경우
            Movie movie = movieRepository.findById(id).orElse(null);
            if (movie == null) {
                response.put("success", false);
                response.put("message", "영화를 찾을 수 없습니다.");
                return ResponseEntity.status(404).body(response); // 404 Not Found
            }
            isHearted = favoriteService.toggleFavorite(user, null, movie, null);
        } else if ("variety".equals(type)) {
            // 예능 타입인 경우
            Variety variety = varietyRepository.findById(id).orElse(null);
            if (variety == null) {
                response.put("success", false);
                response.put("message", "예능을 찾을 수 없습니다.");
                return ResponseEntity.status(404).body(response); // 404 Not Found
            }
            isHearted = favoriteService.toggleFavorite(user, null, null, variety);
        } else {
            // 유효하지 않은 타입이 요청된 경우
            response.put("success", false);
            response.put("message", "유효하지 않은 타입입니다.");
            return ResponseEntity.status(400).body(response); // 400 Bad Request
        }

        // 찜 상태를 성공적으로 변경했을 때의 응답
        response.put("success", true);
        response.put("isHearted", isHearted);  // 찜 상태가 등록되었는지 여부
        response.put("message", isHearted ? "찜 상태가 등록되었습니다." : "찜 상태가 해제되었습니다.");
        return ResponseEntity.ok(response); // 200 OK
    }

    // 특정 항목의 찜 상태를 확인하는 메서드
    @GetMapping("/{id}/is-hearted")
    public ResponseEntity<Map<String, Object>> isHearted(
            @PathVariable("id") int id,  // 찜 상태를 확인할 드라마, 영화, 예능의 ID
            Principal principal,  // 현재 로그인한 사용자 정보를 담고 있는 객체
            @RequestParam(required = false) String type  // 항목의 타입: 'drama', 'movie', 'variety'
    ) {
        Map<String, Object> response = new HashMap<>();

        // 사용자가 로그인했는지 확인
        if (principal == null || principal.getName() == null) {
            response.put("success", false);
            response.put("message", "사용자 인증이 필요합니다.");
            return ResponseEntity.status(401).body(response); // 401 Unauthorized
        }

        String username = principal.getName();

        // 로그인한 사용자 정보 가져오기
        SiteUser user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            response.put("success", false);
            response.put("message", "사용자를 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(response); // 404 Not Found
        }

        boolean isHearted = false;

        // 요청된 타입에 따라 해당 항목의 찜 상태를 확인
        if ("drama".equals(type)) {
            // 드라마 타입인 경우
            Drama drama = dramaRepository.findById(id).orElse(null);
            if (drama == null) {
                response.put("success", false);
                response.put("message", "드라마를 찾을 수 없습니다.");
                return ResponseEntity.status(404).body(response); // 404 Not Found
            }
            Optional<Favorite> favoriteDrama = favoriteRepository.findByUsernameAndDramaNo(user, drama);
            isHearted = favoriteDrama.isPresent();
            response.put("favoriteAt", isHearted ? favoriteDrama.get().getFavoriteAt() : null);
        } else if ("movie".equals(type)) {
            // 영화 타입인 경우
            Movie movie = movieRepository.findById(id).orElse(null);
            if (movie == null) {
                response.put("success", false);
                response.put("message", "영화를 찾을 수 없습니다.");
                return ResponseEntity.status(404).body(response); // 404 Not Found
            }
            Optional<Favorite> favoriteMovie = favoriteRepository.findByUsernameAndMovieNo(user, movie);
            isHearted = favoriteMovie.isPresent();
            response.put("favoriteAt", isHearted ? favoriteMovie.get().getFavoriteAt() : null);
        } else if ("variety".equals(type)) {
            // 예능 타입인 경우
            Variety variety = varietyRepository.findById(id).orElse(null);
            if (variety == null) {
                response.put("success", false);
                response.put("message", "예능을 찾을 수 없습니다.");
                return ResponseEntity.status(404).body(response); // 404 Not Found
            }
            Optional<Favorite> favoriteVariety = favoriteRepository.findByUsernameAndVarietyNo(user, variety);
            isHearted = favoriteVariety.isPresent();
            response.put("favoriteAt", isHearted ? favoriteVariety.get().getFavoriteAt() : null);
        } else {
            // 유효하지 않은 타입이 요청된 경우
            response.put("success", false);
            response.put("message", "유효하지 않은 타입입니다.");
            return ResponseEntity.status(400).body(response); // 400 Bad Request
        }

        // 찜 상태를 성공적으로 확인했을 때의 응답
        response.put("success", true);
        response.put("isHearted", isHearted);  // 찜 상태 여부
        return ResponseEntity.ok(response); // 200 OK
    }
}
