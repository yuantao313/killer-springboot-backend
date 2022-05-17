package xyz.fumarase.killer.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.fumarase.killer.anlaiye.Login;
import xyz.fumarase.killer.anlaiye.Manager;
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
@AllArgsConstructor
public class UserServiceImpl implements IUserService {
    private Manager manager;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    public void setManager(Manager manager) {
        this.manager = manager;
    }
    public List<User> getUsers() {
        return new ArrayList<>(manager.getUsers());
    }

    public User getUser(Long userId) {
        return manager.getUser(userId);
    }

    @Override
    public Boolean addUser(Long userId, String captcha) {
        try {
            UserModel userModel = new UserModel();
            userModel.setUserId(userId);
            HashMap<String, String> captchaResult = Login.loginWithCaptcha(userId, captcha);
            userModel.setToken(captchaResult.get("token"));
            userModel.setLoginToken(captchaResult.get("login_token"));
            manager.addUser(userModel);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
