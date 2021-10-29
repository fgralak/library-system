package pl.gralak.librarysystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.gralak.librarysystem.appuser.AppUser;
import pl.gralak.librarysystem.exception.MissingUsernameOrPasswordException;
import pl.gralak.librarysystem.exception.UserAlreadyExistsException;
import pl.gralak.librarysystem.registration.RegistrationRequest;
import pl.gralak.librarysystem.registration.RegistrationService;

@Controller
@RequiredArgsConstructor
public class TemplateController
{
    private final RegistrationService registrationService;

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
        try{
            registrationService.register(new RegistrationRequest(user.getUsername(), user.getPassword()));
        } catch(UserAlreadyExistsException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "App User with username: " + user.getUsername() + " already exists");
            return "redirect:/sign-up";
        } catch(MissingUsernameOrPasswordException e)
        {
            redirectAttributes.addFlashAttribute("error",
                    "Value of username and/or password was null or empty");
            return "redirect:/sign-up";
        }
        return "account-created";
    }


    @GetMapping("/hello")
    public String hello(Model model)
    {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("name", userDetails.getUsername());
        return "hello";
    }
}

