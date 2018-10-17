package com.inclass06.inclass06;

public class CreateThreadResponse {
    Thread thread;
    String status;
    String message;

    public CreateThreadResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CreateThreadResponse{" +
                "thread=" + thread +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
