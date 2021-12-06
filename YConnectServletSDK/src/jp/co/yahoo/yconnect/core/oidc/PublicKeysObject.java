/*
 * The MIT License (MIT)
 *
 * © Yahoo Japan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package jp.co.yahoo.yconnect.core.oidc;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.json.JsonObject;
import org.apache.commons.codec.binary.Base64;

/**
 * PublicKeys Object Class
 *
 * @author © Yahoo Japan
 */
public class PublicKeysObject {

    private final Map<String, String> publicKeys;
    private JsonObject jsonObject;

    public PublicKeysObject() {
        publicKeys = new HashMap<String, String>();
    }

    /**
     * kidに紐づく公開鍵を取得します。
     *
     * @param kid kid
     * @return 公開鍵の文字列。紐づく鍵がなければnull。
     */
    public RSAPublicKey getPublicKey(String kid)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyString = publicKeys.get(kid);
        if (publicKeyString == null) {
            return null;
        }

        String publicKeyContent =
                publicKeyString
                        .replace("\n", "")
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "");

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpecX509 =
                new X509EncodedKeySpec(Base64.decodeBase64(publicKeyContent));

        return (RSAPublicKey) keyFactory.generatePublic(keySpecX509);
    }

    /**
     * 公開鍵を登録します。
     *
     * @param kid kid
     * @param publicKey 公開鍵
     */
    public void register(String kid, String publicKey) {
        publicKeys.put(kid, publicKey);
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
