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

import jp.co.yahoo.yconnect.core.oidc.JWTVerification;

import javax.json.stream.JsonParsingException;
import org.junit.Test;

/**
 * JWTVerification Test Case
 *
 * @author Copyright (C) 2016 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
public class JWTVerificationTest {

  @Test
  public void testSuccess() throws DataFormatException {
    String clientSecret = "SECRET_KEY";
    String idTokenString =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvYXV0aC5sb2dpbi55YWhvby5jby5qcCIsInVzZXJfaWQiOiJVU0VSX0lEIiwiYXVkIjoiQVBQTElDQVRJT05fSUQiLCJleHAiOjE0MTE2NDcxMzksImlhdCI6MTQxMDQzNzU0MCwibm9uY2UiOiJhYmNkZWZnIn0.OLUFkAlp4BwwAxniMzXyoR5ZFC3Q5HCNHRvR6qDf-zY";
    JWTVerification verifier = new JWTVerification(clientSecret, idTokenString);
    boolean result = verifier.verifyJWT();
    assertTrue(result);
  }

  @Test(expected = JsonParsingException.class)
  public void testInvalidHeader() throws DataFormatException {
    String clientSecret = "SECRET_KEY";
    String idTokenString =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ.eyJpc3MiOiJodHRwczpcL1wvYXV0aC5sb2dpbi55YWhvby5jby5qcCIsInVzZXJfaWQiOiJVU0VSX0lEIiwiYXVkIjoiQVBQTElDQVRJT05fSUQiLCJleHAiOjE0MTE2NDcxMzksImlhdCI6MTQxMDQzNzU0MCwibm9uY2UiOiJhYmNkZWZnIn0.OLUFkAlp4BwwAxniMzXyoR5ZFC3Q5HCNHRvR6qDf-zY";
    JWTVerification verifier = new JWTVerification(clientSecret, idTokenString);
    boolean result = verifier.verifyJWT();
    assertFalse(result);
  }

  @Test(expected = JsonParsingException.class)
  public void testInvalidPayload() throws DataFormatException {
    String clientSecret = "SECRET_KEY";
    String idTokenString =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvYXV0aC5sb2dpbi55YWhvby5jby5qcCIsInVzZXJfaWQiOiJVU0VSX0lEIiwiYXVkIjoiQVBQTElDQVRJT05fSUQiLCJleHAiOjE0MTE2NDcxMzksImlhdCI6MTQxMDQzNzU0MCwibm9uY2UiOiJhYmNkZWZnIn.OLUFkAlp4BwwAxniMzXyoR5ZFC3Q5HCNHRvR6qDf-zY";
    JWTVerification verifier = new JWTVerification(clientSecret, idTokenString);
    boolean result = verifier.verifyJWT();
    assertFalse(result);
  }

  @Test
  public void testInvalidSignature() throws DataFormatException {
    String clientSecret = "SECRET_KEY";
    String idTokenString =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvYXV0aC5sb2dpbi55YWhvby5jby5qcCIsInVzZXJfaWQiOiJVU0VSX0lEIiwiYXVkIjoiQVBQTElDQVRJT05fSUQiLCJleHAiOjE0MTE2NDcxMzksImlhdCI6MTQxMDQzNzU0MCwibm9uY2UiOiJhYmNkZWZnIn0.OLUFkAlp4BwwAxniMzXyoR5ZFC3Q5HCNHRvR6qDf-z";
    JWTVerification verifier = new JWTVerification(clientSecret, idTokenString);
    boolean result = verifier.verifyJWT();
    assertFalse(result);
  }

  @Test
  public void testInvalidSecret() throws DataFormatException {
    String clientSecret = "INVALID_SECRET_KEY";
    String idTokenString =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvYXV0aC5sb2dpbi55YWhvby5jby5qcCIsInVzZXJfaWQiOiJVU0VSX0lEIiwiYXVkIjoiQVBQTElDQVRJT05fSUQiLCJleHAiOjE0MTE2NDcxMzksImlhdCI6MTQxMDQzNzU0MCwibm9uY2UiOiJhYmNkZWZnIn0.OLUFkAlp4BwwAxniMzXyoR5ZFC3Q5HCNHRvR6qDf-zY";
    JWTVerification verifier = new JWTVerification(clientSecret, idTokenString);
    boolean result = verifier.verifyJWT();
    assertFalse(result);
  }
}
