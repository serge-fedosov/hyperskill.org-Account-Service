package account.exceptions.models;

import java.time.LocalDateTime;

public class ErrorModel {

    private int status;

    private LocalDateTime timestamp;

    private String message;
    private String error;

    private String path;

    public ErrorModel(int status, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.error = "Bad Request";

        if (path.startsWith("uri=")) {
            path = path.substring(4);
        }
        this.path = path;
    }

    public ErrorModel(int status, String message, String path, String error) {
        this(status, message, path);

        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }
}
