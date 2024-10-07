package ttt.test.movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ttt.test.favorite.Favorite;
import ttt.test.favorite.FavoriteRepository;
import ttt.test.mypage.WatchHistory;
import ttt.test.mypage.WatchHistoryRepository;
import ttt.test.user.SiteUser;
import ttt.test.user.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
public class MovieController {

    @Value("${file.upload-dir}")
    private String uploadDir; // 파일 업로드 디렉토리 경로

    @Autowired
    private MovieRepository movieRepository; // 영화 데이터 접근 레포지토리

    @Autowired
    private WatchHistoryRepository watchHistoryRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private MovieService movieService; // 영화 서비스 클래스

    @Autowired
    private UserService userService; // 사용자 서비스 클래스

    // 영화 비디오와 사진을 업로드하고 저장하는 메서드
    @PostMapping("/uploadMovieVideo")
    @Transactional
    public String uploadMovieVideo(
            @RequestParam("movie_player") MultipartFile MovieFile, // 비디오 파일
            @RequestParam("movie_title") String MovieTitle, // 영화 제목
            @RequestParam("movie_country") String Moviegenre, // 영화 장르
            @RequestParam("movie_content") String MovieContent, // 영화 내용
            @RequestParam("movie_photo") MultipartFile MoviePhoto, // 사진 파일
            RedirectAttributes redirectAttributes) {

        // 필드가 비어있는지 확인
        if (MovieFile.isEmpty() || MovieTitle.isEmpty() || Moviegenre.isEmpty() || MovieContent.isEmpty() || MoviePhoto.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "모든 필드를 채워주세요.");
            return "redirect:/admin/admin_movie_register"; // 오류 시 영화 등록 페이지로 이동
        }

        try {
            // 비디오 파일 저장
            String videoFileName = MovieFile.getOriginalFilename();
            Path videoPath = Paths.get(uploadDir, videoFileName);
            Files.write(videoPath, MovieFile.getBytes());

            // 사진 파일 저장
            String photoFileName = MoviePhoto.getOriginalFilename();
            Path photoPath = Paths.get(uploadDir, photoFileName);
            Files.write(photoPath, MoviePhoto.getBytes());

            // 영화 객체 생성 및 저장
            Movie movie = new Movie(MovieTitle, MovieContent, Moviegenre, null, "/uploads/" + photoFileName, "/uploads/" + videoFileName);
            movieRepository.save(movie);

            redirectAttributes.addFlashAttribute("message", "영화 등록 성공!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "파일 업로드 실패!");
        }

        return "redirect:/admin/MovieList"; // 성공 시 영화 목록 페이지로 이동
    }

    // 영화 목록 페이지를 가져오는 메서드
    @GetMapping("/admin/MovieList")
    public String getMovieList(Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page, // 페이지 번호
                               @RequestParam(value = "kw", defaultValue = "") String kw) { // 검색어
        Page<Movie> paging = movieService.getList(page, kw); // 페이지와 검색어로 영화 목록 조회

        if (paging.getTotalElements() == 0) {
            return "redirect:/admin"; // 영화가 없으면 admin.html로 리다이렉트
        }

        model.addAttribute("movies", paging.getContent()); // 현재 페이지의 영화 목록
        model.addAttribute("paging", paging); // 페이징 정보
        model.addAttribute("kw", kw); // 검색어
        return "admin/admin_movie_list"; // 영화 목록 페이지 반환
    }

    // 영화 등록 페이지를 보여주는 메서드
    @GetMapping("/admin/admin_movie_register")
    public String showMovieRegisterPage(Model model) {
        return "admin/admin_movie_register"; // 영화 등록 페이지 뷰 반환
    }

    // 영화 상세 페이지를 보여주는 메서드
    @GetMapping("/admin/admin_movie_details/{movieNo}")
    public String getMovieDetails(@PathVariable("movieNo") int movieNo, Model model) {
        Optional<Movie> movieOpt = movieRepository.findById(movieNo);
        if (!movieOpt.isPresent()) {
            System.out.println("Movie with movieNo " + movieNo + " not found.");
            return "redirect:/admin"; // 영화가 없으면 목록으로 이동
        }
        Movie movie = movieOpt.get();
        System.out.println("Movie found: " + movie);
        model.addAttribute("movie", movie); // 영화 객체를 모델에 추가
        return "admin/admin_movie_details"; // 영화 상세 페이지 반환
    }

