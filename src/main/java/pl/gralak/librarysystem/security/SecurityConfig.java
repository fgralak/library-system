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
        http.authorizeRequests().antMatchers("/user/manage-employees", "/user/create-form/employee",
                "/user/create/employee").hasAuthority("ROLE_ADMIN");
        http.authorizeRequests().antMatchers("/user/edit-form/{id}", "/user/edit/{id}",
                "/user/delete/{id}", "/user/reset-password-form/{role}/{id}", "/user/reset-password/{role}/{id}")
                .access("@webSecurity.checkUserAccess(authentication,#id)");
        http.authorizeRequests().antMatchers("/book/all", "/book/all-by-author", "/book/all-by-title",
                "/book/all-by-rating").hasAnyAuthority("ROLE_EMPLOYEE", "ROLE_USER");
        http.authorizeRequests().antMatchers("/user/rented-books-employee", "/book/**",
                "/record/**").hasAuthority("ROLE_EMPLOYEE");
        http.authorizeRequests().antMatchers("/user/rented-books").hasAuthority("ROLE_USER");
        http.authorizeRequests().antMatchers("/user/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE");
        http.authorizeRequests().anyRequest().authenticated();
        http.formLogin().loginPage("/login").usernameParameter("email").defaultSuccessUrl("/menu");
        http.oauth2Login().loginPage("/login").userInfoEndpoint().userService(customOAuth2UserService)
                .and().successHandler(oAuth2LoginSuccessHandler).defaultSuccessUrl("/menu");
        http.logout().logoutUrl("/logout");
    }
}
