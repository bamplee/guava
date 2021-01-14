package im.prize.api.auth.controller;

import im.prize.api.application.NaverLoginService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final NaverLoginService naverLoginService;

    public AuthController(NaverLoginService naverLoginService) {
        this.naverLoginService = naverLoginService;
    }

    @GetMapping("/login/naver")
    public String testNaver() {
        return naverLoginService.getNaverLoginUrl();
    }

    @PostMapping("/login/naver/callback")
    public String getNaverProfile(HttpServletResponse response,
                                  @RequestBody AuthNaverLoginRequest authNaverLoginRequest) throws IOException {
        String jwtToken = naverLoginService.getJwtToken(authNaverLoginRequest.getCode(), authNaverLoginRequest.getState());
        this.setToken(response, jwtToken);
        return jwtToken;
    }

    private void setToken(HttpServletResponse response, String token) {
        response.setHeader("Authorization", String.format("Bearer %s", token));
    }
}
