package ttt.test.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ttt.test.drama.Drama;
import ttt.test.drama.DramaService;
import ttt.test.movie.Movie;
import ttt.test.movie.MovieService;
import ttt.test.user.SiteUser;
import ttt.test.user.UserRepository;
import ttt.test.variety.Variety;
import ttt.test.variety.VarietyService;
import ttt.test.user.UserService;


import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private DramaService dramaService;
    @Autowired
    private MovieService MovieService;
    @Autowired
    private VarietyService varietyService;
    @Autowired
    private UserService UserService;
    @Autowired
    private UserRepository userRepository;


    // 관리자 메인 페이지
    @GetMapping("/admin")
    public String adminPage(Model model) {
        List<Variety> latestVariety = varietyService.getLatestVarieties(5); // 최신 예능 5개 가져오기
        List<Drama> latestDramas = dramaService.getLatestDramas(5); // 최신 드라마 5개 가져오기
        List<Movie> latestMovies = MovieService.getLatestMovies(5); // 최신 영화 5개 가져오기
        List<SiteUser> latestUsers = UserService.getLatestUsers(5); // 최신 회원정보 5개 가져오기
        model.addAttribute("latestDramas", latestDramas);
        model.addAttribute("latestMovies", latestMovies);
        model.addAttribute("latestVariety", latestVariety);
        model.addAttribute("latestUsers", latestUsers);
        return "admin/admin"; // admin.html 반환
    }


    // 관리자 리스트 페이지
    @GetMapping("/admin_list")
    public String adminList(Model model, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5); // 한 페이지에 5개의 항목
        Page<SiteUser> adminPage = UserService.getAdminspaging(pageable);

        // 총 관리자 수를 계산
        long totalAdmins = adminPage.getTotalElements();

        model.addAttribute("adminPage", adminPage);
        model.addAttribute("totalAdmins", totalAdmins); // 총 관리자 수를 모델에 추가

        return "admin/admin_list";
    }




    @GetMapping("/admin_user_list")
    public String adminUser(Model model,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<SiteUser> paging = UserService.getList(page, kw); // UserService 호출 수정

        // 검색된 결과가 없으면 관리자 홈으로 리다이렉트
        if (paging.getTotalElements() == 0) {
            return "redirect:/admin_user_list";
        }

        // 직접 paging 객체에서 사용자 목록 가져오기
        List<SiteUser> users = paging.getContent().stream()
                .filter(user -> !user.getUsername().contains("admin"))
                .collect(Collectors.toList());

        // 총 회원 수 계산 (admin 제외)
        int totalUsersExcludingAdmin = (int) userRepository.countByUsernameNotContaining("admin"); // admin 제외한 수

        model.addAttribute("totalUsers", totalUsersExcludingAdmin);
        model.addAttribute("paging", paging);
        model.addAttribute("users", users); // 사용자 목록 다시 추가
        model.addAttribute("kw", kw);
        return "admin/admin_user_list"; // 반환할 뷰
    }



    // 관리자 페이지 관리자 상세정보 페이지
    // 관리자 세부 정보를 표시하는 경로 추가
    @GetMapping("/admin_detail/{username}")
    public String getUserDetail(@PathVariable String username, Model model) {
        // UserService를 통해 username으로 사용자 정보를 찾음
        SiteUser admin = UserService.getUserByUsername(username);

        // 관리자가 존재하지 않을 경우 admin.html로 리다이렉트
        if (admin == null) {
            return "redirect:/admin"; // admin.html로 리다이렉트
        }

        // 찾은 사용자 정보를 모델에 담아 뷰로 전달
        model.addAttribute("admin", admin);
        return "admin/admin_detail"; // admin_detail.html 파일을 반환
    }


    // 관리자 등록 페이지
    @GetMapping("/admin_register")
    public String AdminRegister() {

        return "admin/admin_register";
    }

    // 관리자 등록 페이지
    @PostMapping("/admin_register")
    public String registerAdmin(String username, String password, String email, Model model) {

        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("message", "중복된 아이디입니다.");
            return "admin/admin_register";  // 중복된 아이디가 있으면 다시 등록 페이지로 이동
        }

        // 이메일 중복 확인
        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("message", "중복된 이메일입니다.");
            return "admin/admin_register";  // 중복된 이메일이 있으면 다시 등록 페이지로 이동
        }

        // username에 "admin" 포함 여부 검증
        if (!username.contains("admin")) {
            model.addAttribute("message", "허용되지 않은 아이디입니다.");
            return "admin/admin_register";  // 다시 등록 페이지로 이동
        }

        // 새로운 관리자 생성
        SiteUser admin = new SiteUser();
        admin.setUsername(username);
        admin.setPassword(password);  // 비밀번호를 평문으로 저장
        admin.setEmail(email);
        admin.setMembership("프리미엄");  // 관리자의 멤버십은 항상 프리미엄
        admin.setRegisterDate(new Date());

        // ROLE_ADMIN 권한 부여
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");
        admin.setRoles(roles);

        // 관리자 저장
        userRepository.save(admin);

        model.addAttribute("message", "관리자 등록이 완료되었습니다.");
        return "redirect:/admin_list";  // 성공 시 관리자 홈으로 리다이렉트
    }

    @GetMapping("/check_username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean isAvailable = !userRepository.findByUsername(username).isPresent();
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", isAvailable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check_email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean isAvailable = !userRepository.findByEmail(email).isPresent();
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", isAvailable);
        return ResponseEntity.ok(response);
    }


    // 관리자 삭제
    @PostMapping("/admin/delete_admin/{username}")
    public String deleteAdmin(@PathVariable String username, RedirectAttributes redirectAttributes) {
        SiteUser admin = UserService.getUserByUsername(username);  // 서비스 호출
        if (admin != null) {
            UserService.deleteUser(admin.getId());  // 삭제 처리
            redirectAttributes.addFlashAttribute("message", "관리자가 삭제되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("message", "관리자를 찾을 수 없습니다.");
        }
        return "redirect:/admin_list";  // 성공 시 리다이렉트
    }



    @GetMapping("/admin_drama_register")
    public String AdminDramaRegister() {
        return "admin/admin_drama_register";
    }


    @GetMapping("/admin_variety_register")
    public String AdminVarietyRegister() {
        return "admin/admin_variety_register";
    }


}


