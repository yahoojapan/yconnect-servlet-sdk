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

package jp.co.yahoo.yconnect.test;

import static org.junit.Assert.*;

import java.util.zip.DataFormatException;

import jp.co.yahoo.yconnect.core.oidc.IdTokenDecoder;
import jp.co.yahoo.yconnect.core.oidc.IdTokenObject;
import jp.co.yahoo.yconnect.core.oidc.IdTokenVerification;

import org.junit.Test;

/**
 * IdTokenVerification Test Case
 *
 * @author Copyright (C) 2016 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
public class IdTokenVerificationTest {

  // 正常系パラメーター値
  String iss = "https://auth.login.yahoo.co.jp";
  long exp = (long) 1411647139;
  long iat = (long) 1410437540;
  String nonce = "abcdefg";
  String issuer = "https://auth.login.yahoo.co.jp";
  String authNonce = "abcdefg";
  String clientId = "APPLICATION_ID";
  String clientSecret = "SECRET_KEY";

  // 正常系入力IDトークン文字列
  String idTokenString =
      "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvYXV0aC5sb2dpbi55YWhvby5jby5qcCIsInVzZXJfaWQiOiJVU0VSX0lEIiwiYXVkIjoiQVBQTElDQVRJT05fSUQiLCJleHAiOjE0MTE2NDcxMzksImlhdCI6MTQxMDQzNzU0MCwibm9uY2UiOiJhYmNkZWZnIn0.OLUFkAlp4BwwAxniMzXyoR5ZFC3Q5HCNHRvR6qDf-zY";

  @Test
  public void testCheckStringStringStringIdTokenObject() throws DataFormatException {
    IdTokenDecoder idTokenDecoder = new IdTokenDecoder(idTokenString);
    IdTokenObject idTokenObject = idTokenDecoder.decode();
    IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
    assertTrue(idTokenVer.check(issuer, authNonce, clientId, idTokenObject, clientSecret,
        idTokenString));
  }

  @Test
  public void testGetCheckErrorMessageType() throws DataFormatException {
    String idTokenString =
        "eyJ0eXAiOiJJTlZBTElEX0pXVCIsImFsZyI6IkhTMjU2In0.eyJpc3MiOiJodHRwczpcL1wvYXV0aC5sb2dpbi55YWhvby5jby5qcCIsInVzZXJfaWQiOiJVU0VSX0lEIiwiYXVkIjoiQVBQTElDQVRJT05fSUQiLCJleHAiOjE0MTE2NDcxMzksImlhdCI6MTQxMDQzNzU0MCwibm9uY2UiOiJhYmNkZWZnIn0.2SQProJa2GhsLTtSpdJz1FT0gnlTIvHTvcijxovxucY";
    IdTokenDecoder idTokenDecoder = new IdTokenDecoder(idTokenString);
    IdTokenObject idTokenObject = idTokenDecoder.decode();
    IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
    assertFalse(idTokenVer.check(issuer, authNonce, clientId, idTokenObject, clientSecret,
        idTokenString));
    assertEquals(idTokenVer.getErrorMessage(), "invalid_type");
    assertEquals(idTokenVer.getErrorDescriptionMessage(), "The type is not JWT. (INVALID_JWT)");
  }

  @Test
  public void testGetCheckErrorMessageAlgorithm() throws DataFormatException {
    String idTokenString =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJJTlZBTElEX0FMR09SSVRITSJ9.eyJpc3MiOiJodHRwczpcL1wvYXV0aC5sb2dpbi55YWhvby5jby5qcCIsInVzZXJfaWQiOiJVU0VSX0lEIiwiYXVkIjoiQVBQTElDQVRJT05fSUQiLCJleHAiOjE0MTE2NDcxMzksImlhdCI6MTQxMDQzNzU0MCwibm9uY2UiOiJhYmNkZWZnIn0.tjWFVLEeA-RQd6cu1jmgTFYHX0Fv_U4fm3YqP3coLC0";
    IdTokenDecoder idTokenDecoder = new IdTokenDecoder(idTokenString);
    IdTokenObject idTokenObject = idTokenDecoder.decode();
    IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
    assertFalse(idTokenVer.check(issuer, authNonce, clientId, idTokenObject, clientSecret,
        idTokenString));
    assertEquals(idTokenVer.getErrorMessage(), "invalid_algorithm");
    assertEquals(idTokenVer.getErrorDescriptionMessage(),
        "The algorithm is not HmacSHA256. (INVALID_ALGORITHM)");

  }

  @Test
  public void testGetCheckErrorMessageIssuer() throws DataFormatException {
    String idTokenString =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvZXhhbXBsZS5jb20iLCJ1c2VyX2lkIjoiVVNFUl9JRCIsImF1ZCI6IkFQUExJQ0FUSU9OX0lEIiwiZXhwIjoxNDExNjQ3MTM5LCJpYXQiOjE0MTA0Mzc1NDAsIm5vbmNlIjoiYWJjZGVmZyJ9.xYo7dQJegGusaAHo0MbtSyxTYsBYe8N2DizbESK3pjA";
    IdTokenDecoder idTokenDecoder = new IdTokenDecoder(idTokenString);
    IdTokenObject idTokenObject = idTokenDecoder.decode();
    IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
    assertFalse(idTokenVer.check(issuer, authNonce, clientId, idTokenObject, clientSecret,
        idTokenString));
    assertEquals(idTokenVer.getErrorMessage(), "invalid_issuer");
    assertEquals(idTokenVer.getErrorDescriptionMessage(), "The issuer did not match. ("
        + "https://example.com" + ")");
  }

  @Test
  public void testGetCheckErrorMessageNonce() throws DataFormatException {
    String idTokenString =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvYXV0aC5sb2dpbi55YWhvby5jby5qcCIsInVzZXJfaWQiOiJVU0VSX0lEIiwiYXVkIjoiQVBQTElDQVRJT05fSUQiLCJleHAiOjE0MTE2NDcxMzksImlhdCI6MTQxMDQzNzU0MCwibm9uY2UiOiJJTlZBTElEX05PTkNFIn0.ZPnj8nUYnrLKSMbDjV9ZynDgwoOft4QUU064xKwzUvs";
    IdTokenDecoder idTokenDecoder = new IdTokenDecoder(idTokenString);
    IdTokenObject idTokenObject = idTokenDecoder.decode();
    IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
    assertFalse(idTokenVer.check(issuer, authNonce, clientId, idTokenObject, clientSecret,
        idTokenString));
    assertEquals(idTokenVer.getErrorMessage(), "not_match_nonce");
    assertEquals(idTokenVer.getErrorDescriptionMessage(),
        "The nonce did not match. (INVALID_NONCE)");
  }

  @Test
  public void testGetCheckErrorMessageAudience() throws DataFormatException {
    String idTokenString =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvYXV0aC5sb2dpbi55YWhvby5jby5qcCIsInVzZXJfaWQiOiJVU0VSX0lEIiwiYXVkIjoiSU5WQUxJRF9BUFBMSUNBVElPTl9JRCIsImV4cCI6MTQxMTY0NzEzOSwiaWF0IjoxNDEwNDM3NTQwLCJub25jZSI6ImFiY2RlZmcifQ.8O_O87NP0r1T8V6TpF0-3KseGPzK2176NDYP1XCmmOU";
    IdTokenDecoder idTokenDecoder = new IdTokenDecoder(idTokenString);
    IdTokenObject idTokenObject = idTokenDecoder.decode();
    IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
    assertFalse(idTokenVer.check(issuer, authNonce, clientId, idTokenObject, clientSecret,
        idTokenString));
    assertEquals(idTokenVer.getErrorMessage(), "invalid_audience");
    assertEquals(idTokenVer.getErrorDescriptionMessage(),
        "The client id did not match. (INVALID_APPLICATION_ID)");

  }

  @Test
  public void testGetCheckErrorMessageExpired() throws DataFormatException {
    String idTokenString =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvYXV0aC5sb2dpbi55YWhvby5jby5qcCIsInVzZXJfaWQiOiJVU0VSX0lEIiwiYXVkIjoiQVBQTElDQVRJT05fSUQiLCJleHAiOjEzMTE2NDcxMzksImlhdCI6MTQxMDQzNzU0MCwibm9uY2UiOiJhYmNkZWZnIn0.UPZxsM_IEXBXe-pf84PqcLnyEcbYpX2VAI6Il_8_kfk";
    IdTokenDecoder idTokenDecoder = new IdTokenDecoder(idTokenString);
    IdTokenObject idTokenObject = idTokenDecoder.decode();
    IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
    assertFalse(idTokenVer.check(issuer, authNonce, clientId, idTokenObject, clientSecret,
        idTokenString));
    assertEquals(idTokenVer.getErrorMessage(), "expired_id_token");
    assertEquals(idTokenVer.getErrorDescriptionMessage(), "Re-issue Id Token. (1311647139)");

  }

  @Test
  public void testGetCheckErrorMessageOverAcceptableRange() throws DataFormatException {
    String idTokenString =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvYXV0aC5sb2dpbi55YWhvby5jby5qcCIsInVzZXJfaWQiOiJVU0VSX0lEIiwiYXVkIjoiQVBQTElDQVRJT05fSUQiLCJleHAiOjE0MTE2NDcxMzksImlhdCI6MTMxMDQzNzU0MCwibm9uY2UiOiJhYmNkZWZnIn0.O-mfBzK-oPf9Qz50QOwRDn75JUHhwP4Jw0PZU-UZeJM";
    IdTokenDecoder idTokenDecoder = new IdTokenDecoder(idTokenString);

    IdTokenObject idTokenObject = idTokenDecoder.decode();
    IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
    assertFalse(idTokenVer.check(issuer, authNonce, clientId, idTokenObject, clientSecret,
        idTokenString));
    assertEquals(idTokenVer.getErrorMessage(), "over_acceptable_range");
    assertEquals(idTokenVer.getErrorDescriptionMessage(),
        "This access has expired possible. (1310437540)");
  }

  @Test
  public void testGetCheckErrorMessageCheckSignature() throws DataFormatException {
    String idTokenString =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvYXV0aC5sb2dpbi55YWhvby5jby5qcCIsInVzZXJfaWQiOiJVU0VSX0lEIiwiYXVkIjoiQVBQTElDQVRJT05fSUQiLCJleHAiOjE0MTE2NDcxMzksImlhdCI6MTQxMDQzNzU0MCwibm9uY2UiOiJhYmNkZWZnIn0.p7RNhgD2IorhOQd_SxMZ80_LyIL2eJtQEE1ODD44d7k";
    IdTokenDecoder idTokenDecoder = new IdTokenDecoder(idTokenString);
    IdTokenObject idTokenObject = idTokenDecoder.decode();
    IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
    assertFalse(idTokenVer.check(issuer, authNonce, clientId, idTokenObject, clientSecret,
        idTokenString));
    assertEquals(idTokenVer.getErrorMessage(), "invalid_signature");
    assertEquals(idTokenVer.getErrorDescriptionMessage(), "Signature verification failed.");
  }

  public class IdTokenVerificationCurrentTimeTest extends IdTokenVerification {
    private long curretTime;

    public IdTokenVerificationCurrentTimeTest(long currentTime) {
      this.curretTime = currentTime;
    }

    protected long getCurrentTime() {
      return this.curretTime;
    }
  }
}
