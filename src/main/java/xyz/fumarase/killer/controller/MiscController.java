package xyz.fumarase.killer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import xyz.fumarase.killer.model.ConfigModel;
import xyz.fumarase.killer.response.BaseResponse;
import xyz.fumarase.killer.service.MiscServiceImpl;

import java.util.HashMap;
import java.util.List;

/**
 * @author YuanTao
 */
@BaseResponse
@RestController
@CrossOrigin
public class MiscController {
    @Autowired
    private MiscServiceImpl miscService;


    @GetMapping("/captcha/{userId}")
    public Boolean captcha(@PathVariable("userId") Long userId) {
        return miscService.requestCaptcha(userId);
    }

    @GetMapping("/school/{schoolId}")
    public List<HashMap<String,Object>> school(@PathVariable("schoolId") Integer schoolId) {
        return miscService.getSchool(schoolId);
    }

    @GetMapping("/config/{adminId}")
    public ConfigModel config(@PathVariable("adminId") Integer adminId) {
        return miscService.getConfig(adminId);
    }

    @GetMapping("/info")
    public HashMap<String, Object> info() {
        return miscService.getInfo();
    }
}
