package im.prize.api.auth.controller;

import im.prize.api.application.NaverLoginService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class AuthRedirectController {

    private final NaverLoginService naverLoginService;

    public AuthRedirectController(NaverLoginService naverLoginService) {this.naverLoginService = naverLoginService;}

/*    @GetMapping("/api/auth/googleNews/naver/callback")
    public void getNaverProfile(HttpServletResponse response,
                                HttpServletRequest request,
                                @RequestParam("code") String code,
                                @RequestParam("state") String state) throws IOException {
        this.setToken(response, naverLoginService.getJwtToken(code, state));
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.sendRedirect("http://localhost:3000/");
    }

    private void setToken(HttpServletResponse response, String token) {
        response.setHeader("Authorization", String.format("Bearer %s", token));
    }*/
}
