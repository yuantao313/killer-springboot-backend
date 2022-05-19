package xyz.fumarase.killer.anlaiye;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import xyz.fumarase.killer.anlaiye.client.Client;

class ClientTest {
    @Test
    void test() throws JsonProcessingException {
        Client client = new Client();
        System.out.println(123);
        System.out.println(client.getShop(27279));
        client.setToken("0438de2f525515d75fcd5608b7838304","0ea7b97261ad9c366b3510f18bed878e");
        System.out.println(client.getAddress());
    }
}