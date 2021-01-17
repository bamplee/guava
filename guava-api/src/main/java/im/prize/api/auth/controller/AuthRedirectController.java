package im.prize.api.auth.controller;

import im.prize.api.application.NaverLoginService;
import org.springframework.stereotype.Controller;

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
