package xyz.fumarase.killer.service;

import xyz.fumarase.killer.model.UserModel;

import java.util.List;

/**
 * @author YuanTao
 */

public interface IUserService {
    List<UserModel> getUsers();

    UserModel getUser(Long userId);

    boolean addUser(Long userId, String captcha) throws Exception;
    boolean updateUser(Long userId, String captcha) throws Exception;

    boolean deleteUser(Long userId);

}
