package im.prize.api.datatool.response;

import lombok.Data;

@Data
public class NaverLoginResponse {
    private String access_token;
    private String refresh_token;
    private String token_type;
    private String expires_in;
}
