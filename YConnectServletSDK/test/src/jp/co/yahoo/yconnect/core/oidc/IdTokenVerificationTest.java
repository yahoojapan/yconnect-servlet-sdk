/*
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import jp.co.yahoo.yconnect.util.IdTokenGenerator;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * IdTokenVerification Test Case
 *
 * @author Copyright (C) 2021 Yahoo Japan Corporation. All Rights Reserved.
 */
public class IdTokenVerificationTest {

    // 正常系パラメーター値
    String iss = "https://auth.login.yahoo.co.jp/yconnect/v2";
    String sub = "USER_PPID";
    long exp = 1411647139;
    long iat = 1410437540;
    long authTime = 1410437541;
    String nonce = "abcdefg";
    String atHash;
    String clientId = "APPLICATION_ID";
    String type = "JWT";
    String algorithm = "RS256";
    String kid = "sample_kid";

    String accessToken = "access_token";

    IdTokenObject idTokenObject = new IdTokenObject();

    @Before
    public void beforeEach() {
        idTokenObject.setType(type);
        idTokenObject.setAlgorithm(algorithm);
        idTokenObject.setKid(kid);
        idTokenObject.setSub(sub);
        idTokenObject.setIss(iss);
        idTokenObject.setAud(new ArrayList<>(Collections.singletonList(clientId)));
        idTokenObject.setExp(exp);
        idTokenObject.setIat(iat);
        idTokenObject.setAuthTime(authTime);
        idTokenObject.setNonce(nonce);
        atHash = generateHash(accessToken);
        idTokenObject.setAtHash(atHash);
    }

    @Test
    public void testCheckReturnTrue() throws Exception {
        IdTokenGenerator idTokenGenerator = new IdTokenGenerator(idTokenObject);
        String idTokenString = idTokenGenerator.getIdTokenString();
        IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
        PublicKeysObject publicKeysObject = new PublicKeysObject();
        publicKeysObject.register(
                kid,
                Base64.getEncoder()
                        .withoutPadding()
                        .encodeToString(idTokenGenerator.getPublicKey().getEncoded()));

        assertTrue(
                idTokenVer.check(
                        iss,
                        nonce,
                        clientId,
                        idTokenObject,
                        publicKeysObject,
                        idTokenString,
                        accessToken));
    }

    @Test
    public void testCheckReturnTrueWithoutAtHash() throws Exception {
        idTokenObject.setAtHash(null);
        IdTokenGenerator idTokenGenerator = new IdTokenGenerator(idTokenObject);
        String idTokenString = idTokenGenerator.getIdTokenString();
        IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
        PublicKeysObject publicKeysObject = new PublicKeysObject();
        publicKeysObject.register(
                kid,
                Base64.getEncoder()
                        .withoutPadding()
                        .encodeToString(idTokenGenerator.getPublicKey().getEncoded()));

        assertTrue(
                idTokenVer.check(
                        iss,
                        nonce,
                        clientId,
                        idTokenObject,
                        publicKeysObject,
                        idTokenString,
                        "INVALID_ACCESS_TOKEN"));
    }

    @Test
    public void testGetCheckErrorMessageAccessToken() throws Exception {
        IdTokenGenerator idTokenGenerator = new IdTokenGenerator(idTokenObject);
        String idTokenString = idTokenGenerator.getIdTokenString();
        IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
        PublicKeysObject publicKeysObject = new PublicKeysObject();
        publicKeysObject.register(
                kid,
                Base64.getEncoder()
                        .withoutPadding()
                        .encodeToString(idTokenGenerator.getPublicKey().getEncoded()));

        assertFalse(
                idTokenVer.check(
                        iss,
                        nonce,
                        clientId,
                        idTokenObject,
                        publicKeysObject,
                        idTokenString,
                        "INVALID_ACCESS_TOKEN"));
        assertEquals(idTokenVer.getErrorMessage(), "invalid_at_hash");
        assertEquals(
                idTokenVer.getErrorDescriptionMessage(),
                "The at_hash did not match. (" + atHash + ")");
    }

    @Test
    public void testGetCheckErrorMessageType() throws Exception {
        idTokenObject.setType("INVALID_JWT");
        IdTokenGenerator idTokenGenerator = new IdTokenGenerator(idTokenObject);
        String idTokenString = idTokenGenerator.getIdTokenString();
        IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
        PublicKeysObject publicKeysObject = new PublicKeysObject();
        publicKeysObject.register(
                kid,
                Base64.getEncoder()
                        .withoutPadding()
                        .encodeToString(idTokenGenerator.getPublicKey().getEncoded()));

        assertFalse(
                idTokenVer.check(
                        iss,
                        nonce,
                        clientId,
                        idTokenObject,
                        publicKeysObject,
                        idTokenString,
                        accessToken));
        assertEquals(idTokenVer.getErrorMessage(), "invalid_type");
        assertEquals(idTokenVer.getErrorDescriptionMessage(), "The type is not JWT. (INVALID_JWT)");
    }

