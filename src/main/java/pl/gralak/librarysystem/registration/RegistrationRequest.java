package pl.gralak.librarysystem.registration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationRequest
{
    private String username;
    private String password;
}
