package xyz.fumarase.killer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import xyz.fumarase.killer.service.MiscServiceImpl;

import java.util.HashMap;

@RestController
@CrossOrigin
public class MiscController {
    @Autowired
    private MiscServiceImpl miscService;


    @GetMapping("/captcha/{userId}")
    public MyResponse captcha(@PathVariable("userId") Long userId) {
        if (miscService.requestCaptcha(userId)) {
            return new MyResponse(true);
        } else {
            return new MyResponse(false);
        }
    }

    @GetMapping("/school/{schoolId}")
    public MyResponse school(@PathVariable("schoolId") Integer schoolId) {
        HashMap<String, Object> data = new HashMap<>(1);
        data.put("shops", miscService.getSchool(schoolId));
        return new MyResponse(true, data);
    }

    @GetMapping("/config/{adminId}")
    public MyResponse config(@PathVariable("adminId") Integer adminId) {
        HashMap<String, Object> data = new HashMap<>(1);
        data.put("config", miscService.getConfig(adminId));
        return new MyResponse(true, data);
    }
}
