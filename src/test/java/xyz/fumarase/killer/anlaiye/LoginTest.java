package xyz.fumarase.killer.anlaiye;

import org.junit.jupiter.api.Test;
import xyz.fumarase.killer.anlaiye.login.Login;

class LoginTest {

    @Test
    void requestCaptcha() throws Exception {
        //Login.requestCaptcha(15064583769L);
        System.out.println(Login.loginWithCaptcha(15064583769L,"7137"));
    }
}