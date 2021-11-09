package pl.gralak.librarysystem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import pl.gralak.librarysystem.appuser.AppUser;
import pl.gralak.librarysystem.appuser.AppUserService;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WebSecurity
{
    private final AppUserService appUserService;

    public boolean checkUserAccess(Authentication authentication, Long id)
    {
        Set<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        AppUser appUser = appUserService.getUserById(id);
        String role = appUser.getRole().name();
        if(authorities.contains("ROLE_ADMIN") && (role.equals("ROLE_ADMIN") || role.equals("ROLE_EMPLOYEE") || role.equals("ROLE_USER")))
        {
            return true;
        } else return authorities.contains("ROLE_EMPLOYEE") && role.equals("ROLE_USER");
    }
}
