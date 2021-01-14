package im.prize.api.application;

import im.prize.api.datatool.response.NaverLoginResponse;
import im.prize.api.datatool.response.NaverOpenApiResponse;

public interface NaverLoginService {
    String getNaverLoginUrl();

    NaverLoginResponse getNaverAuth(String code, String state);

    NaverOpenApiResponse getNaverProfile(String code, String state);

    NaverOpenApiResponse getNaverProfile(String accessToken);

    String getJwtToken(String code, String state);
}
