package xyz.fumarase.killer.service;

import xyz.fumarase.killer.anlaiye.object.User;

import java.util.List;

/**
 * @author YuanTao
 */

public interface IUserService {
    List<User> getUsers();

    User getUser(Long userId);

    boolean addUser(Long userId, String captcha) throws Exception;
    boolean updateUser(Long userId, String captcha) throws Exception;

    Boolean deleteUser(Long userId);


}
