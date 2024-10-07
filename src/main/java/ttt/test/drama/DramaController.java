package ttt.test.drama;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ttt.test.favorite.FavoriteRepository;
import ttt.test.favorite.FavoriteService;
import ttt.test.mypage.WatchHistory;
import ttt.test.mypage.WatchHistoryRepository;
import ttt.test.user.SiteUser;
import ttt.test.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class DramaController {

    @Value("${file.upload-dir}")
    private String uploadDir; // 파일 업로드 디렉토리 경로

    @Autowired
    private DramaRepository dramaRepository; // DramaRepository 의존성 주입

    @Autowired
    private DramaService dramaService; // DramaService 의존성 주입

    @Autowired
    private UserService userService; // UserService 의존성 주입

    @Autowired
    private WatchHistoryRepository watchHistoryRepository; // WatchHistoryRepository 의존성 주입

    @Autowired
    private FavoriteService favoriteService; // FavoriteService 의존성 주입

    @Autowired
    private FavoriteRepository favoriteRepository; // FavoriteService 의존성 주입

    // 드라마 등록 처리
    @PostMapping("/uploadVideo")
    @Transactional
    public String uploadVideo(
            @RequestParam("videoFile") MultipartFile videoFile,
            @RequestParam("title") String title,
            @RequestParam("genre") String genre,
            @RequestParam("description") String description,
            @RequestParam("photoFile") MultipartFile photoFile,
            RedirectAttributes redirectAttributes) {

        if (videoFile.isEmpty() || photoFile.isEmpty() || title.isEmpty() || genre.isEmpty() || description.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "모든 필드를 채워주세요.");
            return "redirect:/admin/admin_drama_register"; // 필드가 비어있으면 등록 페이지로 리다이렉트
        }

        try {
            // 비디오 및 사진 파일 저장
            String videoFileName = videoFile.getOriginalFilename();
            Path videoPath = Paths.get(uploadDir, videoFileName);
            Files.write(videoPath, videoFile.getBytes());

            String photoFileName = photoFile.getOriginalFilename();
            Path photoPath = Paths.get(uploadDir, photoFileName);
            Files.write(photoPath, photoFile.getBytes());

            // 드라마 엔티티 생성 및 저장
            Drama drama = new Drama(title, description, genre, null, "/uploads/" + photoFileName, "/uploads/" + videoFileName);
            dramaRepository.save(drama);

            redirectAttributes.addFlashAttribute("message", "드라마 등록 성공!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "파일 업로드 실패!");
        }

        return "redirect:/admin/dramaList"; // 등록 후 드라마 리스트 페이지로 리다이렉트
    }

    // 등록한 데이터 받아와서 리스트 페이지로 띄우기
    @GetMapping("/admin/dramaList")
    public String getDramaList(Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Drama> paging = dramaService.getList(page, kw); // 페이지와 검색어로 검색
        if (paging.getTotalElements() == 0) {
            return "redirect:/admin"; // 드라마가 없으면 admin.html로 리다이렉트
        }
        model.addAttribute("dramas", paging.getContent()); // 현재 페이지의 드라마 리스트
        model.addAttribute("paging", paging); // 페이징 정보 추가
        model.addAttribute("kw", kw); // 검색어 추가
        return "admin/admin_drama_list"; // admin_drama_list.html 반환
    }


    // 드라마 상세 페이지
    @GetMapping("/admin/admin_drama_details/{dramaNo}")
    public String getDramaDetails(@PathVariable("dramaNo") int dramaNo, Model model) {
        Optional<Drama> dramaOpt = dramaRepository.findById(dramaNo);
        if (!dramaOpt.isPresent()) {
            System.out.println("Drama with dramaNo " + dramaNo + " not found.");
            return "redirect:/admin"; // 드라마가 없으면 admin.html로 리다이렉트
        }
        Drama drama = dramaOpt.get();
        System.out.println("Drama found: " + drama);
        model.addAttribute("drama", drama);
        return "admin/admin_drama_details"; // 드라마 상세 페이지 반환
    }


    @GetMapping("/drama/{dramaNo}")
    public String getDramaMainDetails(@PathVariable("dramaNo") int dramaNo,
                                      Model model,
                                      HttpServletRequest request) { // HttpServletRequest 추가
        Optional<Drama> dramaOpt = dramaRepository.findById(dramaNo);
        if (dramaOpt.isPresent()) {
            Drama drama = dramaOpt.get();
            model.addAttribute("drama", drama);

            // Referer 체크
            String referer = request.getHeader("Referer");
            if (referer != null && referer.contains("/search_results")) {
                // 검색 결과에서 왔을 경우
                model.addAttribute("fromSearchResults", true); // 플래그를 모델에 추가
            }

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
                model.addAttribute("user", user);
            } else {
                model.addAttribute("isSubscriptionValid", "None");
                System.out.println("No principal or not authenticated, Subscription Valid: None");
            }

            return "content/drama_content"; // 드라마 상세 페이지 반환
        } else {
            return "redirect:/dramaList"; // 드라마가 없으면 드라마 리스트 페이지로 리다이렉트
        }
    }


    // 드라마 리스트 페이지
    @GetMapping("/drama_list") // URL 매핑을 /drama_list로 변경
    public String getDramaMainPage(Model model) {
        List<Drama> dramas = dramaService.getAllDramas(); // 전체 드라마 리스트 가져오기
        model.addAttribute("dramas", dramas);
        return "content/drama"; // drama.html 반환
    }

    // 드라마 수정 페이지
    @GetMapping("/admin/drama_modify/{dramaNo}") // 드라마 번호를 URL로 받도록 수정
    public String getDramaModify(@PathVariable("dramaNo") int dramaNo, Model model) {
        Optional<Drama> dramaOpt = dramaRepository.findById(dramaNo);
        if (!dramaOpt.isPresent()) {
            return "redirect:/admin/dramaList"; // 드라마가 존재하지 않으면 리스트로 리다이렉트
        }
        model.addAttribute("drama", dramaOpt.get());
        return "admin/admin_drama_modify"; // 수정 페이지로 이동
    }

    // 드라마 수정 처리
    @PostMapping("/admin/drama_modify/{dramaNo}")
    @Transactional
    public String modifyDrama(
            @PathVariable("dramaNo") int dramaNo,
            @RequestParam("videoFile") MultipartFile videoFile,
            @RequestParam("title") String title,
            @RequestParam("genre") String genre,
            @RequestParam("description") String description,
            @RequestParam("photoFile") MultipartFile photoFile,
            RedirectAttributes redirectAttributes) {

        Optional<Drama> dramaOpt = dramaRepository.findById(dramaNo);
        if (!dramaOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("message", "드라마를 찾을 수 없습니다.");
            return "redirect:/admin/dramaList"; // 드라마가 존재하지 않으면 리스트로 리다이렉트
        }

        Drama drama = dramaOpt.get();

        if (videoFile.isEmpty() || photoFile.isEmpty() || title.isEmpty() || genre.isEmpty() || description.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "모든 필드를 채워주세요.");
            return "redirect:/admin/drama_modify/" + dramaNo; // 필드가 비어있으면 수정 페이지로 리다이렉트
        }

        try {
            // 기존 비디오 및 사진 파일 삭제 후 새 파일 저장
            if (videoFile != null && !videoFile.isEmpty()) {
                String videoFileName = videoFile.getOriginalFilename();
                Path videoPath = Paths.get(uploadDir, videoFileName);
                Files.write(videoPath, videoFile.getBytes());
                drama.setVideoFileName("/uploads/" + videoFileName);
            }

            if (photoFile != null && !photoFile.isEmpty()) {
                String photoFileName = photoFile.getOriginalFilename();
                Path photoPath = Paths.get(uploadDir, photoFileName);
                Files.write(photoPath, photoFile.getBytes());
                drama.setDramaImg("/uploads/" + photoFileName);
            }

            // 드라마 정보 업데이트
            drama.setDramaName(title);
            drama.setDramaGenre(genre);
            drama.setDramaText(description);

            // 변경 사항 저장
            dramaRepository.save(drama);

            redirectAttributes.addFlashAttribute("message", "드라마 수정 성공!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "파일 업로드 실패!");
        }

        return "redirect:/admin/dramaList"; // 수정 후 목록으로 리다이렉트
    }

    // 드라마 삭제 처리
    @PostMapping("/admin/drama_delete/{dramaNo}")
    @Transactional
    public String deleteDrama(@PathVariable("dramaNo") int dramaNo, RedirectAttributes redirectAttributes) {
        Optional<Drama> dramaOpt = dramaRepository.findById(dramaNo);
        if (!dramaOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("message", "드라마를 찾을 수 없습니다.");
            return "redirect:/admin/dramaList"; // 드라마가 존재하지 않으면 리스트로 리다이렉트
        }

        try {
            // 관련된 시청 기록 삭제
            watchHistoryRepository.deleteAllByDramaNo(dramaNo);

            // 관련된 즐겨찾기 삭제
            favoriteRepository.deleteAllByDramaNo(dramaNo);


            // 드라마 삭제
            dramaRepository.deleteById(dramaNo);
            redirectAttributes.addFlashAttribute("message", "드라마 삭제 성공!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "드라마 삭제 실패!");
        }

        return "redirect:/admin/dramaList"; // 삭제 후 목록으로 리다이렉트
    }

    // 드라마 필터링 처리
    @GetMapping("/drama/filter")
    public String filterDramas(@RequestParam(required = false, defaultValue = "전체") String category, Model model) {
        List<Drama> dramas;

        if (category.equals("전체")) {
            dramas = dramaService.getAllDramas(); // 전체 드라마 반환
        } else {
            dramas = dramaService.findDramasByCategory(category); // 선택한 장르에 따라 드라마 리스트 반환
        }

        model.addAttribute("dramas", dramas); // 필터링된 드라마 목록을 모델에 추가
        return "content/drama"; // 드라마 목록을 보여줄 템플릿 반환
    }

    // 드라마 시청 기록 추가
    @PostMapping("/drama/{dramaNo}/uploads/{videoFileName}")
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public String historyDrama(@PathVariable("dramaNo") int dramaNo) {
        Optional<Drama> dramaOpt = dramaRepository.findById(dramaNo);

        if (dramaOpt.isPresent()) {
            Drama drama = dramaOpt.get();

            WatchHistory watchHistory = new WatchHistory();
            watchHistory.setDrama(drama); // Drama 객체를 WatchHistory에 설정

            watchHistoryRepository.save(watchHistory); // WatchHistory 저장
        } else {
            System.out.println("드라마 정보를 찾을 수 없습니다.");
        }

        return "mypage/my_watch"; // 시청 기록 페이지로 리다이렉트
    }
}
