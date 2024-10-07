package ttt.test.variety;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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
import ttt.test.drama.Drama;
import ttt.test.favorite.FavoriteRepository;
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
public class VarietyController {

    @Value("${file.upload-dir}")
    private String uploadDir; // 업로드 디렉토리 경로

    @Autowired
    private VarietyService varietyService;

    @Autowired
    private VarietyRepository varietyRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private WatchHistoryRepository watchHistoryRepository; // WatchHistoryRepository 의존성 주입

    @Autowired
    private FavoriteRepository favoriteRepository; // FavoriteService 의존성 주입

    // 예능을 등록하는 메서드
    @PostMapping("/varietyVideo")
    @Transactional
    public String varietyVideo(
            @RequestParam("videoFile") MultipartFile videoFile, // 비디오 파일
            @RequestParam("title") String title, // 제목
            @RequestParam("description") String description, // 설명
            @RequestParam("photoFile") MultipartFile photoFile, // 사진 파일
            RedirectAttributes redirectAttributes) {

        // 필드가 비어있는지 확인
        if (videoFile.isEmpty() || photoFile.isEmpty() || title.isEmpty() || description.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "모든 필드를 채워주세요.");
            return "redirect:/admin/admin_variety_register";
        }

        try {
            // 비디오 파일 저장
            String videoFileName = videoFile.getOriginalFilename();
            Path videoPath = Paths.get(uploadDir, videoFileName);
            Files.write(videoPath, videoFile.getBytes());

            // 사진 파일 저장
            String photoFileName = photoFile.getOriginalFilename();
            Path photoPath = Paths.get(uploadDir, photoFileName);
            Files.write(photoPath, photoFile.getBytes());

            // 예능 엔티티 생성 및 저장
            Variety variety = new Variety(title, description, null, "/uploads/" + photoFileName, "/uploads/" + videoFileName);
            varietyRepository.save(variety);

            redirectAttributes.addFlashAttribute("message", "예능 등록 성공!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "파일 업로드 실패!");
        }

        return "redirect:/admin/varietyList";
    }

    // 등록된 예능 목록을 가져와서 페이지로 보여주는 메서드
    @GetMapping("/admin/varietyList")
    public String getVarietyList(Model model,
                                 @RequestParam(value = "page", defaultValue = "0") int page, // 페이지 번호
                                 @RequestParam(value = "kw", defaultValue = "") String kw) { // 검색어
        Page<Variety> paging = varietyService.getList(page, kw); // 페이지와 검색어로 예능 목록 가져오기

        if (paging.getTotalElements() == 0) {
            return "redirect:/admin"; // 예능이 없으면 admin.html로 리다이렉트
        }
        model.addAttribute("varieties", paging.getContent()); // 현재 페이지의 예능 목록
        model.addAttribute("paging", paging); // 페이지 정보
        model.addAttribute("kw", kw); // 검색어
        return "admin/admin_variety_list";
    }

    // 예능 상세 정보를 보여주는 메서드
    @GetMapping("/admin/admin_variety_details/{varietyNo}")
    public String getVarietyDetails(@PathVariable("varietyNo") int varietyNo, Model model) {
        Optional<Variety> varietyOpt = varietyRepository.findById(varietyNo);
        if (!varietyOpt.isPresent()) {
            System.out.println("Variety with varietyNo " + varietyNo + " not found.");
            return "redirect:/admin";
        }
        Variety variety = varietyOpt.get();
        System.out.println("Variety found: " + variety);
        model.addAttribute("variety", variety);
        return "admin/admin_variety_details";
    }

    // 예능의 메인 정보를 보여주는 메서드
    @GetMapping("/variety/{varietyNo}")
    public String getVarietyMainDetails(@PathVariable("varietyNo") int varietyNo, Model model) {
        Optional<Variety> varietyOpt = varietyRepository.findById(varietyNo);
        if (varietyOpt.isPresent()) {
            Variety variety = varietyOpt.get();
            model.addAttribute("variety", variety);

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

            return "content/variety_content";
        } else {
            return "redirect:/varietyList"; // 예능이 없으면 목록으로 리다이렉트
        }
    }

