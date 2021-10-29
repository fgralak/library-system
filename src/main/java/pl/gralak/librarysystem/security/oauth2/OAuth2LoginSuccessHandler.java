package pl.gralak.librarysystem.security.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pl.gralak.librarysystem.appuser.AppUser;
import pl.gralak.librarysystem.appuser.AppUserService;
import pl.gralak.librarysystem.appuser.Provider;
import pl.gralak.librarysystem.exception.UserAlreadyExistsException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static pl.gralak.librarysystem.appuser.Role.ROLE_USER;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler
{
    public final AppUserService appUserService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws ServletException, IOException
    {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String clientName = customOAuth2User.getOAuth2ClientName();
        String email = customOAuth2User.getEmail();

        AppUser user = new AppUser();
        user.setUsername(email);
        user.setRole(ROLE_USER);
        user.setAuthProvider(Provider.valueOf(clientName.toUpperCase()));
        user.setEnabled(true);
        try
        {
            appUserService.addOAuth2User(user);
        } catch(UserAlreadyExistsException e)
        {
            log.info("User already exist in database");
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
