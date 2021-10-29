package pl.gralak.librarysystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.gralak.librarysystem.appuser.AppUser;
import pl.gralak.librarysystem.appuser.AppUserRepo;
import pl.gralak.librarysystem.book.Book;
import pl.gralak.librarysystem.book.BookServiceImpl;

import static pl.gralak.librarysystem.appuser.Provider.LOCAL;
import static pl.gralak.librarysystem.appuser.Role.ROLE_USER;

@SpringBootApplication
public class LibrarySystemApplication {

	public static void main(String[] args) {
		SpringApplication.	run(LibrarySystemApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner commandLineRunner(BookServiceImpl bookServiceImpl,
										AppUserRepo appUserRepo)
	{
		return args -> {

			Book book1 = new Book();
			book1.setTitle("Harry Potter And The Philosopher's Stone");
			book1.setAuthor("JK Rowling");

			Book book2 = new Book();
			book2.setTitle("Harry Potter And The Chamber Of Secrets");
			book2.setAuthor("JK Rowling");

			bookServiceImpl.addBook(book1);
			bookServiceImpl.addBook(book2);

			AppUser user = new AppUser();
			user.setUsername("user@user.com");
			user.setPassword(passwordEncoder().encode("user"));
			user.setRole(ROLE_USER);
			user.setAuthProvider(LOCAL);
			user.setEnabled(true);

			appUserRepo.save(user);
		};
	}
}
