package xyz.fumarase.killer.anlaiye;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import xyz.fumarase.killer.anlaiye.crypto.GraphToken;
import xyz.fumarase.killer.anlaiye.crypto.Password;
import xyz.fumarase.killer.anlaiye.crypto.Phone;

class CryptoTest {
    static final String TEST_PHONE = "13800138000";
    static final String TEST_PHONE_ENCRYPTED = "48CB46FA75F027089F4374F3E539E161";
    static final String TEST_PASSWORD = "13800138000";
    static final String TEST_PASSWORD_ENCRYPTED = "wO8QqLDOpssZ5HeSH9bsUw";
    static final String TEST_GRAPH_TOKEN = "VmZN7gpiEKUQlrtfebBdUS5GDCNO4g5G6eP81zZUgqawHbdWdO5NkFm1EkMrbHiHrzqDpZtxzsl7\nxoOgvLl9sAv8yWXhLoE8vI3OMFBqprFyFMWc9gAyU+JwqqZtYgXz6fKppuO9XnOiiembAjlNv7px\n7pTcmNCrsQc4+Cnfxo0=\n";
    static final String TEST_GRAPH_TOKEN_DECRYPTED = "131636016681965741293771dqqrwmtwqz";
    @Test
    public void test() throws Exception {
        assertEquals(TEST_PHONE_ENCRYPTED, Phone.encrypt(TEST_PHONE));
        assertEquals(TEST_PHONE, Phone.decrypt(TEST_PHONE_ENCRYPTED));
        assertEquals(TEST_PASSWORD_ENCRYPTED, Password.encrypt(TEST_PASSWORD));
        assertEquals(TEST_PASSWORD, Password.decrypt(TEST_PASSWORD_ENCRYPTED));
        assertEquals(TEST_GRAPH_TOKEN_DECRYPTED, GraphToken.decryptToken(TEST_GRAPH_TOKEN));
    }
}