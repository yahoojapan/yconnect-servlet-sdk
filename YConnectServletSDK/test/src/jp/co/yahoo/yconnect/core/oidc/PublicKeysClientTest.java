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

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import javax.json.stream.JsonParsingException;
import jp.co.yahoo.yconnect.core.api.ApiClientException;
import jp.co.yahoo.yconnect.core.http.HttpHeaders;
import jp.co.yahoo.yconnect.core.http.HttpParameters;
import jp.co.yahoo.yconnect.core.http.YHttpClient;
import org.junit.BeforeClass;
import org.junit.Test;

public class PublicKeysClientTest {

    private final String endpoint = "https://userinfo.yahooapis.jp/yconnect/v2/attribute";
    private static String publicKey;

    @BeforeClass
    public static void beforeAll() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);

        KeyPair pair = keyPairGen.generateKeyPair();
        publicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
    }

    @Test
    public void testFetchResource() throws ApiClientException {
        YHttpClient httpClient =
                new YHttpClient() {
                    @Override
                    public void requestGet(
                            String urlString,
                            HttpParameters parameters,
                            HttpHeaders requestHeaders) {}

                    @Override
                    public int getStatusCode() {
                        return 200;
                    }

                    @Override
                    public String getStatusMessage() {
                        return "200 - OK";
                    }

                    @Override
                    public HttpHeaders getResponseHeaders() {
                        return new HttpHeaders();
                    }

                    @Override
                    public String getResponseBody() {
                        return "{\"kid\":\"sample_public_key\"}";
                    }
                };

        PublicKeysClient client =
                new PublicKeysClient() {
                    @Override
                    protected YHttpClient getYHttpClient() {
                        return httpClient;
                    }
                };

        client.fetchResource(endpoint);

        assertNotNull(client.getPublicKeysObject());
    }

    @Test
    public void testFetchResourceThrowsApiClientException() {
        int responseCode = 400;
        String statusMessage = "400 - Bad Request";
        HttpHeaders headers = new HttpHeaders();
        String responseBody = "{}";

        YHttpClient httpClient =
                new YHttpClient() {
                    @Override
                    public void requestGet(
                            String urlString,
                            HttpParameters parameters,
                            HttpHeaders requestHeaders) {}

                    @Override
                    public int getStatusCode() {
                        return responseCode;
                    }

                    @Override
                    public String getStatusMessage() {
                        return statusMessage;
                    }

                    @Override
                    public HttpHeaders getResponseHeaders() {
                        return headers;
                    }

                    @Override
                    public String getResponseBody() {
                        return responseBody;
                    }
                };

        PublicKeysClient client =
                new PublicKeysClient() {
                    @Override
                    protected YHttpClient getYHttpClient() {
                        return httpClient;
                    }
                };

        ApiClientException ex =
                assertThrows(ApiClientException.class, () -> client.fetchResource(endpoint));

        String expect =
                "Failed Request.(status code: "
                        + responseCode
                        + " status message: "
                        + statusMessage
                        + ")";
        assertEquals(expect, ex.getError());
    }

    @Test
    public void testPublicKeysParser() throws Exception {
        String json = "{" + "\"kid1\":\"" + publicKey + "\"," + "\"kid2\":\"" + publicKey + "\"}";

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
        String json = "{" + "\"kid1\":\"" + publicKey + "\"," + "\"kid2\":\"" + publicKey + "\"";

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
