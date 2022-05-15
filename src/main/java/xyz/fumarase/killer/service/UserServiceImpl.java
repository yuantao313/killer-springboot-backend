package xyz.fumarase.killer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.fumarase.killer.anlaiye.Login;
import xyz.fumarase.killer.anlaiye.Manager;
import xyz.fumarase.killer.anlaiye.object.User;
import xyz.fumarase.killer.anlaiye.object.UserBuilder;
import xyz.fumarase.killer.model.UserModel;
import xyz.fumarase.killer.mapper.UserMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author YuanTao
 */
@Service("UserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private Manager manager;

    public List<User> getUser() {
        return new ArrayList<User>(manager.getUsers().values());
    }

    @Override
    public Boolean addUser(Long userId, String captcha) {
        try {
            UserModel userModel = new UserModel();
            userModel.setUserId(userId);
            HashMap<String, String> captchaResult = Login.loginWithCaptcha(userId, captcha);
            userModel.setToken(captchaResult.get("token"));
            userModel.setLoginToken(captchaResult.get("login_token"));
            userMapper.insert(userModel);
            manager.addUser(UserBuilder.newUser().fromModel(userModel).build());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean deleteUser(Long userId) {
        try {
            userMapper.deleteById(userId);
            manager.deleteUser(userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
