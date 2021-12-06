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

package jp.co.yahoo.yconnect.core.oauth2;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import jp.co.yahoo.yconnect.core.http.HttpParameters;
import org.junit.Test;

public class TokenClientTest {

    private final String authorizationCode = "sample_authorization_code";
    private final String redirectUri = "https://example.co.jp/callback";
    private final String clientId = "sample_client_id";
    private final String clientSecret = "sample_client_secret";

    private final String accessTokenSample = "sample_token";
    private final String refreshToken = "sample_refresh_token";
    private final String idToken = "sample_id_token";
    private final long expiresIn = 3600;

    @Test
    public void testFetch() throws Exception {
        String json =
                "{\"access_token\":\""
                        + accessTokenSample
                        + "\", \"expires_in\":"
                        + expiresIn
                        + ", \"refresh_token\":\""
                        + refreshToken
                        + "\", \"id_token\":\""
                        + idToken
                        + "\"}";
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        TokenClient client =
                new TokenClient(
                        "https://example.co.jp",
                        authorizationCode,
                        redirectUri,
                        clientId,
                        clientSecret) {
                    @Override
                    protected JsonObject request(HttpParameters parameters) {
                        assertEquals(
                                OAuth2GrantType.AUTHORIZATION_CODE, parameters.get("grant_type"));
                        assertEquals(authorizationCode, parameters.get("code"));
                        assertEquals(redirectUri, parameters.get("redirect_uri"));
                        return jsonObject;
                    }
                };

        client.fetch();

        BearerToken token = client.getAccessToken();
        assertEquals(accessTokenSample, token.getAccessToken());
        assertEquals(expiresIn, token.getExpiration());
        assertEquals(refreshToken, token.getRefreshToken());
        assertEquals(idToken, client.getIdToken());
    }

    @Test
    public void testFetchWithCodeVerifier() throws Exception {
        String codeVerifier = "sample_code_verifier";

        String json =
                "{\"access_token\":\""
                        + accessTokenSample
                        + "\", \"expires_in\":"
                        + expiresIn
                        + ", \"refresh_token\":\""
                        + refreshToken
                        + "\", \"id_token\":\""
                        + idToken
                        + "\"}";
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        TokenClient client =
                new TokenClient(
                        "https://example.co.jp",
                        authorizationCode,
                        redirectUri,
                        clientId,
                        clientSecret,
                        codeVerifier) {
                    @Override
                    protected JsonObject request(HttpParameters parameters) {
                        assertEquals(
                                OAuth2GrantType.AUTHORIZATION_CODE, parameters.get("grant_type"));
                        assertEquals(authorizationCode, parameters.get("code"));
                        assertEquals(redirectUri, parameters.get("redirect_uri"));
                        assertEquals(codeVerifier, parameters.get("code_verifier"));
                        return jsonObject;
                    }
                };

        client.fetch();

        BearerToken token = client.getAccessToken();
        assertEquals(accessTokenSample, token.getAccessToken());
        assertEquals(expiresIn, token.getExpiration());
        assertEquals(refreshToken, token.getRefreshToken());
        assertEquals(idToken, client.getIdToken());
    }

    @Test(expected = TokenException.class)
    public void testFetchThrowsTokenException() throws Exception {
        RefreshTokenClient client =
                new RefreshTokenClient(
                        "https://example.co.jp",
                        "sample_refresh_token",
                        "sample_client_id",
                        "sample_client_secret") {
                    @Override
                    protected JsonObject request(HttpParameters parameters) throws TokenException {
                        throw new TokenException("error_sample", "error_description_sample", 1000);
                    }
                };

        client.fetch();
    }
}
