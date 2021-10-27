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
public class Record
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Action action;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String author;
    @Column(nullable = false)
    private int numberOfBooks;
    private LocalDate date;

    public Record(Action action, String title, String author, int numberOfBooks, LocalDate date)
    {
        this.action = action;
        this.title = title;
        this.author = author;
        this.numberOfBooks = numberOfBooks;
        this.date = date;
    }
}
