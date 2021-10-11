/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2021 Yahoo Japan Corporation. All Rights Reserved.
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

import jp.co.yahoo.yconnect.util.IdTokenGenerator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.DataFormatException;

import static org.junit.Assert.assertEquals;

/**
 * IdTokenDecoder Test Case
 *
 * @author Copyright (C) 2021 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
public class IdTokenDecoderTest {

  // 正常系パラメーター値
  String iss = "https://auth.login.yahoo.co.jp/yconnect/v2";
  String sub = "USER_PPID";
  long exp = 1411647139;
  long iat = 1410437540;
  long authTime = 1410437541;
  String nonce = "abcdefg";
  String atHash = "at_abcde";
  String signature;
  String clientId = "APPLICATION_ID";
  String type = "JWT";
  String algorithm = "RS256";
  String kid = "sample_kid";

  @Test
  public void testDecodeIdToken() throws Exception {
    IdTokenObject expected = new IdTokenObject();
    expected.setType(type);
    expected.setAlgorithm(algorithm);
    expected.setKid(kid);
    expected.setIss(iss);
    expected.setSub(sub);
    expected.setAud(new ArrayList<>(Collections.singletonList(clientId)));
    expected.setNonce(nonce);
    expected.setAtHash(atHash);
    expected.setExp(exp);
    expected.setIat(iat);
    expected.setAuthTime(authTime);

    String idTokenString = new IdTokenGenerator(expected).getIdTokenString();
    signature = idTokenString.split("\\.")[2];

    IdTokenDecoder idTokenDecoder = new IdTokenDecoder(idTokenString);
    IdTokenObject idTokenObject = idTokenDecoder.decode();
    assertEquals(type, idTokenObject.getType());
    assertEquals(algorithm, idTokenObject.getAlgorithm());
    assertEquals(kid, idTokenObject.getKid());
    assertEquals(iss, idTokenObject.getIss());
    assertEquals(sub, idTokenObject.getSub());
    assertEquals(clientId, idTokenObject.getAud().get(0));
    assertEquals(nonce, idTokenObject.getNonce());
    assertEquals(atHash, idTokenObject.getAtHash());
    assertEquals(exp, idTokenObject.getExp());
    assertEquals(iat, idTokenObject.getIat());
    assertEquals(authTime, idTokenObject.getAuthTime());
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
