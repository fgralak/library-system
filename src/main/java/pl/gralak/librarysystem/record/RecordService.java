package pl.gralak.librarysystem.record;

import java.util.List;

public interface RecordService
{
    List<Record> getAllRecords();

    List<Record> getAllAddedRecords();

    List<Record> getAllDeletedRecords();

    List<Record> getRecordsFromDateRange(String fromDate, String toDate);
}