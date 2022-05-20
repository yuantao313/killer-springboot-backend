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
    public List<HistoryModel> getHistories(
            @RequestParam(value = "page",required = false,defaultValue = "-1") Integer page,
            @RequestParam(value = "pageSize",required = false,defaultValue = "-1") Integer pageSize) {
        return historyService.getHistories(page,pageSize);
    }


    @DeleteMapping("/history/{id}")
    public Boolean deleteHistory(@PathVariable("id") Integer id) {
        historyService.deleteHistory(id);
        return true;
    }
}
