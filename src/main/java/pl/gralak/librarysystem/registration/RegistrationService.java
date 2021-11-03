package pl.gralak.librarysystem.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gralak.librarysystem.appuser.AppUser;
import pl.gralak.librarysystem.appuser.AppUserService;
import pl.gralak.librarysystem.registration.email.EmailBuilder;
import pl.gralak.librarysystem.registration.email.EmailService;
import pl.gralak.librarysystem.registration.token.ConfirmationToken;
import pl.gralak.librarysystem.registration.token.ConfirmationTokenService;

import java.time.LocalDateTime;

import static pl.gralak.librarysystem.appuser.Provider.LOCAL;
import static pl.gralak.librarysystem.appuser.Role.ROLE_USER;

@Service
@RequiredArgsConstructor
public class RegistrationService
{
    private final AppUserService appUserService;
    private final EmailService emailService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailBuilder emailBuilder;

    public void register(AppUser appUser)
    {
        appUser.setAuthProvider(LOCAL);
        appUser.setRole(ROLE_USER);

        String token = appUserService.addLocalUser(appUser);

        String link = "http://localhost:8080/registration/confirm?token=" + token;
        emailService.send(appUser.getUsername(), emailBuilder.buildEmail(appUser.getUsername(), link));
    }

    @Transactional
    public void confirmToken(String token)
    {
        ConfirmationToken confirmationToken = confirmationTokenService.getConfirmationToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));

        if(confirmationToken.getConfirmedAt() != null)
        {
            throw new IllegalStateException("Email already confirmed");
        }

        LocalDateTime expiresAt = confirmationToken.getExpiresAt();

        if(expiresAt.isBefore(LocalDateTime.now()))
        {
            throw new IllegalStateException("Token already expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(confirmationToken.getAppUser().getUsername());
    }
}
