package pl.gralak.librarysystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;

@RestControllerAdvice
public class ExceptionAdvice
{
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Exception> bookNotFoundHandler(BookNotFoundException e)
    {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        Exception exception = new Exception(e.getMessage(), httpStatus, ZonedDateTime.now());
        return new ResponseEntity<>(exception, httpStatus);
    }

    @ExceptionHandler(BookAlreadyExistsException.class)
    public ResponseEntity<Exception> bookAlreadyExistHandler(BookAlreadyExistsException e)
    {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        Exception exception = new Exception(e.getMessage(), httpStatus, ZonedDateTime.now());
        return new ResponseEntity<>(exception, httpStatus);
    }

    @ExceptionHandler(MissingTitleOrAuthorException.class)
    public ResponseEntity<Exception> missingTitleOrAuthorHandler(MissingTitleOrAuthorException e)
    {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        Exception exception = new Exception(e.getMessage(), httpStatus, ZonedDateTime.now());
        return new ResponseEntity<>(exception, httpStatus);
    }

    @ExceptionHandler(DateParseException.class)
    public ResponseEntity<Exception> dateParseExceptionHandler(DateParseException e)
    {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        Exception exception = new Exception(e.getMessage(), httpStatus, ZonedDateTime.now());
        return new ResponseEntity<>(exception, httpStatus);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Exception> userAlreadyExistsHandler(UserAlreadyExistsException e)
    {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        Exception exception = new Exception(e.getMessage(), httpStatus, ZonedDateTime.now());
        return new ResponseEntity<>(exception, httpStatus);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Exception> userNotFoundHandler(UserNotFoundException e)
    {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        Exception exception = new Exception(e.getMessage(), httpStatus, ZonedDateTime.now());
        return new ResponseEntity<>(exception, httpStatus);
    }

    @ExceptionHandler(MissingUsernameOrPasswordException.class)
    public ResponseEntity<Exception> missingUsernameOrPasswordHandler(MissingUsernameOrPasswordException e)
    {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        Exception exception = new Exception(e.getMessage(), httpStatus, ZonedDateTime.now());
        return new ResponseEntity<>(exception, httpStatus);
    }

    @ExceptionHandler(SomeBooksAreRentedException.class)
    public ResponseEntity<Exception> someBooksAreNotReturnedHandler(SomeBooksAreRentedException e)
    {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        Exception exception = new Exception(e.getMessage(), httpStatus, ZonedDateTime.now());
        return new ResponseEntity<>(exception, httpStatus);
    }

    @ExceptionHandler(NoAvailableBookException.class)
    public ResponseEntity<Exception> noAvailableBookHandler(NoAvailableBookException e)
    {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        Exception exception = new Exception(e.getMessage(), httpStatus, ZonedDateTime.now());
        return new ResponseEntity<>(exception, httpStatus);
    }

    @ExceptionHandler(TooManyBooksRentedException.class)
    public ResponseEntity<Exception> tooManyBooksRentedHandler(TooManyBooksRentedException e)
    {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        Exception exception = new Exception(e.getMessage(), httpStatus, ZonedDateTime.now());
        return new ResponseEntity<>(exception, httpStatus);
    }

    @ExceptionHandler(UserAccountNotEnabledException.class)
    public ResponseEntity<Exception> tooManyBooksRentedHandler(UserAccountNotEnabledException e)
    {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        Exception exception = new Exception(e.getMessage(), httpStatus, ZonedDateTime.now());
        return new ResponseEntity<>(exception, httpStatus);
    }
}
