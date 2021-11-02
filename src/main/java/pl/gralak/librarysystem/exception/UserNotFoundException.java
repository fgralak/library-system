package pl.gralak.librarysystem.exception;

public class UserNotFoundException extends RuntimeException
{
    public UserNotFoundException(String username)
    {
        super("User: " + username + " does not exist");
    }

    public UserNotFoundException(Long id)
    {
        super("There is no such user in database");
    }
}
