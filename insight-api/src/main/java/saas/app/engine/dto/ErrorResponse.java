package saas.app.engine.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private String path;
}
