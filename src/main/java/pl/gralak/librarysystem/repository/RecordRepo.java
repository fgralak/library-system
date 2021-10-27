package pl.gralak.librarysystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.gralak.librarysystem.entity.Record;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepo extends JpaRepository<Record, Long>
{
    @Query("SELECT r FROM Record r WHERE r.action = ADDED")
    List<Record> findAllAddedRecords();

    @Query("SELECT r FROM Record r WHERE r.action = DELETED")
    List<Record> findAllDeletedRecords();

    @Query("SELECT r FROM Record r WHERE r.date > ?1 AND r.date < ?2")
    List<Record> getRecordsFromDateRange(LocalDate fromDate, LocalDate toDate);
}
