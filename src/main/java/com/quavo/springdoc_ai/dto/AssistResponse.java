package com.quavo.springdoc_ai.dto;

import java.time.Instant;

public class AssistResponse {

    private boolean success;
    private String message;
    private Object data;
    private String error;
    private Instant timestamp;
    private String requestId;

    private AssistResponse() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final AssistResponse instance = new AssistResponse();

        public Builder success(boolean success) { instance.success = success; return this; }
        public Builder message(String message) { instance.message = message; return this; }
        public Builder data(Object data) { instance.data = data; return this; }
        public Builder error(String error) { instance.error = error; return this; }
        public Builder timestamp(Instant timestamp) { instance.timestamp = timestamp; return this; }
        public Builder requestId(String requestId) { instance.requestId = requestId; return this; }
        public AssistResponse build() { return instance; }
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
    public String getError() { return error; }
    public Instant getTimestamp() { return timestamp; }
    public String getRequestId() { return requestId; }
}