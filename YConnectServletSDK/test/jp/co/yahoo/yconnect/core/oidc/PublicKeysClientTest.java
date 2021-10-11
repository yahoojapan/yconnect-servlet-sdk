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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class PublicKeysClientTest {

    private static String publicKey;

    @BeforeClass
    public static void beforeAll() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);

        KeyPair pair = keyPairGen.generateKeyPair();
        publicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
    }

    @Test
    public void testPublicKeysParser() throws Exception {
        String json = "{"
                + "\"kid1\":\"" + publicKey + "\","
                + "\"kid2\":\"" + publicKey + "\"}";

        PublicKeysClient client = new PublicKeysClient();

        Class<PublicKeysClient> c = PublicKeysClient.class;
        Method parser = c.getDeclaredMethod("publicKeysParser", String.class);
        parser.setAccessible(true);
        parser.invoke(client, json);

        PublicKeysObject publicKeys = client.getPublicKeysObject();
        assertNotNull(publicKeys.getPublicKey("kid1"));
        assertNotNull(publicKeys.getPublicKey("kid2"));
        assertNull(publicKeys.getPublicKey("kid3"));
    }

    @Test(expected = JsonParsingException.class)
    public void testInvalidPublicKeysParser() throws Exception {
        String json = "{"
                + "\"kid1\":\"" + publicKey + "\","
                + "\"kid2\":\"" + publicKey + "\"";

        PublicKeysClient client = new PublicKeysClient();

        Class<PublicKeysClient> c = PublicKeysClient.class;
        Method parser = c.getDeclaredMethod("publicKeysParser", String.class);
        parser.setAccessible(true);
        try {
            parser.invoke(client, json);
        } catch (InvocationTargetException ex) {
            throw (Exception) ex.getCause();
        }

    }
}
