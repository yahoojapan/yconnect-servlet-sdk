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

import org.junit.BeforeClass;
import org.junit.Test;

import javax.json.stream.JsonParsingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * JWTVerification Test Case
 *
 * @author Copyright (C) 2021 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
public class JWTVerificationTest {

  private static PrivateKey privateKey;
  private static PublicKey publicKey;
  private static String base64Header;
  private static String base64Payload;
  private static String base64Signature;

  @BeforeClass
  public static void beforeAll() throws Exception {
    KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
    keyPairGen.initialize(2048);

    KeyPair pair = keyPairGen.generateKeyPair();
    privateKey = pair.getPrivate();

    String header = "{\"typ\": \"JWT\", \"alg\": \"RS256\", \"kid\": \"sample-kid\"}";
    base64Header = Base64.getUrlEncoder().encodeToString(header.getBytes(StandardCharsets.UTF_8))
            .replace("=", "");

    String payload = "{\"iss\": \"sample/iss\", \"sub\": \"SampleSub\", \"aud\": [\"SampleAud\"]," +
            "\"exp\": 1234567890, \"iat\": 1234567890, \"nonce\": \"sample_nonce\"}";
    base64Payload = Base64.getUrlEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8))
            .replace("=", "");

    base64Signature = getBase64Signature(base64Header, base64Payload, privateKey);

    publicKey = pair.getPublic();
  }

  @Test
  public void testSuccess() throws Exception {
    String idTokenString = base64Header + "." + base64Payload + "." + base64Signature;
    JWTVerification verifier = new JWTVerification(publicKey, idTokenString);
    boolean result = verifier.verifyJWT();
    assertTrue(result);
  }

  @Test(expected = JsonParsingException.class)
  public void testInvalidHeader() throws Exception {
    String header = "{\"typ\": \"JWT\", \"alg\": \"RS256\", \"kid\": \"sample-kid\"";
    String base64Header = Base64.getUrlEncoder().encodeToString(header.getBytes(StandardCharsets.UTF_8))
            .replace("=", "");
    String base64Signature = getBase64Signature(base64Header, base64Payload, privateKey);

    String idTokenString = base64Header + "." + base64Payload + "." + base64Signature;
    JWTVerification verifier = new JWTVerification(publicKey, idTokenString);
    boolean result = verifier.verifyJWT();
    assertFalse(result);
  }

  @Test(expected = JsonParsingException.class)
  public void testInvalidPayload() throws Exception {
    String payload = "{\"iss\": \"sample/iss\", \"sub\": \"SampleSub\", \"aud\": [\"SampleAud\"]," +
            "\"exp\": 1234567890, \"iat\": 1234567890, \"nonce\": \"sample_nonce\"";
    String base64Payload = Base64.getUrlEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8))
            .replace("=", "");
    String base64Signature = getBase64Signature(base64Header, base64Payload, privateKey);

    String idTokenString = base64Header + "." + base64Payload + "." + base64Signature;
    JWTVerification verifier = new JWTVerification(publicKey, idTokenString);
    boolean result = verifier.verifyJWT();
    assertFalse(result);
  }

  @Test
  public void testInvalidSignature() throws Exception {
    String base64Signature = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(String.join("", Collections.nCopies(64, "sign"))
                    .getBytes(StandardCharsets.UTF_8));
    String idTokenString = base64Header + "." + base64Payload + "." + base64Signature;
    JWTVerification verifier = new JWTVerification(publicKey, idTokenString);
    boolean result = verifier.verifyJWT();
    assertFalse(result);
  }

  @Test
  public void testInvalidPublicKey() throws Exception {
    KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
    keyPairGen.initialize(2048);
    KeyPair pair = keyPairGen.generateKeyPair();
    PublicKey publicKey = pair.getPublic();

    String idTokenString = base64Header + "." + base64Payload + "." + base64Signature;
    JWTVerification verifier = new JWTVerification(publicKey, idTokenString);
    boolean result = verifier.verifyJWT();
    assertFalse(result);
  }

  private static String getBase64Signature(String base64Header, String base64Payload, PrivateKey privateKey) throws Exception {
    Signature sign = Signature.getInstance("SHA256withRSA");
    sign.initSign(privateKey);
    sign.update(base64Header.getBytes(StandardCharsets.UTF_8));
    sign.update(".".getBytes(StandardCharsets.UTF_8));
    sign.update(base64Payload.getBytes(StandardCharsets.UTF_8));

    return Base64.getUrlEncoder().withoutPadding().encodeToString(sign.sign());
  }
}
