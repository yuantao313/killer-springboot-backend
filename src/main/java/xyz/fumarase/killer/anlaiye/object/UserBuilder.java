package xyz.fumarase.killer.anlaiye.object;

import xyz.fumarase.killer.model.UserModel;

/**
 * @author YuanTao
 */
public class UserBuilder {
    private String token;
    private String loginToken;
    private Long userId;

    public static UserBuilder newUser() {
        return new UserBuilder();
    }


    public UserBuilder setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public UserBuilder setToken(String token, String loginToken) {
        this.token = token;
        this.loginToken = loginToken;
        return this;
    }

    public UserBuilder fromModel(UserModel userModel) {
        this.userId = userModel.getUserId();
        this.token = userModel.getToken();
        this.loginToken = userModel.getLoginToken();
        return this;
    }

    public User build() {
        return new User(userId, token, loginToken);
    }

}
