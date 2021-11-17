package pl.gralak.librarysystem.appuser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static pl.gralak.librarysystem.appuser.Provider.LOCAL;
import static pl.gralak.librarysystem.appuser.Role.*;

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
        AppUser appUser = appUserRepo.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException(username));
        if(appUser.getPassword() == null)
        {
            throw new BadCredentialsException("Wrong password or authentication provider");
        }
        else
        {
            log.info("User found in the database: {}", username);
        }
        return appUser;
    }

    public void addOAuth2User(AppUser user)
    {
        if(appUserRepo.findByUsername(user.getUsername()).isPresent())
        {
            throw new UserAlreadyExistsException(user.getUsername());
        }
        appUserRepo.save(user);
    }

    public void updateAuthProvider(String username, String clientName)
    {
        Provider provider = Provider.valueOf(clientName.toUpperCase());
        appUserRepo.updateAuthProvider(username, provider);
    }

    public String addLocalUser(AppUser user)
    {
        String username = user.getUsername();
        if(username == null || username.length() == 0 || user.getPassword() == null || user.getPassword().length() == 0)
        {
            throw new MissingUsernameOrPasswordException();
        }

        AppUser existedUser = getUserByUsername(username);
        if(existedUser != null)
        {
            if(!existedUser.getAuthProvider().equals(LOCAL))
            {
                existedUser.setFirstName(user.getFirstName());
                existedUser.setLastName(user.getLastName());
                existedUser.setPassword(passwordEncoder.encode(user.getPassword()));
                existedUser.setAuthProvider(LOCAL);
                appUserRepo.save(existedUser);
                return null;
            }
            else if(!existedUser.isEnabled() && existedUser.getUsername().equals(user.getUsername()))
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
                throw new UserAlreadyExistsException(username);
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

        AppUser existedUser = getUserByUsername(username);
        if(existedUser != null)
        {
            if(existedUser.getPassword() != null)
            {
                throw new UserAlreadyExistsException(username);
            }
            else
            {
                existedUser.setFirstName(user.getFirstName());
                existedUser.setLastName(user.getLastName());
                existedUser.setPassword(passwordEncoder.encode(user.getPassword()));
                existedUser.setAuthProvider(LOCAL);
                appUserRepo.save(existedUser);
                return;
            }

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
        return appUserRepo.findUserByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
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

    @EventListener(ApplicationReadyEvent.class)
    public void init()
    {
        Optional<AppUser> appUser = appUserRepo.findByUsername("user@user.com");
        if (!appUser.isPresent())
        {
            AppUser user = new AppUser();
            user.setFirstName("User");
            user.setLastName("User");
            user.setUsername("user@user.com");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRole(ROLE_USER);
            user.setAuthProvider(LOCAL);
            user.setEnabled(true);
            appUserRepo.save(user);
        }

        appUser = appUserRepo.findByUsername("employee@employee.com");
        if (!appUser.isPresent())
        {
            AppUser user = new AppUser();
            user.setFirstName("Employee");
            user.setLastName("Employee");
            user.setUsername("employee@employee.com");
            user.setPassword(passwordEncoder.encode("employee"));
            user.setRole(ROLE_EMPLOYEE);
            user.setAuthProvider(LOCAL);
            user.setEnabled(true);
            appUserRepo.save(user);
        }

        appUser = appUserRepo.findByUsername("admin@admin.com");
        if (!appUser.isPresent())
        {
            AppUser user = new AppUser();
            user.setFirstName("Admin");
            user.setLastName("Admin");
            user.setUsername("admin@admin.com");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setRole(ROLE_ADMIN);
            user.setAuthProvider(LOCAL);
            user.setEnabled(true);
            appUserRepo.save(user);
        }
    }
}
