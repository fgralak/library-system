package pl.gralak.librarysystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.gralak.librarysystem.entity.Book;
import pl.gralak.librarysystem.service.BookServiceImpl;

@SpringBootApplication
public class LibrarySystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibrarySystemApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(BookServiceImpl bookServiceImpl)
	{
		return args -> {

			Book book1 = new Book();
			book1.setTitle("Harry Potter And The Philosopher's Stone");
			book1.setAuthor("J.K Rowling");

			Book book2 = new Book();
			book2.setTitle("Harry Potter And The Chamber Of Secrets");
			book2.setAuthor("J.K Rowling");

			bookServiceImpl.addBook(book1);
			bookServiceImpl.addBook(book2);
		};
	}
}
