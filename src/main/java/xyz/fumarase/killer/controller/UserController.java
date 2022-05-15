package xyz.fumarase.killer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.fumarase.killer.service.UserServiceImpl;

import java.util.HashMap;

/**
 * @author YuanTao
 */
@RestController
@CrossOrigin
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/user")
    public MyResponse user() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("users", userService.getUser());
        return new MyResponse(true, result);
    }

    @PostMapping("/user/{userId}")
    public MyResponse user(@PathVariable("userId") Long userId, @RequestBody HashMap<String, String> param) {
        if (userService.addUser(userId, param.get("captcha"))) {
            return new MyResponse(true);
        } else {
            return new MyResponse(false);
        }
    }



    @DeleteMapping("/user/{userId}")
    public MyResponse user(@PathVariable("userId") Long userId) {
        if (userService.deleteUser(userId)) {
            return new MyResponse(true);
        } else {
            return new MyResponse(false);
        }
    }
}
