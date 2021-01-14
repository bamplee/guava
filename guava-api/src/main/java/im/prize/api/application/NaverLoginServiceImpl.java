package im.prize.api.application;

import im.prize.api.auth.JwtService;
import im.prize.api.datatool.NaverLoginClient;
import im.prize.api.datatool.NaverOpenApiClient;
import im.prize.api.datatool.response.NaverLoginResponse;
import im.prize.api.datatool.response.NaverOpenApiResponse;
import im.prize.api.domain.entity.PrizeUser;
import im.prize.api.infrastructure.persistence.jpa.repository.PrizeUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Optional;

import static im.prize.api.domain.entity.constants.UserTypeEnum.NAVER;

@Service
public class NaverLoginServiceImpl implements NaverLoginService {
    @Value("${app.naver.auth.clientId}")
    private String CLIENT_ID; //애플리케이션 클라이언트 아이디값";
    @Value("${app.naver.auth.cliSecret}")
    private String CLI_SECRET; //애플리케이션 클라이언트 시크릿값";
    @Value("${app.ui.url}")
    private String redirectUrl;

    private final NaverLoginClient naverLoginClient;
    private final NaverOpenApiClient naverOpenApiClient;
    private final PrizeUserRepository prizeUserRepository;
    private final JwtService jwtService;

    public NaverLoginServiceImpl(NaverLoginClient naverLoginClient,
                                 NaverOpenApiClient naverOpenApiClient,
                                 PrizeUserRepository prizeUserRepository, JwtService jwtService) {
        this.naverLoginClient = naverLoginClient;
        this.naverOpenApiClient = naverOpenApiClient;
        this.prizeUserRepository = prizeUserRepository;
        this.jwtService = jwtService;
    }

    @Override
    public String getNaverLoginUrl() {
        String redirectURI = null;
        try {
            redirectURI = URLEncoder.encode(String.format("%s/user/googleNews", redirectUrl), "UTF-8");
            SecureRandom random = new SecureRandom();
            String state = new BigInteger(130, random).toString();
            String apiURL = String.format(
                "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=%s&redirect_uri=%s&state=%s",
                CLIENT_ID,
                redirectURI,
                state);
            return apiURL;
        } catch (UnsupportedEncodingException e) {
            // fixme
        }
        return null;
    }

    @Override
    public NaverLoginResponse getNaverAuth(String code, String state) {
        try {
            String redirectURI = URLEncoder.encode(redirectUrl, "UTF-8");
            NaverLoginResponse res = naverLoginClient.test("authorization_code", CLIENT_ID, CLI_SECRET, redirectURI, code, state);
            return res;
        } catch (UnsupportedEncodingException e) {
            //
        }
        return null;
    }

    @Override
    public NaverOpenApiResponse getNaverProfile(String code, String state) {
        NaverLoginResponse naverLoginResponse = this.getNaverAuth(code, state);
        NaverOpenApiResponse naverProfile = this.getNaverProfile(naverLoginResponse.getAccess_token());
        return naverProfile;
    }

    @Override
    public NaverOpenApiResponse getNaverProfile(String accessToken) {
        NaverOpenApiResponse profile = naverOpenApiClient.getProfile("Bearer " + accessToken);
        return profile;
    }

    @Override
    public String getJwtToken(String code, String state) {
        NaverLoginResponse naverLoginResponse = this.getNaverAuth(code, state);
        NaverOpenApiResponse naverProfile = this.getNaverProfile(naverLoginResponse.getAccess_token());

        Optional<PrizeUser> optionalPrizeUser = prizeUserRepository.findByLoginId(String.format("n@%s", naverProfile.getResponse().getId()));
        if (!optionalPrizeUser.isPresent()) {
            PrizeUser prizeUser = prizeUserRepository.save(PrizeUser.builder()
                                                                    .type(NAVER)
                                                                    .loginId(String.format("n@%s", naverProfile.getResponse().getId()))
                                                                    .email(naverProfile.getResponse().getEmail())
                                                                    .build());
            optionalPrizeUser = prizeUserRepository.findById(prizeUser.getId());
        }

        return jwtService.createJWTToken(optionalPrizeUser.get().getLoginId(), optionalPrizeUser.get().getEmail());
    }
}
