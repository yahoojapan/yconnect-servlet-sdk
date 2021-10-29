/*
 * The MIT License (MIT)
 *
 * Copyright (C) 2016 Yahoo Japan Corporation. All Rights Reserved.
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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.DataFormatException;
import javax.json.*;
import org.apache.commons.codec.binary.Base64;

/**
 * IdToken Decoder Class
 *
 * @author Copyright (C) 2016 Yahoo Japan Corporation. All Rights Reserved.
 */
public class IdTokenDecoder {

    private final String idTokenString;

    /**
     * IdTokenDecoderコンストラクタ
     *
     * @param idTokenString IDトークン文字列
     */
    public IdTokenDecoder(String idTokenString) {
        this.idTokenString = idTokenString;
    }

    /**
     * IdTokenのデコード
     *
     * @return IdTokenObject
     * @throws DataFormatException 入力された文字列がJWTフォーマットではないときに発生
     */
    public IdTokenObject decode() throws DataFormatException {

        HashMap<String, String> idToken = this.splitIdToken();

        // Header
        String jsonHeader = idToken.get("header");

        JsonReader jsonHeaderReader = Json.createReader(new StringReader(jsonHeader));
        JsonObject rootHeader = jsonHeaderReader.readObject();
        jsonHeaderReader.close();

        JsonString typeString = rootHeader.getJsonString("typ");
        String type = typeString.getString();

        JsonString algorithmString = rootHeader.getJsonString("alg");
        String algorithm = algorithmString.getString();

        JsonString kidString = rootHeader.getJsonString("kid");
        String kid = kidString.getString();

        // Payload
        String jsonPayload = idToken.get("payload");

        JsonReader jsonPayloadReader = Json.createReader(new StringReader(jsonPayload));

        JsonObject rootPayload = jsonPayloadReader.readObject();
        jsonPayloadReader.close();

        JsonString issString = rootPayload.getJsonString("iss");
        String iss = issString.getString();

        JsonString subString = rootPayload.getJsonString("sub");
        String sub = subString.getString();

        String ppidSub;
        try {
            ppidSub = rootPayload.getString("ppid_sub");
        } catch (NullPointerException ex) {
            ppidSub = null;
        }

        JsonArray audStringArr = rootPayload.getJsonArray("aud");
        ArrayList<String> aud = new ArrayList<String>();
        for (JsonValue audValue : audStringArr) {
            JsonString audString = (JsonString) audValue;
            aud.add(audString.getString());
        }

        JsonNumber expString = rootPayload.getJsonNumber("exp");
        int exp = expString.intValue();

        JsonNumber iatString = rootPayload.getJsonNumber("iat");
        int iat = iatString.intValue();

        JsonNumber authTimeString = rootPayload.getJsonNumber("auth_time");
        long authTime = 0;
        if (authTimeString != null) {
            authTime = authTimeString.longValue();
        }

        JsonString nonceString = rootPayload.getJsonString("nonce");
        String nonce = nonceString.getString();

        String atHash;
        try {
            atHash = rootPayload.getString("at_hash");
        } catch (NullPointerException ex) {
            atHash = null;
        }

        // signature
        String signature = idToken.get("signature");

        // デコードした値を格納
        return new IdTokenObject(
                type, algorithm, kid, iss, sub, ppidSub, aud, nonce, atHash, exp, iat, authTime,
                signature);
    }

    private HashMap<String, String> splitIdToken() throws DataFormatException {
        String[] idTokens = this.idTokenString.split("\\.");

        if (idTokens.length != 3) {
            throw new DataFormatException();
        }

        // 分割したidTokenを格納
        HashMap<String, String> idTokenMap = new HashMap<String, String>();
        idTokenMap.put("header", new String(Base64.decodeBase64(idTokens[0].getBytes())));
        idTokenMap.put("payload", new String(Base64.decodeBase64(idTokens[1].getBytes())));
        idTokenMap.put("signature", idTokens[2]);

        return idTokenMap;
    }
}
