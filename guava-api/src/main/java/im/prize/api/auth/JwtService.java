package im.prize.api.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;

import java.util.Date;

@Slf4j
public class JwtService {
    private Algorithm algorithm;

    public JwtService(String secretKey) {
        this.algorithm = Algorithm.HMAC256(secretKey);
    }

    public String createJWTToken(String loginId, String email) {
        String token = null;

        try {
            Long EXPIRATION_TIME = 1000L * 60L * 10L;
            Date issuedAt = new Date();
            Date notBefore = new Date(issuedAt.getTime());
            Date expiresAt = new Date(issuedAt.getTime() + EXPIRATION_TIME);

            token = JWT.create()
                       .withIssuedAt(issuedAt)
                       .withNotBefore(notBefore)
                       .withExpiresAt(expiresAt)
                       .withClaim("loginId", loginId)
                       .withClaim("email", email)
                       .withSubject(loginId)
                       .sign(this.algorithm);
        } catch (Exception e) {
            System.err.println("err: " + e);
        }
        return token;
    }

    public String generateToken(Authentication auth) {
        SimpleLoginUser loginUser = (SimpleLoginUser) auth.getPrincipal();

        Long EXPIRATION_TIME = 1000L * 60L * 10L;
        Date issuedAt = new Date();
        Date notBefore = new Date(issuedAt.getTime());
        Date expiresAt = new Date(issuedAt.getTime() + EXPIRATION_TIME);

        String token = JWT.create()
                          .withIssuedAt(issuedAt)
                          .withNotBefore(notBefore)
                          .withExpiresAt(expiresAt)
                          .withClaim("loginId", loginUser.getUser().getLoginId())
                          .withClaim("email", loginUser.getUser().getEmail())
                          .withSubject(loginUser.getUser().getId().toString())
                          .sign(this.algorithm);
        log.debug("generate token : {}", token);
        return token;
    }
}
