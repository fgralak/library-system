package pl.gralak.librarysystem.service;

import pl.gralak.librarysystem.entity.Record;

import java.util.List;

public interface RecordService
{
    List<Record> getAllRecords();

    List<Record> getAllAddedRecords();

    List<Record> getAllDeletedRecords();

    List<Record> getRecordsFromDateRange(String fromDate, String toDate);
}