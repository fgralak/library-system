package pl.gralak.librarysystem.exception;

public class DateParseException extends RuntimeException
{
    public DateParseException()
    {
        super("Date did not have correct format. Please enter date in following format: yyyy-mm-dd");
    }
}
