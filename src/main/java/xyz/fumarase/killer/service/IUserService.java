package xyz.fumarase.killer.service;

import xyz.fumarase.killer.anlaiye.object.User;

import java.util.List;

/**
 * @author YuanTao
 */

public interface IUserService {
    List<User> getUsers();

    User getUser(Long userId);

    Boolean addUser(Long userId, String captcha);

    Boolean deleteUser(Long userId);


}
