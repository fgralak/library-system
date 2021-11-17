package pl.gralak.librarysystem.login;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController
{
    @GetMapping("/login")
    public String viewLoginPage()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/menu";
    }

    @GetMapping("/menu")
    public String userMenu(Model model)
    {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("name", userDetails.getUsername());
        if(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
        {
            return "menu/menu-admin";
        } else if(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLOYEE")))
        {
            return "menu/menu-employee";
        } else
        {
            return "menu/menu-user";
        }
    }
}