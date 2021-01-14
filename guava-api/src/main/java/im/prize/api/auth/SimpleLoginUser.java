package im.prize.api.auth;

import im.prize.api.domain.entity.PrizeUser;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class SimpleLoginUser extends User {
    private PrizeUser user;

    public SimpleLoginUser(PrizeUser user) {
        super(user.getLoginId(), user.getEmail(), AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER"));
        this.user = user;
    }

    public PrizeUser getUser() {
        return user;
    }
}