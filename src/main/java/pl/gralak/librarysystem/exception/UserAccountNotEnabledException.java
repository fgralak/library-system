package pl.gralak.librarysystem.exception;

public class UserAccountNotEnabledException extends RuntimeException
{
    public UserAccountNotEnabledException(String username)
    {
        super("User account: " + username + " is not enabled");
    }
}
