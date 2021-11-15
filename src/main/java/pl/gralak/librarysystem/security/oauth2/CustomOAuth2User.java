package pl.gralak.librarysystem.security.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User, UserDetails
{
    private final OAuth2User oAuth2User;
    private final String oAuth2ClientName;

    @Override
    public Map<String, Object> getAttributes()
    {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName()
    {
        return oAuth2User.getName();
    }

    public String getEmail()
    {
        return oAuth2User.getAttribute("email");
    }

    public String getOAuth2ClientName()
    {
        return this.oAuth2ClientName;
    }

    @Override
    public String getPassword()
    {
        return null;
    }

    @Override
    public String getUsername()
    {
        return oAuth2User.getAttribute("email");
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }

}
