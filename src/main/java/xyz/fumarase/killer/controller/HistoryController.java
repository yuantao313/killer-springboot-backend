package xyz.fumarase.killer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.fumarase.killer.model.HistoryModel;
import xyz.fumarase.killer.service.HistoryServiceImpl;
import java.util.List;

/**
 * @author YuanTao
 */
@RestController
@CrossOrigin
public class HistoryController {

    private HistoryServiceImpl historyService;
    @Autowired
    public void setHistoryService(HistoryServiceImpl historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/history")
    public List<HistoryModel> getHistories() {
        return historyService.getHistories();
    }


    @PostMapping("/history/{id}")
    public Boolean checkHistory(@PathVariable("id") Integer id) {
        historyService.checkHistory(id);
        return true;
    }
}
