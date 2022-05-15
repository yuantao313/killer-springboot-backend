package xyz.fumarase.killer.service;

import xyz.fumarase.killer.anlaiye.object.User;

import java.util.List;

/**
 * @author YuanTao
 */

public interface IUserService {
    public List<User> getUser();

    public Boolean addUser(Long userId, String captcha);

    public Boolean deleteUser(Long userId);


}
