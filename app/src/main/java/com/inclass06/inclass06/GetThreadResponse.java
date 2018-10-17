package com.inclass06.inclass06;

import java.util.List;

public class GetThreadResponse {
    List<Thread> threads;
    String status;


    public GetThreadResponse() {
    }

    public List<Thread> getThreads() {
        return threads;
    }

    public void setThreads(List<Thread> threads) {
        this.threads = threads;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "GetThreadResponse{" +
                "threads=" + threads +
                ", status='" + status + '\'' +
                '}';
    }
}
