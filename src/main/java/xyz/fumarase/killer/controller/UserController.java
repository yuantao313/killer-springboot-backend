package xyz.fumarase.killer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.fumarase.killer.anlaiye.object.User;
import xyz.fumarase.killer.service.UserServiceImpl;

import java.util.HashMap;
import java.util.List;

/**
 * @author YuanTao
 */
@RestController
@CrossOrigin
public class UserController {
    @Autowired
    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    private UserServiceImpl userService;

    @GetMapping("/user")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/user/{userId}")
    public User getUser(@PathVariable("userId") Long userId) {
        return userService.getUser(userId);
    }


    @PostMapping("/user/{userId}")
    public Boolean addUser(@PathVariable("userId") Long userId, @RequestBody HashMap<String, String> param) throws Exception {
        return userService.addUser(userId, param.get("captcha"));
    }

    @PutMapping("/user/{userId}")
    public Boolean updateUser(@PathVariable("userId") Long userId, @RequestBody HashMap<String, String> param) throws Exception {
        return userService.updateUser(userId, param.get("captcha"));
    }

    @DeleteMapping("/user/{userId}")
    public Boolean deleteUser(@PathVariable("userId") Long userId) {
        return userService.deleteUser(userId);
    }
}
