package pl.gralak.librarysystem.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String author;

    private int year;
    @Enumerated(EnumType.STRING)
    @Column(name = "book_subject")
    private Classification bookSubject;
    private String location; // location in library
    private int numberOfBooks; // number of books owned by library
    private int booksAvailable; // number of books available in library
    private double rating;
}
