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

import static org.junit.Assert.*;

import java.util.zip.DataFormatException;

import jp.co.yahoo.yconnect.core.oidc.IdTokenDecoder;
import jp.co.yahoo.yconnect.core.oidc.IdTokenObject;

import org.junit.Test;

/**
 * IdTokenDecoder Test Case
 *
 * @author Copyright (C) 2016 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
public class IdTokenDecoderTest {

  // 正常系パラメーター値
  String iss = "https://auth.login.yahoo.co.jp";
  long exp = (long) 1411647139;
  long iat = (long) 1410437540;
  String nonce = "abcdefg";
  String signature = "OLUFkAlp4BwwAxniMzXyoR5ZFC3Q5HCNHRvR6qDf-zY";
  String issuer = "https://auth.login.yahoo.co.jp";
  String authNonce = "abcdefg";
  String clientId = "APPLICATION_ID";
  String clientSecret = "SECRET_KEY";
  String type = "JWT";
  String algorithm = "HS256";

  // 正常系入力IDトークン文字列
  String idTokenString =
      "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvYXV0aC5sb2dpbi55YWhvby5jby5qcCIsInVzZXJfaWQiOiJVU0VSX0lEIiwiYXVkIjoiQVBQTElDQVRJT05fSUQiLCJleHAiOjE0MTE2NDcxMzksImlhdCI6MTQxMDQzNzU0MCwibm9uY2UiOiJhYmNkZWZnIn0.OLUFkAlp4BwwAxniMzXyoR5ZFC3Q5HCNHRvR6qDf-zY";

  @Test
  public void testDecodeIdToken() throws DataFormatException {
    IdTokenDecoder idTokenDecoder = new IdTokenDecoder(idTokenString);
    IdTokenObject idTokenObject = idTokenDecoder.decode();
    assertEquals(type, idTokenObject.getType());
    assertEquals(algorithm, idTokenObject.getAlgorithm());
    assertEquals(iss, idTokenObject.getIss());
    assertEquals(nonce, idTokenObject.getNonce());
    assertEquals(clientId, idTokenObject.getAud().get(0));
    assertEquals(exp, idTokenObject.getExp());
    assertEquals(iat, idTokenObject.getIat());
    assertEquals(signature, idTokenObject.getSignature());
  }

  @Test(expected = DataFormatException.class)
  public void testSplitArrayIndexOutOfBoundsException() throws DataFormatException {
    String idTokenString = "";
    IdTokenDecoder idTokenDecoder = new IdTokenDecoder(idTokenString);
    idTokenDecoder.decode();
  }

  @Test(expected = NullPointerException.class)
  public void testSplitError() throws DataFormatException {
    String idTokenString = null;
    IdTokenDecoder idTokenDecoder = new IdTokenDecoder(idTokenString);
    idTokenDecoder.decode();
  }
}
