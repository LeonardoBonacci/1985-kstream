package guru.bonacci.heroes.transferingress.transfer;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Value;

@ControllerAdvice
class ControllerAdviceErrorHandling {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationFailedResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {

      var fieldErrors = e.getBindingResult().getFieldErrors().stream().map(fieldError -> 
        new ViolationError(fieldError.getField(), fieldError.getDefaultMessage())
      );

      var globalErrors = e.getBindingResult().getGlobalErrors().stream().map(globalError -> 
        new ViolationError(globalError.getObjectName(), globalError.getDefaultMessage())
      );
      
      return new ValidationFailedResponse(
                     concat(fieldErrors, globalErrors).collect(toList()));
    }

    @Value
    public static class ValidationFailedResponse {
      private List<ViolationError> violations;
    }
    
    @Value
    public static class ViolationError {
      private final String fieldName;
      private final String message;
    }
}