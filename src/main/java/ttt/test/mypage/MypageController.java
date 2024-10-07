package ttt.test.mypage;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import ttt.test.drama.Drama;
import ttt.test.favorite.Favorite;
import ttt.test.favorite.FavoriteService;
import ttt.test.movie.Movie;
import ttt.test.user.SiteUser;
import ttt.test.user.UserRepository;
import ttt.test.user.UserService;
import ttt.test.variety.Variety;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class MypageController {

    @Autowired
    private UserRepository userRepository; // 사용자 데이터 접근을 위한 레포지토리

    @Autowired
    private UserService userService; // 사용자 관련 서비스

    @Autowired
    private HttpSession session; // 세션을 통한 메시지 저장

    @Autowired
    private WatchHistoryService watchHistoryService; // 시청 기록을 위한 서비스

    @Autowired
    private FavoriteService favoriteService;

    // 사용자 메인 페이지
    @GetMapping("/mypage")
    public String mypage(@RequestParam(value = "userId", required = false) Long userId,
                         Model model, HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증되지 않은 경우 로그인 페이지로 리다이렉트
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/user/login";
        }

        String username = authentication.getName();
        SiteUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // userId 파라미터가 있으면 해당 사용자 정보로 덮어씀
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        }

        model.addAttribute("username", username);
        model.addAttribute("membership", user.getMembership());

        // 최신 시청 기록 가져오기
        List<WatchHistory> latestWatchHistory = watchHistoryService.getLatestWatchHistory(username);
        model.addAttribute("latestWatchHistory", latestWatchHistory);

        // 최신 관심 영화 6개 가져오기
        List<Favorite> latestFavoriteMovies = favoriteService.getLatestFavoriteMovies(username);
        model.addAttribute("latestFavoriteMovies", latestFavoriteMovies);

        List<Favorite> latestFavoritePrograms = favoriteService.getLatestFavoritePrograms(username);
        List<Map<String, Object>> favoriteProgramDetails = new ArrayList<>();

        for (Favorite favorite : latestFavoritePrograms) {
            Map<String, Object> programDetails = new HashMap<>();

            if (favorite.getDramaNo() != null) { // dramaNo 확인
                programDetails.put("url", "/drama/" + favorite.getDramaNo().getDramaNo());
                programDetails.put("image", favorite.getDramaNo().getDramaImg());
                programDetails.put("name", favorite.getDramaNo().getDramaName());
            } else if (favorite.getVarietyNo() != null) { // varietyNo 확인
                programDetails.put("url", "/variety/" + favorite.getVarietyNo().getVarietyNo());
                programDetails.put("image", favorite.getVarietyNo().getVarietyImg());
                programDetails.put("name", favorite.getVarietyNo().getVarietyName());
            } else {
                programDetails.put("url", "#");
                programDetails.put("image", "default-image.jpg");
                programDetails.put("name", "프로그램 정보 없음");
            }

            favoriteProgramDetails.add(programDetails);
        }

        model.addAttribute("latestFavoritePrograms", favoriteProgramDetails);


        // 세션 메시지 처리
        String message = (String) session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message");
        }

        return "mypage/mypage"; // 실제 사용하는 템플릿 이름으로 변경
    }

    // 사용자 정보 수정 페이지
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user_modify")
    public String userModify(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // 현재 사용자 이름

        // UserRepository를 사용하여 사용자 정보 조회
        Optional<SiteUser> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            SiteUser siteUser = optionalUser.get(); // SiteUser 객체 가져오기

            String email = siteUser.getEmail();

            // 모델에 사용자 정보 추가
            model.addAttribute("user", siteUser); // SiteUser 객체를 모델에 추가
            model.addAttribute("username", username);
            model.addAttribute("email", email); // 이메일 추가
            model.addAttribute("membership", siteUser.getMembership());
        } else {
            // 사용자 정보를 찾을 수 없는 경우 적절한 처리 (예: 예외 처리, 에러 메시지 등)
            return "redirect:/user/login"; // 에러 페이지로 리디렉션
        }

        return "mypage/user_modify";
    }

    // 결제 정보 페이지로 이동
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mypage/credit_info")
    public String showCreditInfo(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // 현재 사용자 이름

        SiteUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 결제 금액을 정수형 문자열로 포맷
        String formattedPaymentAmount = user.getPaymentAmount() != null
                ? user.getPaymentAmount().toBigInteger().toString()
                : "0";

        // 결제 일자를 원하는 형식으로 포맷
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedPaymentDate = user.getPaymentDate() != null
                ? dateFormat.format(user.getPaymentDate())
                : "없음";

        // 모델에 사용자 정보 추가
        model.addAttribute("username", username);
        model.addAttribute("membership", user.getMembership());
        model.addAttribute("paymentAmount", formattedPaymentAmount); // 포맷된 결제 금액 추가
        model.addAttribute("paymentDate", formattedPaymentDate); // 포맷된 결제 일자 추가
        model.addAttribute("message", "결제가 완료되었습니다!"); // 결제 완료 메시지 추가

        return "mypage/credit_info"; // credit_info.html 반환
    }

    // 이용권 업데이트
    @PostMapping("/membership/update")
    public String updateMembership(@RequestParam("membership") String membership, RedirectAttributes redirectAttributes) {
        // 현재 로그인된 사용자 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 사용자 찾기
        SiteUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 현재 이용 중인 이용권
        String currentMembership = user.getMembership();

        // 현재 이용 중인 이용권과 동일한 경우 에러 메시지 추가
        if (currentMembership != null && currentMembership.equals(membership)) {
            redirectAttributes.addFlashAttribute("error", "이미 이용 중인 이용권과 동일한 이용권을 선택하셨습니다.");
            return "redirect:/mypage/mypage"; // 결제 정보 페이지로 리다이렉트
        }

        // 이용권 금액 맵
        Map<String, BigDecimal> membershipAmounts = Map.of(
                "베이직", new BigDecimal("5900"),
                "스탠다드", new BigDecimal("8000"),
                "프리미엄", new BigDecimal("13000")
        );

        // 결제 금액 및 결제 날짜 설정
        BigDecimal paymentAmount = membershipAmounts.getOrDefault(membership, BigDecimal.ZERO);
        user.setPaymentAmount(paymentAmount);
        user.setPaymentDate(new Date()); // 현재 날짜 설정

        // 이용권 업데이트
        user.setMembership(membership);
        userRepository.save(user);

        // 결제 완료 페이지로 리다이렉트
        return "redirect:/mypage/credit_info";
    }

    // 회원권 취소
    @PostMapping("/cancel-membership")
    public RedirectView cancelMembership(@RequestParam("userId") Long userId) {
        userService.cancelMembership(userId);
        return new RedirectView("/mypage?userId=" + userId); // userId를 쿼리 파라미터로 추가
    }

    // 비밀번호 수정
    @PostMapping("/user/update-password")
    @PreAuthorize("isAuthenticated()")
    public String updatePassword(@RequestParam("current_password") String currentPassword,
                                 @RequestParam("new_password") String newPassword,
                                 @RequestParam("confirm_new_password") String confirmNewPassword,
                                 Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 사용자 정보 가져오기
        SiteUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 현재 비밀번호 확인
        if (!currentPassword.equals(user.getPassword())) {
            model.addAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
            model.addAttribute("username", username);
            model.addAttribute("email", user.getEmail());
            model.addAttribute("membership", user.getMembership());
            return "mypage/user_modify";
        }

        // 새 비밀번호와 비밀번호 확인 일치 여부 확인
        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("error", "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            model.addAttribute("username", username);
            model.addAttribute("email", user.getEmail());
            model.addAttribute("membership", user.getMembership());
            return "mypage/user_modify";
        }

        // 새 비밀번호 저장
        user.setPassword(newPassword);
        userRepository.save(user);

        return "redirect:/mypage";
    }

    @GetMapping("/my_program")
    public String MyProgram(Principal principal,
                            @RequestParam(name = "dramaNo", required = false) Drama dramaNo,
                            @RequestParam(name = "varietyNo", required = false) Variety varietyNo,
                            Model model) {
        // username만 받아오기
        String username = principal.getName();

        // 디버깅 로그 추가
        System.out.println("username: " + username);

        // username을 모델에 추가
        model.addAttribute("username", username);

        // 기존 코드 유지
        List<Favorite> favoriteList = favoriteService.getFavoriteByUsername(username, dramaNo, varietyNo);
        model.addAttribute("favoriteList", favoriteList);

        return "mypage/my_program";
    }



    @GetMapping("/my_movie")
    public String MyMovie(Principal principal,
                          @RequestParam(name = "movieNo", required = false) Movie movieNo,
                          Model model) {
        // username만 받아오기
        String username = principal.getName();

        // 디버깅 로그 추가
        System.out.println("username: " + username);

        // username을 모델에 추가
        model.addAttribute("username", username);

        // 모든 찜 목록 가져오기
        List<Favorite> favoriteList = favoriteService.getFavoriteByUsername(username, movieNo);
        model.addAttribute("favoriteList", favoriteList);

        return "mypage/my_movie";
    }
    @PostMapping("/my_movie/delete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteFavorites(@RequestBody List<Integer> movieNos) {
        try {
            System.out.println("받은 ID 목록: " + movieNos); // 받은 데이터 콘솔 출력
            if (movieNos == null || movieNos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "잘못된 요청: ID 목록이 비어 있습니다."));
            }

            favoriteService.deleteFavoriteMoviesByIds(movieNos);
            return ResponseEntity.ok(Map.of("message", "삭제 성공"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "삭제에 실패했습니다."));
        }
    }

    @PostMapping("/my_program/delete")
    public ResponseEntity<?> removeFavorites(@RequestBody Map<String, List<Integer>> favoriteIds) {
        try {
            List<Integer> dramaNos = favoriteIds.get("dramaNos"); // 드라마 ID 목록
            List<Integer> varietyNos = favoriteIds.get("varietyNos"); // 예능 ID 목록

            // 삭제할 항목이 없을 경우 처리
            if ((dramaNos == null || dramaNos.isEmpty()) &&
                    (varietyNos == null || varietyNos.isEmpty())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 요청: 삭제할 ID 목록이 비어 있습니다.");
            }

            // 드라마 삭제
            if (dramaNos != null && !dramaNos.isEmpty()) {
                favoriteService.deleteFavoriteDramasByIds(dramaNos);
            }

            // 예능 삭제
            if (varietyNos != null && !varietyNos.isEmpty()) {
                favoriteService.deleteFavoriteVarietiesByIds(varietyNos);
            }

            return ResponseEntity.ok().body(Map.of("message", "삭제 성공")); // 수정된 부분
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패");
        }
    }







    // 구독 유효성 검사
    public boolean isSubscriptionValid(String username) {
        Optional<SiteUser> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            SiteUser user = userOpt.get();
            String membership = user.getMembership();

            // 디버깅 로그
            System.out.println("username: " + username);
            System.out.println("membership: " + membership);

            // 유효한 이용권 처리
            if ("베이직".equals(membership) || "스탠다드".equals(membership) || "프리미엄".equals(membership)) {
                return true;
            }
        } else {
            System.out.println("사용자를 찾을 수 없습니다: " + username);
        }

        return false;
    }
}