    // 전체 예능 목록을 가져오는 메서드
    @GetMapping("/variety_list")
    public String getVarietyMainPage(Model model) {
        List<Variety> varieties = varietyService.getAllVarieties(); // 전체 예능 목록 가져오기
        model.addAttribute("varieties", varieties);
        return "content/variety"; // 예능 페이지 반환
    }

    // 예능 수정 페이지를 보여주는 메서드
    @GetMapping("/admin/variety_modify/{varietyNo}")
    public String getVarietyModify(@PathVariable("varietyNo") int varietyNo, Model model) {
        Optional<Variety> varietyOpt = varietyRepository.findById(varietyNo);
        if (!varietyOpt.isPresent()) {
            return "redirect:/admin/varietyList"; // 예능이 존재하지 않으면 목록으로 리다이렉트
        }
        model.addAttribute("variety", varietyOpt.get());
        return "admin/admin_variety_modify"; // 수정 페이지로 이동
    }

    // 예능을 수정하는 메서드
    @PostMapping("/admin/variety_modify/{varietyNo}")
    @Transactional
    public String modifyVariety(
            @PathVariable("varietyNo") int varietyNo,
            @RequestParam("videoFile") MultipartFile videoFile, // 비디오 파일
            @RequestParam("title") String title, // 제목
            @RequestParam("description") String description, // 설명
            @RequestParam("photoFile") MultipartFile photoFile, // 사진 파일
            RedirectAttributes redirectAttributes) {

        Optional<Variety> varietyOpt = varietyRepository.findById(varietyNo);
        if (!varietyOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("message", "예능을 찾을 수 없습니다.");
            return "redirect:/admin/varietyList";
        }

        Variety variety = varietyOpt.get();

        if (videoFile.isEmpty() || photoFile.isEmpty() || title.isEmpty() || description.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "모든 필드를 채워주세요.");
            return "redirect:/admin/variety_modify/" + varietyNo;
        }

        try {
            // 기존 비디오 파일 삭제 후 새 비디오 파일 저장
            if (videoFile != null && !videoFile.isEmpty()) {
                String videoFileName = videoFile.getOriginalFilename();
                Path videoPath = Paths.get(uploadDir, videoFileName);
                Files.write(videoPath, videoFile.getBytes());
                variety.setVideoFileName("/uploads/" + videoFileName);
            }

            // 기존 사진 파일 삭제 후 새 사진 파일 저장
            if (photoFile != null && !photoFile.isEmpty()) {
                String photoFileName = photoFile.getOriginalFilename();
                Path photoPath = Paths.get(uploadDir, photoFileName);
                Files.write(photoPath, photoFile.getBytes());
                variety.setVarietyImg("/uploads/" + photoFileName);
            }

            // 예능 정보 업데이트
            variety.setVarietyName(title);
            variety.setVarietyText(description);

            // 변경 사항 저장
            varietyRepository.save(variety);

            redirectAttributes.addFlashAttribute("message", "예능 수정 성공!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "파일 업로드 실패!");
        }

        return "redirect:/admin/varietyList"; // 수정 후 목록으로 리다이렉트
    }

    // 예능 삭제
    @PostMapping("/admin/variety_delete/{varietyNo}")
    @Transactional
    public String deleteVariety(@PathVariable("varietyNo") int varietyNo, RedirectAttributes redirectAttributes) {
        Optional<Variety> varietyOpt = varietyRepository.findById(varietyNo);
        if (!varietyOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("message", "예능를 찾을 수 없습니다.");
            return "redirect:/admin/varietyList";
        }

        try {

            // 관련된 시청 기록 삭제
            watchHistoryRepository.deleteAllByVarietyNo(varietyNo);

            // 관련된 즐겨찾기 삭제
            favoriteRepository.deleteAllByVarietyNo(varietyNo);


            varietyRepository.deleteById(varietyNo);
            redirectAttributes.addFlashAttribute("message", "예능 삭제 성공!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "예능 삭제 실패!");
        }

        return "redirect:/admin/varietyList"; // 삭제 후 목록으로 리다이렉트
    }
}
