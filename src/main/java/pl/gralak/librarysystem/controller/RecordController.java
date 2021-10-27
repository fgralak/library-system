package pl.gralak.librarysystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.gralak.librarysystem.entity.Record;
import pl.gralak.librarysystem.service.RecordServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/record")
@RequiredArgsConstructor
public class RecordController
{
    private final RecordServiceImpl recordServiceImpl;

    @GetMapping("/all")
    public List<Record> getAllRecords()
    {
        return recordServiceImpl.getAllRecords();
    }

    @GetMapping("/all-added")
    public List<Record> getAllAddedRecords()
    {
        return recordServiceImpl.getAllAddedRecords();
    }

    @GetMapping("/all-deleted")
    public List<Record> getAllDeletedRecords()
    {
        return recordServiceImpl.getAllDeletedRecords();
    }

    @GetMapping("/range")
    public List<Record> getRecordsFromDateRange(@RequestParam String fromDate, @RequestParam String toDate)
    {
        return recordServiceImpl.getRecordsFromDateRange(fromDate, toDate);
    }

}
