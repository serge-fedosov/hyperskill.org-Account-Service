package account.exceptions;

import account.exceptions.models.ErrorModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;


@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errorMessages = new ArrayList<>();
        for (var error : ex.getBindingResult().getAllErrors()) {
            errorMessages.add(error.getDefaultMessage());
        }

        String message = (errorMessages.size() == 1) ? errorMessages.get(0) : errorMessages.toString();
        ErrorModel error = new ErrorModel(HttpStatus.BAD_REQUEST.value(), message, request.getDescription(false));

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // handle error in List (@RequestBody List<@Valid ReceiveEmployee> receiveEmployee)
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorModel> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        List<String> errorMessages = new ArrayList<>();
        for (var error : ex.getConstraintViolations()) {
            errorMessages.add(error.getMessage());
        }

        String message = (errorMessages.size() == 1) ? errorMessages.get(0) : errorMessages.toString();
        ErrorModel error = new ErrorModel(HttpStatus.BAD_REQUEST.value(), message, request.getDescription(false));

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountServiceException.class)
    protected ResponseEntity<ErrorModel> handleAccountServiceException(AccountServiceException ex, WebRequest request) {
        ErrorModel error = new ErrorModel(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<ErrorModel> handleUserFoundException(NotFoundException ex, WebRequest request) {
        ErrorModel error = new ErrorModel(HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getDescription(false), "Not Found");

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
