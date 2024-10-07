package ttt.test.user;

import jakarta.validation.Valid;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ttt.test.mypage.WatchHistoryRepository;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    private final WatchHistoryRepository watchHistoryRepository;

    @GetMapping("/login")
    public String login(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (userDetails instanceof SiteUser) {
                model.addAttribute("userId", ((SiteUser) userDetails).getId()); // 사용자 ID를 모델에 추가
            }
        }
        return "login_form"; // 로그인 폼 뷰 반환
    }


    // 회원 가입 페이지로 이동
    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form"; // 회원 가입 폼 뷰 반환
    }

    // 회원 가입 처리
    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form"; // 유효성 검사 실패 시 회원 가입 폼 뷰 반환
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다."); // 비밀번호 불일치 시 에러 메시지 추가
            return "signup_form"; // 회원 가입 폼 뷰 반환
        }

        try {
            // 일반 사용자는 isAdmin을 false로 설정
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1(), false);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다."); // 중복 사용자 시 에러 메시지 추가
            return "signup_form"; // 회원 가입 폼 뷰 반환
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage()); // 일반 에러 발생 시 에러 메시지 추가
            return "signup_form"; // 회원 가입 폼 뷰 반환
        }

        return "redirect:/user/login"; // 회원 가입 성공 후 로그인 페이지로 리다이렉트
    }

    @PostMapping("/withdraw")
    public String withdrawUser(@RequestParam("userId") Long userId, RedirectAttributes redirectAttributes) {
        // 사용자 이름을 사용하여 관련된 watch_history 레코드를 삭제
        String username = userService.findUsernameById(userId); // 사용자 ID로 사용자 이름 가져오기
        watchHistoryRepository.deleteAllByUserName(username); // 사용자와 관련된 watch_history 삭제

        // 사용자 삭제
        userService.deleteUser(userId);

        redirectAttributes.addFlashAttribute("message", "회원 탈퇴가 완료되었습니다."); // 탈퇴 완료 메시지 추가
        return "redirect:/user/login?withdrawn=true"; // 로그인 페이지로 리다이렉트
    }


}
