package pl.gralak.librarysystem.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/registration")
public class RegistrationController
{
    private final RegistrationService registrationService;

    @PostMapping
    public String register(@RequestBody RegistrationRequest request)
    {
        return registrationService.register(request);
    }

    @GetMapping("/confirm")
    public String confirm(@RequestParam String token)
    {
        return registrationService.confirmToken(token);
    }
}
