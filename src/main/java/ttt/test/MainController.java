package ttt.test;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ttt.test.drama.DramaService;
import ttt.test.movie.MovieService;
import ttt.test.variety.VarietyService;
import ttt.test.user.SiteUser;
import ttt.test.user.UserRepository;
import ttt.test.user.UserCreateForm;

@Controller
public class MainController {

    @Autowired
    private DramaService dramaService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private VarietyService varietyService;

    @Autowired
    private UserRepository userRepository;


    @GetMapping({"/", "/main"})
    public String main(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/user/login";
        }

        String username = authentication.getName();
        SiteUser user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("dramas", dramaService.getMainDramas());
        model.addAttribute("movies", movieService.getMainMovies());
        model.addAttribute("varieties", varietyService.getMainVarieties());

        return "main";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }
}
