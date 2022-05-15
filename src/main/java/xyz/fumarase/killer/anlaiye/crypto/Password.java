package xyz.fumarase.killer.anlaiye.crypto;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author YuanTao
 */
public class Password {
    public static final String APP_LOGIN_IN_KEY = "Kbm.543Lbwb5kNbP";

    public static final String GIV = "B*L032Ykls9-g852";

    public static String decrypt(String paramString) throws InvalidAlgorithmParameterException, UnsupportedEncodingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return decrypt(paramString, APP_LOGIN_IN_KEY, GIV);
    }

    public static String encrypt(String paramString1) throws Exception {
        return encrypt2(paramString1, APP_LOGIN_IN_KEY, GIV);
    }

    public static String decrypt(String paramString1, String paramString2, String paramString3) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] arrayOfByte = Base64.getDecoder().decode(paramString1);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(paramString3.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec secretKeySpec = new SecretKeySpec(paramString2.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(2, secretKeySpec, ivParameterSpec);
        return new String(cipher.doFinal(arrayOfByte), StandardCharsets.UTF_8);
    }

    public static String encrypt(String paramString1, String paramString2, String paramString3) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] arrayOfByte = paramString1.getBytes(StandardCharsets.UTF_8);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(paramString3.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec secretKeySpec = new SecretKeySpec(paramString2.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(1, secretKeySpec, ivParameterSpec);
        return replaceStr(Base64.getEncoder().encodeToString(cipher.doFinal(arrayOfByte)).replace("\n", "").trim());
    }

    public static String replaceStr(String paramString) {
        return paramString.replace("+", "__2B").replace("/", "__2F").replace("=", "");
    }

    public static String encrypt2(String paramString1, String paramString2,String paramString3) throws Exception {
        if (paramString1 != null) {
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                int i = cipher.getBlockSize();
                byte[] arrayOfByte2 = paramString1.getBytes();
                int j = arrayOfByte2.length;
                int k = j;
                if (j % i != 0) {
                    k = j + i - j % i;
                }
                byte[] arrayOfByte1 = new byte[k];
                System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, arrayOfByte2.length);
                SecretKeySpec secretKeySpec = new SecretKeySpec(paramString2.getBytes(), "AES");
                IvParameterSpec ivParameterSpec = new IvParameterSpec(paramString3.getBytes());
                cipher.init(1, secretKeySpec, ivParameterSpec);
                return replaceStr(Base64.getEncoder().encodeToString(cipher.doFinal(arrayOfByte1)).replace("\n", "").trim());
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }
        return "";
    }
}
