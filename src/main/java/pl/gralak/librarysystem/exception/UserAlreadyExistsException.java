package pl.gralak.librarysystem.exception;

public class UserAlreadyExistsException extends RuntimeException
{
    public UserAlreadyExistsException(String username)
    {
        super("App User with username: " + username + ", already exists");
    }
}
