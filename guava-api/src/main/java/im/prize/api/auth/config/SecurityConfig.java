package im.prize.api.auth.config;

import im.prize.api.auth.JwtService;
import im.prize.api.auth.SimpleAccessDeniedHandler;
import im.prize.api.auth.SimpleAuthenticationEntryPoint;
import im.prize.api.auth.SimpleAuthenticationFailureHandler;
import im.prize.api.auth.SimpleAuthenticationSuccessHandler;
import im.prize.api.auth.SimpleTokenFilter;
import im.prize.api.auth.SimpleUserDetailsService;
import im.prize.api.infrastructure.persistence.jpa.repository.PrizeUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.filter.GenericFilterBean;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PrizeUserRepository userRepository;
    @Autowired
    private SimpleUserDetailsService simpleUserDetailsService;

    // ★1
    @Value("${app.jwt.secretKey:secret}")
    private String secretKey;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            // AUTHORIZE
            .authorizeRequests()
            .antMatchers("/api/v1/**")
            .permitAll()
            .antMatchers("/v1/nid/**")
            .permitAll()
            .antMatchers("/api/auth/**")
            .permitAll()
            .antMatchers("/api/products/**")
            .permitAll()
            .antMatchers("/api/deals/**")
            .permitAll()
            .antMatchers("/api/deals/**/schedules")
            .authenticated()
            .antMatchers("/user/**")
            .hasRole("USER")
            .antMatchers("/admin/**")
            .hasRole("ADMIN")
            .anyRequest()
            .authenticated()
            .and()
            // EXCEPTION
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint())
            .accessDeniedHandler(accessDeniedHandler())
            .and()
            // LOGIN
            .formLogin()
            .loginProcessingUrl("/login").permitAll()
            .usernameParameter("username")
            .passwordParameter("password")
            .successHandler(authenticationSuccessHandler())
            .failureHandler(authenticationFailureHandler())
            .and()
            // ★2 LOGOUT
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessHandler(logoutSuccessHandler())
            .and()
            // ★3 CSRF
            .csrf()
            .disable()
            // ★4 AUTHORIZE
            .addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class)
            // ★5 SESSION
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ;
        // @formatter:on
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.eraseCredentials(true)
            .userDetailsService(simpleUserDetailsService)
            .passwordEncoder(passwordEncoder());
    }

//    @Bean("simpleUserDetailsService")
//    UserDetailsService simpleUserDetailsService() {
//        return new SimpleUserDetailsService(userRepository);
//    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    JwtService jwtService() {
        return new JwtService(secretKey);
    }

    GenericFilterBean tokenFilter() {
        return new SimpleTokenFilter(userRepository, secretKey);
    }

    AuthenticationEntryPoint authenticationEntryPoint() {
        return new SimpleAuthenticationEntryPoint();
    }

    AccessDeniedHandler accessDeniedHandler() {
        return new SimpleAccessDeniedHandler();
    }

    AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new SimpleAuthenticationSuccessHandler(secretKey, jwtService());
    }

    AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleAuthenticationFailureHandler();
    }

    LogoutSuccessHandler logoutSuccessHandler() {
        return new HttpStatusReturningLogoutSuccessHandler();
    }

}