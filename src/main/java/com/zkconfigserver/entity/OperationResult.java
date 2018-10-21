package com.zkconfigserver.entity;

import java.util.Date;

/**
 * Created by madali on 2016/5/25.
 */
public class OperationResult {

    private Integer statusCode = 201;

    private boolean success = true;

    private String message;

    private Date timestamp;

    private String token;

    public OperationResult() {
    }

    public OperationResult(Date timestamp) {
        this.timestamp = timestamp;
    }

    public OperationResult(Date timestamp, String token) {
        this.timestamp = timestamp;
        this.token = token;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
