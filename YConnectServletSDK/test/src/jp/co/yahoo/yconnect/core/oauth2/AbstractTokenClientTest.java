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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import jp.co.yahoo.yconnect.core.http.HttpHeaders;
import jp.co.yahoo.yconnect.core.http.HttpParameters;
import jp.co.yahoo.yconnect.core.http.YHttpClient;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

public class AbstractTokenClientTest {

    private final AbstractTokenClient client =
            new AbstractTokenClient(null, null, null) {
                @Override
                void fetch() {}
            };

    @Test
    public void testRequest() throws Exception {
        String clientIdSample = "sample_client_id";
        String clientSecretSample = "sample_client_secret";
        String accessTokenSample = "sample_token";
        long expiresIn = 3600;

        AbstractTokenClient client =
                new AbstractTokenClient(null, clientIdSample, clientSecretSample) {
                    @Override
                    void fetch() throws TokenException {}

                    @Override
                    protected YHttpClient getYHttpClient() {
                        return new YHttpClient() {
                            @Override
                            public void requestPost(
                                    String endpointUrl,
                                    HttpParameters parameters,
                                    HttpHeaders requestHeaders) {
                                assertEquals(
                                        "application/x-www-form-urlencoded;charset=UTF-8",
                                        requestHeaders.get("Content-Type"));
                                String credential = clientIdSample + ":" + clientSecretSample;
                                String basic =
                                        new String(Base64.encodeBase64(credential.getBytes()));
                                assertEquals("Basic " + basic, requestHeaders.get("Authorization"));
                            }

                            @Override
                            public HttpHeaders getResponseHeaders() {
                                return new HttpHeaders();
                            }

                            @Override
                            public String getResponseBody() {
                                return "{\"access_token\":\""
                                        + accessTokenSample
                                        + "\", \"expires_in\":"
                                        + expiresIn
                                        + "}";
                            }

                            @Override
                            public int getStatusCode() {
                                return 200;
                            }
                        };
                    }
                };

        JsonObject result = client.request(new HttpParameters());

        assertEquals(accessTokenSample, result.getString("access_token"));
        assertEquals(expiresIn, result.getJsonNumber("expires_in").longValue());
    }

    @Test
    public void testRequestThrowsTokenException() throws Exception {
        String error = "error_sample";
        String errorDescription = "error_description_sample";
        Integer errorCode = 1000;

        AbstractTokenClient client =
                new AbstractTokenClient(null, null, null) {
                    @Override
                    void fetch() throws TokenException {}

                    @Override
                    protected YHttpClient getYHttpClient() {
                        return new YHttpClient() {
                            @Override
                            public void requestPost(
                                    String endpointUrl,
                                    HttpParameters parameters,
                                    HttpHeaders requestHeaders) {}

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
                    protected void checkErrorResponse(int statusCode, JsonObject jsonObject)
                            throws TokenException {
                        throw new TokenException(error, errorDescription, errorCode);
                    }
                };

        TokenException ex =
                assertThrows(TokenException.class, () -> client.request(new HttpParameters()));

        assertEquals(error, ex.getError());
        assertEquals(errorDescription, ex.getErrorDescription());
        assertEquals(errorCode, ex.getErrorCode());
    }

    @Test
    public void checkErrorResponse() throws Exception {
        String json = "{\"access_token\":\"sample_token\", \"expires_in\":3600}";
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        client.checkErrorResponse(200, jsonObject);
    }

    @Test
    public void testErrorResponseThrowsTokenExceptionWhenJsonHasError() {
        String error = "sample_error";
        String errorDescription = "sample_error_description";
        Integer errorCode = 1000;
        String json =
                "{\"error\":\""
                        + error
                        + "\", \"error_description\":\""
                        + errorDescription
                        + "\", \"error_code\":"
                        + errorCode
                        + "}";
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        TokenException ex =
                assertThrows(
                        TokenException.class, () -> client.checkErrorResponse(400, jsonObject));

        assertEquals(error, ex.getError());
        assertEquals(errorDescription, ex.getErrorDescription());
        assertEquals(errorCode, ex.getErrorCode());
    }

    @Test
    public void testErrorResponseThrowsTokenExceptionWhenJsonHasNotError() {
        String json = "{}";
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        TokenException ex =
                assertThrows(
                        TokenException.class, () -> client.checkErrorResponse(400, jsonObject));

        assertEquals("An unexpected error has occurred.", ex.getError());
        assertEquals("", ex.getErrorDescription());
        assertNull(ex.getErrorCode());
    }

    @Test
    public void testErrorResponseThrowsTokenExceptionWhenStatusCodeInvalid() {
        String json =
                "{\"error\":\"sample_error\", \"error_description\":\"sample_error_description\"}";
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        TokenException ex =
                assertThrows(
                        TokenException.class, () -> client.checkErrorResponse(301, jsonObject));

        assertEquals("An unexpected error has occurred.", ex.getError());
        assertEquals("", ex.getErrorDescription());
        assertNull(ex.getErrorCode());
    }
}
