package xyz.fumarase.killer.service;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.fumarase.killer.anlaiye.login.Login;
import xyz.fumarase.killer.object.Manager;
import xyz.fumarase.killer.model.UserModel;

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
    public List<UserModel> getUsers() {
        List<UserModel> userModel = manager.getUsers();
        return userModel;
    }

    public UserModel getUser(Long userId) {
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
    public boolean deleteUser(Long userId) {
        try {
            manager.deleteUser(userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
