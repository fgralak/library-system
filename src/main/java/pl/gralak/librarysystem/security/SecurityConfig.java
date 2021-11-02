package pl.gralak.librarysystem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import pl.gralak.librarysystem.security.oauth2.CustomOAuth2UserService;
import pl.gralak.librarysystem.security.oauth2.OAuth2LoginSuccessHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.authorizeRequests().antMatchers("/", "/login", "/oauth/**", "/sign-up", "/register",
                "/registration/**", "/images/**").permitAll();
        http.authorizeRequests().anyRequest().authenticated();
        http.formLogin().loginPage("/login").usernameParameter("email").defaultSuccessUrl("/menu");
        http.oauth2Login().loginPage("/login").userInfoEndpoint().userService(customOAuth2UserService)
                .and().successHandler(oAuth2LoginSuccessHandler).defaultSuccessUrl("/menu");
        http.logout().logoutUrl("/logout");
    }
}