    // 영화의 메인 상세 페이지를 보여주는 메서드
    @GetMapping("/movie/{movieNo}")
    public String getMovieMainDetails(@PathVariable("movieNo") int movieNo, Model model) {
        Optional<Movie> movieOpt = movieRepository.findById(movieNo);
        if (movieOpt.isPresent()) {
            Movie movie = movieOpt.get();
            model.addAttribute("movie", movie); // 영화 객체를 모델에 추가

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
                String username = ((UserDetails) authentication.getPrincipal()).getUsername();
                SiteUser user = userService.getUserByUsername(username);

                // 유효한 구독 여부 확인
                String membership = userService.getUserByUsername(username).getMembership();

                // 디버깅 로그 추가
                System.out.println("username: " + username);
                System.out.println("membership: " + membership);

                model.addAttribute("isSubscriptionValid", membership != null ? membership : "None");
                model.addAttribute("user", user); // 사용자 정보 추가
            } else {
                model.addAttribute("isSubscriptionValid", "None");
                System.out.println("No principal or not authenticated, Subscription Valid: None");
            }

            return "content/movie_content"; // 영화 콘텐츠 페이지 반환
        } else {
            return "redirect:/movieList"; // 영화가 없으면 목록으로 리다이렉트
        }
    }

    // 메인 영화 목록 페이지를 가져오는 메서드
    @GetMapping("/movie_list")  // URL 매핑을 /movieList로 변경
    public String getMovieMainPage(Model model) {
        List<Movie> movies = movieService.getAllMovies(); // 전체 영화 리스트 가져오기
        model.addAttribute("movies", movies); // 영화 목록을 모델에 추가
        return "content/movie"; // 영화 페이지 반환
    }

    // 영화 수정 페이지를 보여주는 메서드
    @GetMapping("/admin/movie_modify/{movieNo}") // 영화 번호를 URL로 받도록 수정
    public String getMovieModify(@PathVariable("movieNo") int movieNo, Model model) {
        Optional<Movie> movieOpt = movieRepository.findById(movieNo);
        if (!movieOpt.isPresent()) {
            return "redirect:/admin/movieList"; // 영화가 없으면 목록으로 이동
        }
        model.addAttribute("movie", movieOpt.get()); // 영화 객체를 모델에 추가
        return "admin/admin_movie_modify"; // 영화 수정 페이지 반환
    }

    // 영화 정보를 수정하는 메서드
    @PostMapping("/admin/movie_modify/{movieNo}")
    @Transactional
    public String modifyMovie(
            @PathVariable("movieNo") int movieNo,
            @RequestParam("movie_player") MultipartFile MovieFile, // 비디오 파일
            @RequestParam("movie_title") String MovieTitle, // 영화 제목
            @RequestParam("movie_country") String Moviegenre, // 영화 장르
            @RequestParam("movie_content") String MovieContent, // 영화 내용
            @RequestParam("movie_photo") MultipartFile MoviePhoto, // 사진 파일
            RedirectAttributes redirectAttributes) {

        Optional<Movie> movieOpt = movieRepository.findById(movieNo);
        if (!movieOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("message", "영화를 찾을 수 없습니다.");
            return "redirect:/admin/MovieList"; // 영화가 없으면 목록으로 이동
        }

        Movie movie = movieOpt.get();

        if (MovieFile.isEmpty() || MoviePhoto.isEmpty() || MovieTitle.isEmpty() || Moviegenre.isEmpty() || MovieContent.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "모든 필드를 채워주세요.");
            return "redirect:/admin/movie_modify/" + movieNo; // 오류 시 수정 페이지로 리다이렉트
        }

        try {
            // 기존 비디오 파일 삭제 후 새 비디오 파일 저장
            if (MovieFile != null && !MovieFile.isEmpty()) {
                String videoFileName = MovieFile.getOriginalFilename();
                Path videoPath = Paths.get(uploadDir, videoFileName);
                Files.write(videoPath, MovieFile.getBytes());
                movie.setVideoFileName("/uploads/" + videoFileName);
            }

            // 기존 사진 파일 삭제 후 새 사진 파일 저장
            if (MoviePhoto != null && !MoviePhoto.isEmpty()) {
                String photoFileName = MoviePhoto.getOriginalFilename();
                Path photoPath = Paths.get(uploadDir, photoFileName);
                Files.write(photoPath, MoviePhoto.getBytes());
                movie.setMovieImg("/uploads/" + photoFileName);
            }

            // 영화 정보 업데이트
            movie.setMovieName(MovieTitle);
            movie.setMovieGenre(Moviegenre);
            movie.setMovieText(MovieContent);

            // 변경 사항 저장
            movieRepository.save(movie);

            redirectAttributes.addFlashAttribute("message", "영화 수정 성공!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "파일 업로드 실패!");
        }

        return "redirect:/admin/MovieList"; // 수정 후 목록으로 리다이렉트
    }

    @PostMapping("/admin/movie_delete/{movieNo}")
    @Transactional
    public String deleteMovie(@PathVariable("movieNo") int movieNo, RedirectAttributes redirectAttributes) {
        Optional<Movie> movieOpt = movieRepository.findById(movieNo);
        if (!movieOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("message", "영화를 찾을 수 없습니다.");
            return "redirect:/admin/MovieList"; // 영화가 없으면 목록으로 리다이렉트
        }

        try {
            // 관련된 시청 기록 삭제
            watchHistoryRepository.deleteAllByMovieNo(movieNo);

            // 관련된 즐겨찾기 삭제
            favoriteRepository.deleteAllByMovieNo(movieNo);


            // 영화 삭제
            movieRepository.deleteById(movieNo);
            redirectAttributes.addFlashAttribute("message", "영화 삭제 성공!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "영화 삭제 실패!");
        }

        return "redirect:/admin/MovieList"; // 삭제 후 목록으로 리다이렉트
    }


    // 영화 필터링 메서드
    @GetMapping("/movie/filter")
    public String filterMovies(@RequestParam(required = false, defaultValue = "전체") String category, Model model) {
        List<Movie> movies;

        if (category.equals("전체")) {
            movies = movieService.getAllMovies(); // 전체 영화 반환
        } else {
            movies = movieService.findMoviesByCategory(category); // 선택한 장르에 따라 영화 리스트 반환
        }

        model.addAttribute("movies", movies); // 필터링된 영화 목록을 모델에 추가
        return "content/movie"; // 영화 목록을 보여줄 템플릿 반환
    }
}
