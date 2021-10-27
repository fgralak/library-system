package pl.gralak.librarysystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.gralak.librarysystem.entity.Record;
import pl.gralak.librarysystem.exception.DateParseException;
import pl.gralak.librarysystem.repository.RecordRepo;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService
{
    private final RecordRepo recordRepo;

    @Override
    public List<Record> getAllRecords()
    {
        return recordRepo.findAll();
    }

    @Override
    public List<Record> getAllAddedRecords()
    {
        return recordRepo.findAllAddedRecords();
    }

    @Override
    public List<Record> getAllDeletedRecords()
    {
        return recordRepo.findAllDeletedRecords();
    }

    @Override
    public List<Record> getRecordsFromDateRange(String fromDate, String toDate)
    {
        LocalDate parsedFrom, parsedTo;
        try
        {
            parsedFrom = LocalDate.parse(fromDate);
            parsedTo = LocalDate.parse(toDate);
        } catch(DateTimeParseException e)
        {
            throw new DateParseException();
        }
        return recordRepo.getRecordsFromDateRange(parsedFrom, parsedTo);
    }
}
