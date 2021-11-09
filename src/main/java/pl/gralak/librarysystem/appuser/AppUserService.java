package pl.gralak.librarysystem.appuser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gralak.librarysystem.book.Book;
import pl.gralak.librarysystem.book.BookServiceImpl;
import pl.gralak.librarysystem.exception.*;
import pl.gralak.librarysystem.registration.token.ConfirmationToken;
import pl.gralak.librarysystem.registration.token.ConfirmationTokenService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static pl.gralak.librarysystem.appuser.Provider.LOCAL;
import static pl.gralak.librarysystem.appuser.Role.ROLE_EMPLOYEE;
import static pl.gralak.librarysystem.appuser.Role.ROLE_USER;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AppUserService implements UserDetailsService
{
    private final AppUserRepo appUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final BookServiceImpl bookService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        AppUser appUser = appUserRepo.findByUsernameAndProvider(username, LOCAL);
        if(appUser == null)
        {
            throw new UsernameNotFoundException("User cannot be found");
        }
        else
        {
            log.info("User found in the database: {}", username);
        }

        return new User(appUser.getUsername(), appUser.getPassword() ,appUser.isEnabled(),
                appUser.isAccountNonExpired(), appUser.isCredentialsNonExpired(), appUser.isAccountNonLocked(),
                Collections.singleton(new SimpleGrantedAuthority(appUser.getRole().name())) );
    }

    public void addOAuth2User(AppUser user)
    {
        if(appUserRepo.findByUsernameAndProvider(user.getUsername(), user.getAuthProvider()) != null)
        {
            throw new UserAlreadyExistsException(user.getUsername(), user.getAuthProvider());
        }
        appUserRepo.save(user);
    }

    public String addLocalUser(AppUser user)
    {
        String username = user.getUsername();
        if(username == null || username.length() == 0 || user.getPassword() == null || user.getPassword().length() == 0)
        {
            throw new MissingUsernameOrPasswordException();
        }

        AppUser existedUser = appUserRepo.findByUsernameAndProvider(username, LOCAL);
        if(existedUser != null)
        {
            if(!existedUser.isEnabled() && existedUser.getUsername().equals(user.getUsername()))
            {
                existedUser.setPassword(user.getPassword());
                String token = UUID.randomUUID().toString();
                ConfirmationToken confirmationToken = new ConfirmationToken();
                confirmationToken.setToken(token);
                confirmationToken.setCreatedAt(LocalDateTime.now());
                confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
                confirmationToken.setAppUser(existedUser);

                confirmationTokenService.saveConfirmationToken(confirmationToken);

                return token;
            }
            else
            {
                throw new UserAlreadyExistsException(username, LOCAL);
            }
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAuthProvider(LOCAL);
        appUserRepo.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setToken(token);
        confirmationToken.setCreatedAt(LocalDateTime.now());
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        confirmationToken.setAppUser(user);

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return token;
    }

    public void enableAppUser(String username)
    {
        appUserRepo.enableAppUser(username);
    }

    public List<AppUser> getAllEmployees()
    {
        return appUserRepo.findAllEmployees();
    }

    public void addUserByAdminOrEmployee(AppUser user, String type)
    {
        String username = user.getUsername();
        if(username == null || username.length() == 0 || user.getPassword() == null || user.getPassword().length() == 0)
        {
            throw new MissingUsernameOrPasswordException();
        }

        AppUser existedUser = appUserRepo.findByUsernameAndProvider(username, LOCAL);
        if(existedUser != null)
        {
            throw new UserAlreadyExistsException(username, LOCAL);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAuthProvider(LOCAL);
        if(type.equals("user"))
        {
            user.setRole(ROLE_USER);
        }
        else
        {
            user.setRole(ROLE_EMPLOYEE);
        }
        user.setEnabled(true);
        appUserRepo.save(user);
    }

    public AppUser getUserById(Long id)
    {
        return appUserRepo.findById(id).orElseThrow(() ->
                new UserNotFoundException(id));
    }

    public void updateUser(AppUser user)
    {
        String username = user.getUsername();
        if(username == null || username.length() == 0 || user.getPassword() == null || user.getPassword().length() == 0)
        {
            throw new MissingUsernameOrPasswordException();
        }

        appUserRepo.save(user);
    }

    public AppUser deleteUser(Long id)
    {
        AppUser appUser = getUserById(id);
        appUserRepo.deleteById(id);
        return appUser;
    }

    public void resetPassword(String password, Long id)
    {
        if(password == null || password.length() == 0)
        {
            throw new MissingUsernameOrPasswordException();
        }
        AppUser appUser = getUserById(id);
        appUser.setPassword(passwordEncoder.encode(password));
        appUserRepo.save(appUser);
    }

    public AppUser getUserByUsername(String username)
    {
        return appUserRepo.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    public Set<Book> getAllRentedBooksByUser()
    {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AppUser appUser = getUserByUsername(userDetails.getUsername());
        return appUser.getRentedBooks();
    }

    public Set<Book> getAllRentedBooksByUsername(String username)
    {
        AppUser appUser = getUserByUsername(username);
        return appUser.getRentedBooks();
    }

    public void rentBook(Long id, String username)
    {
        Book book = bookService.getBookById(id);
        if(book.getBooksAvailable() == 0)
        {
            throw new NoAvailableBookException();
        }

        AppUser user = getUserByUsername(username);
        if(!user.isEnabled())
        {
            throw new UserAccountNotEnabledException(username);
        }
        if(user.getRentedBooks().size() == 5)
        {
            throw new TooManyBooksRentedException();
        }
        Set<Book> rentedBooks = user.getRentedBooks();
        rentedBooks.add(book);
        user.setRentedBooks(rentedBooks);
        book.setBooksAvailable(book.getBooksAvailable() - 1);
    }

    public void returnBook(Long id, String username)
    {
        Book book = bookService.getBookById(id);
        AppUser user = getUserByUsername(username);
        Set<Book> rentedBooks = user.getRentedBooks();
        rentedBooks.remove(book);
        user.setRentedBooks(rentedBooks);
        book.setBooksAvailable(book.getBooksAvailable() + 1);
    }
}
