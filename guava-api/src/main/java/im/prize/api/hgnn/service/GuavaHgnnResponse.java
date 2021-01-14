package im.prize.api.hgnn.service;

import lombok.Data;

@Data
public class GuavaHgnnResponse<T> {
    private String status;
    private T data;
}
