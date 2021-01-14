package net.moboo.batch.hgnn.service;

import lombok.Data;

@Data
public class GuavaHgnnResultResponse<T> {
    private Result<T> result;

    @Data
    public static class Result<T> {
        private String status;
        private T data;
    }
}
