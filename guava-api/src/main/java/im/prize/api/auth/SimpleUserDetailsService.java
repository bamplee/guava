package im.prize.api.auth;

import im.prize.api.domain.entity.PrizeUser;
import im.prize.api.infrastructure.persistence.jpa.repository.PrizeUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SimpleUserDetailsService implements UserDetailsService {
    private PrizeUserRepository usersRepository;

    @Autowired
    public SimpleUserDetailsService(PrizeUserRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<PrizeUser> user = usersRepository.findByLoginId(s);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("UsernameNotFoundException");
        }
        return new SimpleLoginUser(user.get());
    }
}