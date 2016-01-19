/**
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

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;

import org.apache.commons.codec.binary.Base64;

/**
 * IdToken Decoder Class
 *
 * @author Copyright (C) 2016 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
public class IdTokenDecoder {

  private String idTokenString;

  /**
   * IdTokenDecoderコンストラクタ
   * 
   * @param clientSecret
   * @param idTokenString
   */
  public IdTokenDecoder(String idTokenString) {
    this.idTokenString = idTokenString;
  }

  /**
   * IdTokenのデコード
   * 
   * @return IdTokenObject
   * @throws DataFormatException
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

    // Payload
    String jsonPayload = idToken.get("payload");

    JsonReader jsonPayloadReader = Json.createReader(new StringReader(jsonPayload));

    JsonObject rootPayload = jsonPayloadReader.readObject();
    jsonPayloadReader.close();

    JsonString issString = rootPayload.getJsonString("iss");
    String iss = issString.getString();

    JsonString userIdString = rootPayload.getJsonString("user_id");
    String userId = userIdString.getString();

    JsonString audString = rootPayload.getJsonString("aud");
    ArrayList<String> aud = new ArrayList<String>();
    aud.add(audString.getString());

    JsonNumber expString = rootPayload.getJsonNumber("exp");
    int exp = expString.intValue();

    JsonNumber iatString = rootPayload.getJsonNumber("iat");
    int iat = iatString.intValue();

    JsonString nonceString = rootPayload.getJsonString("nonce");
    String nonce = nonceString.getString();

    // signature
    String signature = idToken.get("signature");

    // デコードした値を格納
    return new IdTokenObject(type, algorithm, iss, userId, aud, nonce, exp, iat, signature);
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
