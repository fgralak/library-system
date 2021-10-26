package pl.gralak.librarysystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

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

    private LocalDate year;
    private Classification bookSubject;
    private String location; // location in library
    private int booksAvailable; // number of books available in library
    private boolean onlyInside; // if book is only available inside library
    private double rating;
}