    @Test
    public void testGetCheckErrorMessageAlgorithm() throws Exception {
        idTokenObject.setAlgorithm("INVALID_ALGORITHM");
        IdTokenGenerator idTokenGenerator = new IdTokenGenerator(idTokenObject);
        String idTokenString = idTokenGenerator.getIdTokenString();
        IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
        PublicKeysObject publicKeysObject = new PublicKeysObject();
        publicKeysObject.register(
                kid,
                Base64.getEncoder()
                        .withoutPadding()
                        .encodeToString(idTokenGenerator.getPublicKey().getEncoded()));

        assertFalse(
                idTokenVer.check(
                        iss,
                        nonce,
                        clientId,
                        idTokenObject,
                        publicKeysObject,
                        idTokenString,
                        accessToken));
        assertEquals(idTokenVer.getErrorMessage(), "invalid_algorithm");
        assertEquals(
                idTokenVer.getErrorDescriptionMessage(),
                "The algorithm is not RSA-SHA256. (INVALID_ALGORITHM)");
    }

    @Test
    public void testGetCheckErrorMessageIss() throws Exception {
        idTokenObject.setIss("https://example.com");
        IdTokenGenerator idTokenGenerator = new IdTokenGenerator(idTokenObject);
        String idTokenString = idTokenGenerator.getIdTokenString();
        IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
        PublicKeysObject publicKeysObject = new PublicKeysObject();
        publicKeysObject.register(
                kid,
                Base64.getEncoder()
                        .withoutPadding()
                        .encodeToString(idTokenGenerator.getPublicKey().getEncoded()));

        assertFalse(
                idTokenVer.check(
                        iss,
                        nonce,
                        clientId,
                        idTokenObject,
                        publicKeysObject,
                        idTokenString,
                        accessToken));
        assertEquals(idTokenVer.getErrorMessage(), "invalid_issuer");
        assertEquals(
                idTokenVer.getErrorDescriptionMessage(),
                "The issuer did not match. (" + "https://example.com" + ")");
    }

    @Test
    public void testGetCheckErrorMessageNonce() throws Exception {
        idTokenObject.setNonce("INVALID_NONCE");
        IdTokenGenerator idTokenGenerator = new IdTokenGenerator(idTokenObject);
        String idTokenString = idTokenGenerator.getIdTokenString();
        IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
        PublicKeysObject publicKeysObject = new PublicKeysObject();
        publicKeysObject.register(
                kid,
                Base64.getEncoder()
                        .withoutPadding()
                        .encodeToString(idTokenGenerator.getPublicKey().getEncoded()));

        assertFalse(
                idTokenVer.check(
                        iss,
                        nonce,
                        clientId,
                        idTokenObject,
                        publicKeysObject,
                        idTokenString,
                        accessToken));
        assertEquals(idTokenVer.getErrorMessage(), "not_match_nonce");
        assertEquals(
                idTokenVer.getErrorDescriptionMessage(),
                "The nonce did not match. (INVALID_NONCE)");
    }

    @Test
    public void testGetCheckErrorMessageAudience() throws Exception {
        idTokenObject.setAud(new ArrayList<>(Collections.singletonList("INVALID_APPLICATION_ID")));
        IdTokenGenerator idTokenGenerator = new IdTokenGenerator(idTokenObject);
        String idTokenString = idTokenGenerator.getIdTokenString();
        IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
        PublicKeysObject publicKeysObject = new PublicKeysObject();
        publicKeysObject.register(
                kid,
                Base64.getEncoder()
                        .withoutPadding()
                        .encodeToString(idTokenGenerator.getPublicKey().getEncoded()));

        assertFalse(
                idTokenVer.check(
                        iss,
                        nonce,
                        clientId,
                        idTokenObject,
                        publicKeysObject,
                        idTokenString,
                        accessToken));
        assertEquals(idTokenVer.getErrorMessage(), "invalid_audience");
        assertEquals(
                idTokenVer.getErrorDescriptionMessage(),
                "The client id did not match. (INVALID_APPLICATION_ID)");
    }

    @Test
    public void testGetCheckErrorMessageAtHash() throws Exception {
        idTokenObject.setAtHash("INVALID_AT_HASH");
        IdTokenGenerator idTokenGenerator = new IdTokenGenerator(idTokenObject);
        String idTokenString = idTokenGenerator.getIdTokenString();
        IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
        PublicKeysObject publicKeysObject = new PublicKeysObject();
        publicKeysObject.register(
                kid,
                Base64.getEncoder()
                        .withoutPadding()
                        .encodeToString(idTokenGenerator.getPublicKey().getEncoded()));

        assertFalse(
                idTokenVer.check(
                        iss,
                        nonce,
                        clientId,
                        idTokenObject,
                        publicKeysObject,
                        idTokenString,
                        accessToken));
        assertEquals(idTokenVer.getErrorMessage(), "invalid_at_hash");
        assertEquals(
                idTokenVer.getErrorDescriptionMessage(),
                "The at_hash did not match. (INVALID_AT_HASH)");
    }

