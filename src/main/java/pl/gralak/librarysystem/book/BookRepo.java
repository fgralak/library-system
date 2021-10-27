package pl.gralak.librarysystem.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepo extends JpaRepository<Book, Long>
{
    @Query("SELECT b FROM Book b WHERE b.author = ?1 ORDER BY author ASC")
    List<Book> findAllWithGivenAuthor(String author);

    @Query("SELECT b FROM Book b WHERE b.title = ?1 ORDER BY title ASC")
    List<Book> findAllWithGivenTitle(String title);

    @Query("SELECT b FROM Book b WHERE b.rating > ?1 ORDER BY rating DESC")
    List<Book> findAllWithBetterRating(double rating);

    @Query("SELECT b FROM Book b WHERE b.title = ?1 AND b.author = ?2")
    Book findBookByTitleAndAuthor(String title, String author);
}
