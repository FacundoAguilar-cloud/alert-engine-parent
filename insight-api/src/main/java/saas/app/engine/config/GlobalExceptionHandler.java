package saas.app.engine.config;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import saas.app.engine.dto.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    //400- validación
    @ExceptionHandler(MethodArgumentNotValidException.class)
    //errores de validacion y/o campos vacios
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex){
        Map <String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldname = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldname, errorMessage);
                });

        Map <String, Object> response = new HashMap<>();
        response.put("errors", "Error de validación");
        response.put("details", errors);
        response.put("timestamp", LocalDateTime.now());


        return ResponseEntity.badRequest().body(response);
    }


    //404 - Recurso no encontrado
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity <ErrorResponse> handleNotFound(EntityNotFoundException ex, HttpServletRequest request){
        log.warn("Recurso no encontrado: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Recurso no encontrado")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    //401 No autenticado
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity <ErrorResponse> handleAuth(AuthenticationException ex, HttpServletRequest request){
        log.warn("Error en la autenticación: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("No autenticado")
                .message("Token invalido o expirado")
                .status(HttpStatus.UNAUTHORIZED.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
                
    }

    //403 No autorizado
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity <ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request){
        log.warn("Acceso denegado {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Acceso denegado")
                .message("No tienes los permisos requeridos para realizar esta acción")
                .status(HttpStatus.FORBIDDEN.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    //400 Parámetros inválidos
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity <ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request){
        log.warn("Argumento inválido: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .error("Parámetro inválido")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    //500 - Error Interno (no exponer detalles)
    @ExceptionHandler(Exception.class)
    public ResponseEntity <ErrorResponse> handleException(Exception ex, HttpServletRequest request){
        log.error("Error interno del servidor: {} ", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Error interno del servidor")
                .message("Ha ocurrido un error, contacta con el administrador o con soporte")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);

    }


}
