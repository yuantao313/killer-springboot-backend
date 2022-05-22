package xyz.fumarase.killer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.fumarase.killer.anlaiye.object.Good;
import xyz.fumarase.killer.service.MiscServiceImpl;

import java.util.HashMap;
import java.util.List;

/**
 * @author YuanTao
 */
@RestController
@CrossOrigin
public class MiscController {

    private MiscServiceImpl miscService;

    @Autowired
    public void setMiscService(MiscServiceImpl miscService) {
        this.miscService = miscService;
    }

    @GetMapping("/captcha/{userId}")
    public Boolean captcha(@PathVariable("userId") Long userId) {
        return miscService.requestCaptcha(userId);
    }

    @GetMapping("/school/{schoolId}")
    public List<HashMap<String, Object>> school(@PathVariable("schoolId") Integer schoolId) {
        return miscService.getSchool(schoolId);
    }

    @GetMapping("/info")
    public HashMap<String, Object> info() {
        return miscService.getInfo();
    }

    @GetMapping("/testNeedItem")
    public List<Good> testNeedItem(@RequestParam String keyWord, @RequestParam Integer shopId) {
        return miscService.testNeedItem(shopId, keyWord);
    }
}
