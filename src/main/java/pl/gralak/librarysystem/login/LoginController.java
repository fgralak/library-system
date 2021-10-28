package pl.gralak.librarysystem.login;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.gralak.librarysystem.appuser.AppUser;
import pl.gralak.librarysystem.appuser.AppUserService;
import pl.gralak.librarysystem.exception.MissingUsernameOrPasswordException;
import pl.gralak.librarysystem.exception.UserAlreadyExistsException;

import static pl.gralak.librarysystem.appuser.Role.ROLE_USER;

@Controller
@RequiredArgsConstructor
public class LoginController
{
    private final AppUserService appUserService;

    @GetMapping("/login")
    public String viewLoginPage()
    {
        return "login";
    }

    @GetMapping("/sign-up")
    public String signUp(Model model)
    {
        model.addAttribute("user", new AppUser());
        return "sign-up";
    }

    @PostMapping("/register")
    public String register(AppUser user, RedirectAttributes redirectAttributes)
    {
        user.setRole(ROLE_USER);
        try{
            appUserService.addLocalUser(user);
        } catch(UserAlreadyExistsException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "App User with username: " + user.getUsername() +
                            ", provided by: " + user.getAuthProvider().name() + ", already exists");
            return "redirect:/sign-up";
        } catch(MissingUsernameOrPasswordException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "Value of username and/or password was null or empty");
            return "redirect:/sign-up";
        }
        return "login";
    }
}

