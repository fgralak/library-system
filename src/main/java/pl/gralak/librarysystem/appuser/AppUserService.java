package pl.gralak.librarysystem.appuser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gralak.librarysystem.exception.MissingUsernameOrPasswordException;
import pl.gralak.librarysystem.exception.UserAlreadyExistsException;

import java.util.Collections;

import static pl.gralak.librarysystem.appuser.Provider.LOCAL;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AppUserService implements UserDetailsService
{
    private final AppUserRepo appUserRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        AppUser appUser = appUserRepo.findByUsername(username);
        if(appUser == null)
        {
            throw new UsernameNotFoundException("User cannot be found");
        }
        else
        {
            log.info("User found in the database: {}", username);
        }

        return new User(appUser.getUsername(), appUser.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(appUser.getRole().name())));
    }

    public void addOAuth2User(AppUser user)
    {
        if(appUserRepo.findByUsernameAndProvider(user.getUsername(), user.getAuthProvider()) != null)
        {
            throw new UserAlreadyExistsException(user.getUsername(), user.getAuthProvider());
        }
        appUserRepo.save(user);
    }

    public void addLocalUser(AppUser user)
    {
        String username = user.getUsername();
        if(appUserRepo.findByUsernameAndProvider(username, LOCAL) != null)
        {
            throw new UserAlreadyExistsException(username, LOCAL);
        }
        if(username == null || username.length() == 0 || user.getPassword() == null || user.getPassword().length() == 0)
        {
            throw new MissingUsernameOrPasswordException();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAuthProvider(LOCAL);
        appUserRepo.save(user);
    }
}
