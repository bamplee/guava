package im.prize.api.auth.controller;

import lombok.Data;

@Data
public class AuthNaverLoginRequest {
    private String code;
    private String state;
}
