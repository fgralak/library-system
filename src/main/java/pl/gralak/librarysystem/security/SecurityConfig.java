package pl.gralak.librarysystem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.gralak.librarysystem.appuser.AppUserService;
import pl.gralak.librarysystem.security.oauth2.CustomOAuth2UserService;
import pl.gralak.librarysystem.security.oauth2.OAuth2AuthenticationSuccessHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    private final AppUserService appUserService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler authenticationSuccessHandler;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.userDetailsService(appUserService).passwordEncoder(passwordEncoder);
    }

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
        http.rememberMe().key("jAAAjyXe0nRSeoMOOXlL0snIWBLfYc7a").tokenValiditySeconds(14 * 24 * 60 * 60);
        http.oauth2Login().loginPage("/login").userInfoEndpoint().userService(customOAuth2UserService)
                .and().successHandler(authenticationSuccessHandler);
        http.logout().logoutUrl("/logout");
    }
}
