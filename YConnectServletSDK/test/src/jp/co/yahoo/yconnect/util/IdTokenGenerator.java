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

package jp.co.yahoo.yconnect.util;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import jp.co.yahoo.yconnect.core.oidc.IdTokenObject;

public class IdTokenGenerator {

    private final String idTokenString;
    private final String invalidIdTokenString;
    private final PublicKey publicKey;

    public IdTokenGenerator(IdTokenObject idTokenObject) throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);

        KeyPair pair = keyPairGen.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        publicKey = pair.getPublic();

        String header =
                "{\"typ\": \"JWT\", \"alg\": \"RS256\", \"kid\": \""
                        + idTokenObject.getKid()
                        + "\"}";
        String base64Header =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(header.getBytes(StandardCharsets.UTF_8));

        String payload =
                "{\"iss\": \""
                        + idTokenObject.getIss()
                        + "\", \"sub\": \""
                        + idTokenObject.getSub()
                        + "\", "
                        + "\"aud\": [\""
                        + idTokenObject.getAud().get(0)
                        + "\"], \"exp\": "
                        + idTokenObject.getExp()
                        + ","
                        + "\"iat\": "
                        + idTokenObject.getIat()
                        + ", \"nonce\": \""
                        + idTokenObject.getNonce()
                        + "\", "
                        + "\"at_hash\": \""
                        + idTokenObject.getAtHash()
                        + "\", \"auth_time\": "
                        + idTokenObject.getAuthTime()
                        + "}";
        String base64Payload =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(payload.getBytes(StandardCharsets.UTF_8));

        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);
        sign.update(base64Header.getBytes(StandardCharsets.UTF_8));
        sign.update(".".getBytes(StandardCharsets.UTF_8));
        sign.update(base64Payload.getBytes(StandardCharsets.UTF_8));

        String base64Signature =
                Base64.getUrlEncoder().withoutPadding().encodeToString(sign.sign());

        idTokenString = base64Header + "." + base64Payload + "." + base64Signature;

        String invalidHeader =
                "{\"typ\": \"JWT\", \"alg\": \"RS256\", \"kid\": \""
                        + idTokenObject.getKid()
                        + "id"
                        + "\"}";
        String base64InvalidHeader =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(invalidHeader.getBytes(StandardCharsets.UTF_8));

        invalidIdTokenString = base64InvalidHeader + "." + base64Payload + "." + base64Signature;
    }

    public String getIdTokenString() {
        return idTokenString;
    }

    public String getInvalidIdTokenString() {
        return invalidIdTokenString;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
