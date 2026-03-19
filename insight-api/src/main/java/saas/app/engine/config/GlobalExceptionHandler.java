package saas.app.engine.config;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //400- validación
    @ExceptionHandler(MethodArgumentNotValidException.class)
    //errores de validacion y/o campos vacios
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex){

    }

    //404 - Recurso no encontrado
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity <ErrorResponse> handleNotFound(){

    }

    //401 No autenticado
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity <ErrorResponse> handleAuth(){

    }

    //403 No autorizado
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity <ErrorResponse> handleAccessDenied(){

    }

    //500 - Error Interno (no exponer detalles)
    @ExceptionHandler(Exception.class)
    public ResponseEntity <ErrorResponse> handleException(){

    }

    //esto falta que lo terminemos pero queria dejar lista la estructura
}
