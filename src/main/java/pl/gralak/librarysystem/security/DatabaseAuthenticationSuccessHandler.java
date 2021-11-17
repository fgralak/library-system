package pl.gralak.librarysystem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pl.gralak.librarysystem.appuser.AppUserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DatabaseAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler
{
    private final AppUserService appUserService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException
    {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        appUserService.updateAuthProvider(username, "local");
        response.sendRedirect("/menu");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
