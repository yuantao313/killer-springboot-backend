package xyz.fumarase.killer.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.fumarase.killer.anlaiye.login.Login;
import xyz.fumarase.killer.anlaiye.Manager;
import xyz.fumarase.killer.anlaiye.login.exception.LoginException;
import xyz.fumarase.killer.anlaiye.object.User;
import xyz.fumarase.killer.model.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author YuanTao
 */
@Service("UserService")
@NoArgsConstructor
public class UserServiceImpl implements IUserService {
    private Manager manager;

    @Autowired
    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public List<User> getUsers() {
        return manager.getUsers();
    }

    public User getUser(Long userId) {
        return manager.getUser(userId);
    }

    @Override
    public boolean addUser(Long userId, String captcha) throws Exception {
        UserModel userModel = new UserModel();
        userModel.setUserId(userId);
        HashMap<String, String> captchaResult = Login.loginWithCaptcha(userId, captcha);
        userModel.setToken(captchaResult.get("token"));
        userModel.setLoginToken(captchaResult.get("login_token"));
        manager.addUser(userModel);
        return true;
    }

    @Override
    public boolean updateUser(Long userId, String captcha) throws Exception {
        UserModel userModel = new UserModel();
        userModel.setUserId(userId);
        HashMap<String, String> captchaResult = Login.loginWithCaptcha(userId, captcha);
        userModel.setToken(captchaResult.get("token"));
        userModel.setLoginToken(captchaResult.get("login_token"));
        manager.updateUser(userModel);
        return true;
    }

    @Override
    public Boolean deleteUser(Long userId) {
        try {
            manager.deleteUser(userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
