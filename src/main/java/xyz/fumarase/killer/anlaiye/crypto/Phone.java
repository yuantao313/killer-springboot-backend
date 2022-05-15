package xyz.fumarase.killer.anlaiye.crypto;


import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author YuanTao
 */
public class Phone {
    private static final String KEY = "q6%WF*1SvQx^YWzK";
    public static String decrypt(String paramString) {
        return decrypt(paramString, KEY);
    }
    public static String encrypt(String paramString) {
        return encrypt(paramString, KEY);
    }
    public static String decrypt(String paramString, String paramString2) {
        try {
            byte[] arrayOfByte2 = unHex(paramString);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(paramString2.getBytes(StandardCharsets.UTF_8), "AES");
            cipher.init(2, secretKeySpec);
            byte[] arrayOfByte1 = cipher.doFinal(arrayOfByte2);
            String str = new String(arrayOfByte1, "utf-8");
            return str.replace("\000", "");
        } catch (Exception exception) {
            return "";
        }
    }


    public static String encrypt(String paramString, String paramString2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(paramString2.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(1, secretKeySpec);
            return hex(cipher.doFinal(paramString.getBytes())).toUpperCase();
        } catch (Exception exception) {
            return "";
        }
    }

    private static String hex(byte[] paramArrayOfbyte) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b = 0; b < paramArrayOfbyte.length; b++) {
            String str1 = Integer.toHexString(paramArrayOfbyte[b] & 0xFF);
            String str2 = str1;
            if (str1.length() == 1) {
                StringBuilder stringBuilder1 = new StringBuilder();
                stringBuilder1.append("0");
                stringBuilder1.append(str1);
                str2 = stringBuilder1.toString();
            }
            stringBuilder.append(str2);
        }
        return stringBuilder.toString().trim();
    }

    private static byte[] unHex(String paramString) {
        int i = paramString.length() / 2;
        byte[] arrayOfByte = new byte[i];
        for (byte b = 0; b < i; b++) {
            int j = b * 2;
            int k = j + 1;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("0x");
            stringBuilder.append(paramString.substring(j, k));
            stringBuilder.append(paramString.substring(k, k + 1));
            arrayOfByte[b] = Byte.valueOf((byte)Integer.decode(stringBuilder.toString()).intValue()).byteValue();
        }
        return arrayOfByte;
    }
}
