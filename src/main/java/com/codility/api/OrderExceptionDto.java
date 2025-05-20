package com.codility.api;

public class OrderExceptionDto {
    private Integer status;
    private String message;

    public Integer getStatus() {
        return status;
    }

    public OrderExceptionDto setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public OrderExceptionDto setMessage(String message) {
        this.message = message;
        return this;
    }
}
