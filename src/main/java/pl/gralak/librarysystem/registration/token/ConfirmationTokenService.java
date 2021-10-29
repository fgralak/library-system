package pl.gralak.librarysystem.registration.token;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenService
{
    private final ConfirmationTokenRepo confirmationTokenRepo;

    public void saveConfirmationToken(ConfirmationToken token)
    {
        confirmationTokenRepo.save(token);
    }

    public Optional<ConfirmationToken> getConfirmationToken(String token)
    {
        return confirmationTokenRepo.findByToken(token);
    }

    public void setConfirmedAt(String token)
    {
        confirmationTokenRepo.updateConfirmedAt(token, LocalDateTime.now());
    }
}
