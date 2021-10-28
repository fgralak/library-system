package pl.gralak.librarysystem.exception;

import pl.gralak.librarysystem.appuser.Provider;

public class UserAlreadyExistsException extends RuntimeException
{
    public UserAlreadyExistsException(String username, Provider provider)
    {
        super("App User with username: " + username + ", provided by: " + provider.name() + ", already exists");
    }
}
