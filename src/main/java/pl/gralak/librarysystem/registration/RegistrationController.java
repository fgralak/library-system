package pl.gralak.librarysystem.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.gralak.librarysystem.appuser.AppUser;
import pl.gralak.librarysystem.exception.MissingUsernameOrPasswordException;
import pl.gralak.librarysystem.exception.UserAlreadyExistsException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/registration")
public class RegistrationController
{
    private final RegistrationService registrationService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/sign-up")
    public String signUp(Model model)
    {
        model.addAttribute("user", new AppUser());
        return "register/sign-up";
    }

    @PostMapping("/register")
    public String register(AppUser user, RedirectAttributes redirectAttributes)
    {
        try{
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            registrationService.register(user);
        } catch(UserAlreadyExistsException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "App User with username: " + user.getUsername() + " already exists");
            return "redirect:/registration/sign-up";
        } catch(MissingUsernameOrPasswordException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "Value of username and/or password was null or empty");
            return "redirect:/registration/sign-up";
        }
        return "register/account-created";
    }

    @GetMapping("/confirm")
    public String confirm(@RequestParam String token)
    {
        registrationService.confirmToken(token);
        return "register/email-confirmed";
    }
}
