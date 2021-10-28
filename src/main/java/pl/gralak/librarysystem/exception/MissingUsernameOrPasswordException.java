package pl.gralak.librarysystem.exception;

public class MissingUsernameOrPasswordException extends RuntimeException
{
    public MissingUsernameOrPasswordException()
    {
        super("Value of username and/or password is null or empty");
    }
}
