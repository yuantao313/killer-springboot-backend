package xyz.fumarase.killer.anlaiye.crypto;

import java.io.ByteArrayOutputStream;

import java.nio.charset.StandardCharsets;

import java.security.KeyFactory;

import java.security.interfaces.RSAPublicKey;

import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;

public class GraphToken {

    private static RSAPublicKey publicKey;


    private static byte[] decrypt(RSAPublicKey paramRSAPublicKey, byte[] paramArrayOfbyte) {
        if (paramRSAPublicKey != null) {
            try {
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(2, paramRSAPublicKey);
                int i = paramArrayOfbyte.length;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int j = 0;
                byte b = 0;
                while (true) {
                    int k = i - j;
                    if (k > 0) {
                        byte[] arrayOfByte1;
                        if (k > 128) {
                            arrayOfByte1 = cipher.doFinal(paramArrayOfbyte, j, 128);
                        } else {
                            arrayOfByte1 = cipher.doFinal(paramArrayOfbyte, j, k);
                        }
                        byteArrayOutputStream.write(arrayOfByte1, 0, arrayOfByte1.length);
                        j = ++b * 128;
                        continue;
                    }
                    byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.close();
                    return arrayOfByte;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        return paramArrayOfbyte;
    }

    public static String decryptToken(String paramString){
        byte[] arrayOfByte = Base64.decodeBase64(new String(paramString.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
        loadPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDFol7xT2yuenNBy/dJd/XD0XBX\nIatqZBNhX2H+72HiCIwev55JP/K3ZDs7Q6NC6FKEAnZk1ZLEXPhEF1w6TUXNNHJ8\nee66APMYH6CQksRaZD4ldaylXt/VM6LZLCLrZVJoCp0a7o24nvPyRm2UlxM4jbBk\nnk9ucdUY9gSdVyl1hQIDAQAB");
        return new String(Objects.requireNonNull(decrypt(getPublicKey(), arrayOfByte)), StandardCharsets.UTF_8);
    }


    public static RSAPublicKey getPublicKey() {
        return publicKey;
    }


    public static void loadPublicKey(String paramString){
        try {
            byte[] arrayOfByte = Base64.decodeBase64(paramString);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(arrayOfByte);
            publicKey = (RSAPublicKey) keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
