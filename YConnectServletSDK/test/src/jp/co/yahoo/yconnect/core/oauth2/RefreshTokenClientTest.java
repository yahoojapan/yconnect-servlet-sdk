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

package jp.co.yahoo.yconnect.core.oauth2;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import jp.co.yahoo.yconnect.core.http.HttpParameters;
import org.junit.Test;

public class RefreshTokenClientTest {

    @Test
    public void testFetch() throws Exception {
        String refreshToken = "sample_refresh_token";

        String accessTokenSample = "sample_token";
        long expiresIn = 3600;

        String json =
                "{\"access_token\":\""
                        + accessTokenSample
                        + "\", \"expires_in\":"
                        + expiresIn
                        + "}";
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        RefreshTokenClient client =
                new RefreshTokenClient(
                        "https://example.co.jp",
                        refreshToken,
                        "sample_client_id",
                        "sample_client_secret") {
                    @Override
                    protected JsonObject request(HttpParameters parameters) {
                        assertEquals(OAuth2GrantType.REFRESH_TOKEN, parameters.get("grant_type"));
                        assertEquals(refreshToken, parameters.get("refresh_token"));
                        return jsonObject;
                    }
                };

        client.fetch();

        BearerToken token = client.getAccessToken();
        assertEquals(accessTokenSample, token.getAccessToken());
        assertEquals(expiresIn, token.getExpiration());
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
