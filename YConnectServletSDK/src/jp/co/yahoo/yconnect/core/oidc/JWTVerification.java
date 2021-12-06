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

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.zip.DataFormatException;
import org.apache.commons.codec.binary.Base64;

/**
 * JSON Web Token 検証クラス
 *
 * @author © Yahoo Japan
 */
public class JWTVerification {

    private final String idTokenString;

    private final PublicKey publicKey;

    /**
     * JWTVerificationのコンストラクタ
     *
     * @param publicKey 公開鍵
     * @param idTokenString IDトークン文字列
     */
    public JWTVerification(PublicKey publicKey, String idTokenString) {
        this.publicKey = publicKey;
        this.idTokenString = idTokenString;
    }

    /**
     * headerとpayloadの部分からsignatureを生成して一致しているかどうかを返す
     *
     * @return 検証に成功するとtrue, 失敗するとfalse
     * @throws DataFormatException IDトークンのデコードに失敗したときに発生
     */
    public boolean verifyJWT()
            throws DataFormatException, NoSuchAlgorithmException, SignatureException,
                    InvalidKeyException, UnsupportedEncodingException {
        IdTokenDecoder idTokenDecoder = new IdTokenDecoder(this.idTokenString);
        IdTokenObject idTokenObject = idTokenDecoder.decode();

        Signature verifier = getVerifier();
        byte[] signatureBytes = Base64.decodeBase64(idTokenObject.getSignature());

        return verifier.verify(signatureBytes);
    }

    private Signature getVerifier()
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException,
                    UnsupportedEncodingException {
        String[] idTokenArray = idTokenString.split("\\.");
        String dataPart = idTokenArray[0] + "." + idTokenArray[1];
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(dataPart.getBytes("UTF-8"));

        return signature;
    }
}
