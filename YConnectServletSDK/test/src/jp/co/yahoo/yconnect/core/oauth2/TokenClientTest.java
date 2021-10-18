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

package jp.co.yahoo.yconnect.core.oauth2;

import jp.co.yahoo.yconnect.core.http.HttpHeaders;
import jp.co.yahoo.yconnect.core.http.HttpParameters;
import jp.co.yahoo.yconnect.core.http.YHttpClient;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import javax.json.JsonObject;

import static org.junit.Assert.assertEquals;

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

        TokenClient client = new TokenClient("https://example.co.jp",
                authorizationCode, redirectUri, clientId, clientSecret) {
            @Override
            protected YHttpClient getYHttpClient() {
                return new YHttpClient() {
                    @Override
                    public void requestPost(String endpointUrl, HttpParameters parameters, HttpHeaders requestHeaders) {
                        assertEquals(OAuth2GrantType.AUTHORIZATION_CODE, parameters.get("grant_type"));
                        assertEquals(authorizationCode, parameters.get("code"));
                        assertEquals(redirectUri, parameters.get("redirect_uri"));

                        assertEquals("application/x-www-form-urlencoded;charset=UTF-8", requestHeaders.get("Content-Type"));
                        String credential = clientId + ":" + clientSecret;
                        String basic = new String(Base64.encodeBase64(credential.getBytes()));
                        assertEquals("Basic " + basic, requestHeaders.get("Authorization"));
                    }

                    @Override
                    public HttpHeaders getResponseHeaders() {
                        return new HttpHeaders();
                    }

                    @Override
                    public String getResponseBody() {
                        return "{\"access_token\":\"" + accessTokenSample + "\", \"expires_in\":" + expiresIn
                                + ", \"refresh_token\":\"" + refreshToken + "\", \"id_token\":\"" + idToken + "\"}";
                    }

                    @Override
                    public int getStatusCode() {
                        return 200;
                    }
                };
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

        TokenClient client = new TokenClient("https://example.co.jp",
                authorizationCode, redirectUri, clientId, clientSecret, codeVerifier) {
            @Override
            protected YHttpClient getYHttpClient() {
                return new YHttpClient() {
                    @Override
                    public void requestPost(String endpointUrl, HttpParameters parameters, HttpHeaders requestHeaders) {
                        assertEquals(OAuth2GrantType.AUTHORIZATION_CODE, parameters.get("grant_type"));
                        assertEquals(authorizationCode, parameters.get("code"));
                        assertEquals(redirectUri, parameters.get("redirect_uri"));
                        assertEquals(codeVerifier, parameters.get("code_verifier"));

                        assertEquals("application/x-www-form-urlencoded;charset=UTF-8", requestHeaders.get("Content-Type"));
                        String credential = clientId + ":" + clientSecret;
                        String basic = new String(Base64.encodeBase64(credential.getBytes()));
                        assertEquals("Basic " + basic, requestHeaders.get("Authorization"));
                    }

                    @Override
                    public HttpHeaders getResponseHeaders() {
                        return new HttpHeaders();
                    }

                    @Override
                    public String getResponseBody() {
                        return "{\"access_token\":\"" + accessTokenSample + "\", \"expires_in\":" + expiresIn
                                + ", \"refresh_token\":\"" + refreshToken + "\", \"id_token\":\"" + idToken + "\"}";
                    }

                    @Override
                    public int getStatusCode() {
                        return 200;
                    }
                };
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
        RefreshTokenClient client = new RefreshTokenClient("https://example.co.jp",
                "sample_refresh_token", "sample_client_id", "sample_client_secret") {
            @Override
            protected YHttpClient getYHttpClient() {
                return new YHttpClient() {
                    @Override
                    public void requestPost(String endpointUrl, HttpParameters parameters, HttpHeaders requestHeaders) {

                    }

                    @Override
                    public HttpHeaders getResponseHeaders() {
                        return new HttpHeaders();
                    }

                    @Override
                    public String getResponseBody() {
                        return "{\"error\":\"sample_error\", \"error_description\":\"sample_error_description\"}";
                    }

                    @Override
                    public int getStatusCode() {
                        return 400;
                    }
                };
            }

            @Override
            protected void checkErrorResponse(int statusCode, JsonObject jsonObject) throws TokenException {
                throw new TokenException("error_sample", "error_description_sample", 1000);
            }
        };

        client.fetch();
    }
}
