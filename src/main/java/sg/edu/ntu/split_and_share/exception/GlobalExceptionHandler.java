package sg.edu.ntu.split_and_share.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Add exception handlers here
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(),
                LocalDateTime.now());
        logger.warn("User not found : {}", errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(),
                LocalDateTime.now());
        logger.warn("Authentication failed: {}", errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameIsTakenException.class)
    public ResponseEntity<ErrorResponse> handleUserNameIsTakenException(UsernameIsTakenException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(),
                LocalDateTime.now());
        logger.warn("Username is already taken: {}", errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Multiple exceptions handled in Exception handler
    @ExceptionHandler({ GroupMemberNotFoundException.class, ExpenseNotFoundException.class,
            DashboardNotFoundException.class })
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        logger.warn("Resource not found: {}", e.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    };

    // Validation exception handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        // Get a list of validation errors
        List<ObjectError> validationErrors = e.getBindingResult().getAllErrors();

        // Create a StringBuilder to store all messages
        StringBuilder sb = new StringBuilder();

        // Loop through all the errors and append the messages
        for (ObjectError error : validationErrors) {
            sb.append(error.getDefaultMessage() + ". ");
        }
        logger.warn("Validation failed: {}", sb.toString());

        ErrorResponse errorResponse = new ErrorResponse(sb.toString(), LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
// Exception when a client sends a GET request to a URL endpoint, but there is no controller method defined to handle GET requests for that URL.
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        ErrorResponse errorResponse = new ErrorResponse(
            "The requested HTTP method is not supported for this endpoint. Please check your request method and try again.",
            LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // General Exception Handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        // logger.error....
        logger.error("An unexpected error occurred: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse("Something went wrong. Please contact support.",
                LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
