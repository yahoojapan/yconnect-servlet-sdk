/*
 * The MIT License (MIT)
 *
 * © Yahoo Japan
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import org.junit.BeforeClass;
import org.junit.Test;

public class PublicKeysObjectTest {

    private static String publicKey;
    private static String packs8PublicKey;

    @BeforeClass
    public static void beforeAll() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);

        KeyPair pair = keyPairGen.generateKeyPair();
        publicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());

        packs8PublicKey = "-----BEGIN PUBLIC KEY-----\n";
        packs8PublicKey += publicKey.replaceAll("(?<=\\G.{75})(?!$)", "\n") + "\n";
        packs8PublicKey += "-----END PUBLIC KEY-----";
    }

    @Test
    public void testGetPublicKey() throws Exception {
        PublicKeysObject publicKeysObject = new PublicKeysObject();
        publicKeysObject.register("kid", publicKey);

        PublicKey publicKey = publicKeysObject.getPublicKey("kid");
        assertNotNull(publicKey);
    }

    @Test
    public void testGetPublicKeyWithPacks8Format() throws Exception {
        PublicKeysObject publicKeysObject = new PublicKeysObject();
        publicKeysObject.register("kid", packs8PublicKey);

        PublicKey publicKey = publicKeysObject.getPublicKey("kid");
        assertNotNull(publicKey);
    }

    @Test
    public void testGetPublicKeyReturnsNull() throws Exception {
        PublicKeysObject publicKeysObject = new PublicKeysObject();
        publicKeysObject.register("kid", publicKey);

        PublicKey publicKey = publicKeysObject.getPublicKey("invalid_kid");
        assertNull(publicKey);
    }

    @Test(expected = InvalidKeySpecException.class)
    public void testInvalidGetPublicKey() throws Exception {
        PublicKeysObject publicKeysObject = new PublicKeysObject();
        String invalidPublicKey =
                Base64.getEncoder().encodeToString("invalid".getBytes(StandardCharsets.UTF_8));
        publicKeysObject.register("kid", invalidPublicKey);

        publicKeysObject.getPublicKey("kid");
    }
}
