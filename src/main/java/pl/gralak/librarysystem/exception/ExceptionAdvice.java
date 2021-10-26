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

    @ExceptionHandler(BookAlreadyExistException.class)
    public ResponseEntity<Exception> bookAlreadyExistHandler(BookAlreadyExistException e)
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
}
