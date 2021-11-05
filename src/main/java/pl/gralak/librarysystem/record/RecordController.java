package pl.gralak.librarysystem.record;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/record")
@RequiredArgsConstructor
public class RecordController
{
    private final RecordServiceImpl recordServiceImpl;

    @GetMapping("/manage-records")
    public String getRecordsManger()
    {
        return "/record/manage-records";
    }

    @GetMapping("/all")
    public String getAllRecords(Model model)
    {
        model.addAttribute("listOfRecords", recordServiceImpl.getAllRecords());
        model.addAttribute("action", "all");
        return "record/record-list";
    }

    @GetMapping("/all-added")
    public String getAllAddedRecords(Model model)
    {
        model.addAttribute("listOfRecords", recordServiceImpl.getAllAddedRecords());
        model.addAttribute("action", "added");
        return "record/record-list";
    }

    @GetMapping("/all-deleted")
    public String getAllDeletedRecords(Model model)
    {
        model.addAttribute("listOfRecords", recordServiceImpl.getAllDeletedRecords());
        model.addAttribute("action", "deleted");
        return "record/record-list";
    }

    @GetMapping("/range")
    public String getRecordsFromDateRange(@RequestParam String fromDate, @RequestParam String toDate, Model model)
    {
        model.addAttribute("listOfRecords", recordServiceImpl.getRecordsFromDateRange(fromDate, toDate));
        model.addAttribute("action", "all");
        return "record/record-list";
    }

}
