package models;

import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * @author huljas
 */
public class FacebookCookie {

    private String value;
    private boolean valid = false;
    private Data data;
    private String jsonData;

    public FacebookCookie(String value) {
        this.value = value;
    }

    /**
     * Call before anything else!
     */
    public boolean validate(String secret) {
        String[] splits = value.split(Pattern.quote("."));
        String signature = splits[0];
        String encoded = splits[1];
        jsonData = decodeBase64(encoded);
        String signature2 = digest(encoded, secret);
        if (!signature2.equals(signature)) {
            return false;
        }
        Gson gson = new Gson();
        this.data = gson.fromJson(jsonData, Data.class);
        return true;
    }

    public static String digest(String encoded, String secret) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
            hmacSha256.init(key);
            byte[] mac = hmacSha256.doFinal(encoded.getBytes("UTF-8"));
            byte[] base64 = Base64.encodeBase64(mac);
            String s = new String(base64, "UTF-8");
            return s.replace("/", "_").replace("+", "-").replace("=", "");
        } catch (NoSuchAlgorithmException e) {
            throw new Error("Never thrown", e);
        } catch (UnsupportedEncodingException e) {
            throw new Error("Never thrown", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid key", e);
        }
    }

    public static String decodeBase64(String encoded) {
        return new String(Base64.decodeBase64(encoded));
    }

    public Data data() {
        return data;
    }

    public String jsonData() {
        return jsonData;
    }

    public class Data {
        public String json;
        public String user_id;
        public String code;
    }
}
