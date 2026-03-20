package saas.app.engine.dto;

import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public class ErrorResponse {
    private String error;
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String path;
}