    @Test
    public void testGetCheckErrorMessageExpired() throws Exception {
        idTokenObject.setExp(1311647139);
        IdTokenGenerator idTokenGenerator = new IdTokenGenerator(idTokenObject);
        String idTokenString = idTokenGenerator.getIdTokenString();
        IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
        PublicKeysObject publicKeysObject = new PublicKeysObject();
        publicKeysObject.register(
                kid,
                Base64.getEncoder()
                        .withoutPadding()
                        .encodeToString(idTokenGenerator.getPublicKey().getEncoded()));

        assertFalse(
                idTokenVer.check(
                        iss,
                        nonce,
                        clientId,
                        idTokenObject,
                        publicKeysObject,
                        idTokenString,
                        accessToken));
        assertEquals(idTokenVer.getErrorMessage(), "expired_id_token");
        assertEquals(idTokenVer.getErrorDescriptionMessage(), "Re-issue Id Token. (1311647139)");
    }

    @Test
    public void testGetCheckErrorMessageOverAcceptableRange() throws Exception {
        idTokenObject.setIat(1310437540);
        IdTokenGenerator idTokenGenerator = new IdTokenGenerator(idTokenObject);
        String idTokenString = idTokenGenerator.getIdTokenString();
        IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
        PublicKeysObject publicKeysObject = new PublicKeysObject();
        publicKeysObject.register(
                kid,
                Base64.getEncoder()
                        .withoutPadding()
                        .encodeToString(idTokenGenerator.getPublicKey().getEncoded()));

        assertFalse(
                idTokenVer.check(
                        iss,
                        nonce,
                        clientId,
                        idTokenObject,
                        publicKeysObject,
                        idTokenString,
                        accessToken));
        assertEquals(idTokenVer.getErrorMessage(), "over_acceptable_range");
        assertEquals(
                idTokenVer.getErrorDescriptionMessage(),
                "This access has expired possible. (1310437540)");
    }

    @Test
    public void testGetCheckErrorMessagePublicKeyNotFound() throws Exception {
        idTokenObject.setKid("INVALID_KID");
        IdTokenGenerator idTokenGenerator = new IdTokenGenerator(idTokenObject);
        String idTokenString = idTokenGenerator.getIdTokenString();
        IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
        PublicKeysObject publicKeysObject = new PublicKeysObject();
        publicKeysObject.register(
                kid,
                Base64.getEncoder()
                        .withoutPadding()
                        .encodeToString(idTokenGenerator.getPublicKey().getEncoded()));

        assertFalse(
                idTokenVer.check(
                        iss,
                        nonce,
                        clientId,
                        idTokenObject,
                        publicKeysObject,
                        idTokenString,
                        accessToken));
        assertEquals(idTokenVer.getErrorMessage(), "public_key_not_found");
        assertEquals(idTokenVer.getErrorDescriptionMessage(), "PublicKey for kid not found.");
    }

    @Test
    public void testGetCheckErrorMessageCheckSignature() throws Exception {
        IdTokenGenerator idTokenGenerator = new IdTokenGenerator(idTokenObject);
        String idTokenString = idTokenGenerator.getInvalidIdTokenString();
        IdTokenVerification idTokenVer = new IdTokenVerificationCurrentTimeTest(iat);
        PublicKeysObject publicKeysObject = new PublicKeysObject();
        publicKeysObject.register(
                kid,
                Base64.getEncoder()
                        .withoutPadding()
                        .encodeToString(idTokenGenerator.getPublicKey().getEncoded()));

        assertFalse(
                idTokenVer.check(
                        iss,
                        nonce,
                        clientId,
                        idTokenObject,
                        publicKeysObject,
                        idTokenString,
                        accessToken));
        assertEquals(idTokenVer.getErrorMessage(), "invalid_signature");
        assertEquals(idTokenVer.getErrorDescriptionMessage(), "Signature verification failed.");
    }

    public static class IdTokenVerificationCurrentTimeTest extends IdTokenVerification {
        private final long currentTime;

        public IdTokenVerificationCurrentTimeTest(long currentTime) {
            this.currentTime = currentTime;
        }

        protected long getCurrentTime() {
            return this.currentTime;
        }
    }

    private String generateHash(String data) {
        byte[] hash = DigestUtils.sha256(data.getBytes(StandardCharsets.UTF_8));
        byte[] halfOfHash = Arrays.copyOfRange(hash, 0, hash.length / 2);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(halfOfHash);
    }
}
