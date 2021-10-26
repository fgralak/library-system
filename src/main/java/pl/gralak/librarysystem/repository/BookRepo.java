package pl.gralak.librarysystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.gralak.librarysystem.entity.Book;

import java.util.List;

@Repository
public interface BookRepo extends JpaRepository<Book, Long>
{
    @Query("SELECT b FROM Book b WHERE b.author = ?1")
    List<Book> findAllWithGivenAuthor(String author);

    @Query("SELECT b FROM Book b WHERE b.title = ?1")
    List<Book> findAllWithGivenTitle(String title);

    @Query("SELECT b FROM Book b WHERE b.rating > ?1")
    List<Book> findAllWithBetterRating(double rating);
}
