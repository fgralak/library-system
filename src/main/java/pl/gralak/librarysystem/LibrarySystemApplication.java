package pl.gralak.librarysystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.gralak.librarysystem.appuser.AppUser;
import pl.gralak.librarysystem.appuser.AppUserRepo;
import pl.gralak.librarysystem.book.Book;
import pl.gralak.librarysystem.book.BookServiceImpl;

import static pl.gralak.librarysystem.appuser.Provider.LOCAL;
import static pl.gralak.librarysystem.appuser.Role.*;

@SpringBootApplication
@EnableAsync
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

			AppUser employee = new AppUser();
			employee.setFirstName("Kate");
			employee.setLastName("Winslet");
			employee.setUsername("employee@employee.com");
			employee.setPassword(passwordEncoder().encode("employee"));
			employee.setRole(ROLE_EMPLOYEE);
			employee.setAuthProvider(LOCAL);
			employee.setEnabled(true);

			AppUser admin = new AppUser();
			admin.setUsername("admin@admin.com");
			admin.setPassword(passwordEncoder().encode("admin"));
			admin.setRole(ROLE_ADMIN);
			admin.setAuthProvider(LOCAL);
			admin.setEnabled(true);

			appUserRepo.save(user);
			appUserRepo.save(employee);
			appUserRepo.save(admin);
		};
	}
}
