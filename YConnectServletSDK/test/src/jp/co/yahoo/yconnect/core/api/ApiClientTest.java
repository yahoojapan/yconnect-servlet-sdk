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

package jp.co.yahoo.yconnect.core.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import jp.co.yahoo.yconnect.core.http.HttpHeaders;
import jp.co.yahoo.yconnect.core.http.HttpParameters;
import jp.co.yahoo.yconnect.core.http.YHttpClient;
import org.junit.Test;

public class ApiClientTest {

    private final int statusCode = 200;
    private final String statusMessage = "Sample Message";
    private HttpHeaders headers = new HttpHeaders();
    private final String response = "Sample Body";

    @Test
    public void testGetFetchResource() throws Exception {
        ApiClient client = getMockApiClient();
        client.fetchResource("http;//example.com", ApiClient.GET_METHOD);

        assertEquals(statusCode, client.getStatusCode());
        assertEquals(statusMessage, client.getStatusMessage());
        assertEquals(headers, client.getHeaders());
        assertEquals(response, client.getResponse());
    }

    @Test
    public void testPostFetchResource() throws Exception {
        ApiClient client = getMockApiClient();
        client.fetchResource("http;//example.com", ApiClient.POST_METHOD);

        assertEquals(statusCode, client.getStatusCode());
        assertEquals(statusMessage, client.getStatusMessage());
        assertEquals(headers, client.getHeaders());
        assertEquals(response, client.getResponse());
    }

    @Test()
    public void testFetchResourceThrowsApiClientException() {
        ApiClient client = getMockApiClient();

        ApiClientException ex =
                assertThrows(
                        ApiClientException.class,
                        () -> client.fetchResource("http;//example.com", "DELETE"));
        assertEquals(ex.getError(), "Undefined Http method.");
        assertEquals(ex.getMessage(), "");
    }

    @Test
    public void testErrorFetchResourceByResponseCode() {
        int responseCode = 404;
        String responseMessage = "Not Found.";

        ApiClient client =
                new ApiClient() {
                    @Override
                    protected YHttpClient newHttpClient() {
                        return new YHttpClient() {
                            @Override
                            public void requestGet(
                                    String url, HttpParameters parameters, HttpHeaders headers) {}

                            @Override
                            public void requestPost(
                                    String url, HttpParameters parameters, HttpHeaders headers) {}

                            @Override
                            public int getStatusCode() {
                                return responseCode;
                            }

                            @Override
                            public String getStatusMessage() {
                                return responseMessage;
                            }

                            @Override
                            public HttpHeaders getResponseHeaders() {
                                return headers;
                            }

                            @Override
                            public String getResponseBody() {
                                return response;
                            }
                        };
                    }
                };

        String error =
                "Failed Request.(status code: "
                        + responseCode
                        + " status message: "
                        + responseMessage
                        + ")";
        String message = headers.toString();

        ApiClientException ex =
                assertThrows(
                        ApiClientException.class,
                        () -> client.fetchResource("http;//example.com", ApiClient.GET_METHOD));
        assertEquals(ex.getError(), error);
        assertEquals(ex.getMessage(), message);
    }

    @Test
    public void testErrorFetchResourceByWwwAuthHeader() {
        String error = "Page not found.";
        String errorDescription = "/info is not found.";
        ApiClient client =
                new ApiClient() {
                    @Override
                    protected YHttpClient newHttpClient() {
                        return new YHttpClient() {
                            @Override
                            public void requestGet(
                                    String url, HttpParameters parameters, HttpHeaders headers) {}

                            @Override
                            public void requestPost(
                                    String url, HttpParameters parameters, HttpHeaders headers) {}

                            @Override
                            public int getStatusCode() {
                                return 404;
                            }

                            @Override
                            public String getStatusMessage() {
                                return "Not Found.";
                            }

                            @Override
                            public HttpHeaders getResponseHeaders() {
                                headers = new HttpHeaders();
                                headers.put(
                                        "WWW-Authenticate",
                                        "\"error\"=\""
                                                + error
                                                + "\",\"error_description\"=\""
                                                + errorDescription
                                                + "\"");
                                return headers;
                            }

                            @Override
                            public String getResponseBody() {
                                return response;
                            }
                        };
                    }
                };

        ApiClientException ex =
                assertThrows(
                        ApiClientException.class,
                        () -> client.fetchResource("http;//example.com", ApiClient.GET_METHOD));
        assertEquals(ex.getError(), error);
        assertEquals(ex.getMessage(), errorDescription);
    }

    @Test
    public void testSetHeader() throws Exception {
        ApiClient client = new ApiClient();

        String field = "Authorization";
        String value = "Bearer token";
        client.setHeader(field, value);

        HttpHeaders headers = getRequestHeaders(client);

        assertEquals(headers.get(field), value);
    }

    @Test
    public void testSetHeaderWithColon() throws Exception {
        ApiClient client = new ApiClient();

        String field = "Authorization:";
        String value = "Bearer token";
        client.setHeader(field, value);

        HttpHeaders headers = getRequestHeaders(client);

        assertEquals(headers.get("Authorization"), value);
    }

    @Test
    public void testExtractWWWAuthHeader() throws Exception {
        ApiClient client = new ApiClient();

        String error = "Page not found.";
        String errorDescription = "/info is not found.";

        Method checkErrorResponseMethod =
                ApiClient.class.getDeclaredMethod("extractWWWAuthHeader", String.class);
        checkErrorResponseMethod.setAccessible(true);
        HashMap<?, ?> result =
                (HashMap<?, ?>)
                        checkErrorResponseMethod.invoke(
                                client,
                                "\"error\"=\""
                                        + error
                                        + "\",\"error_description\"=\""
                                        + errorDescription
                                        + "\"");

        assertEquals(result.get("error"), error);
        assertEquals(result.get("error_description"), errorDescription);
    }

    private ApiClient getMockApiClient() {
        return new ApiClient() {
            @Override
            protected YHttpClient newHttpClient() {
                return new YHttpClient() {
                    @Override
                    public void requestGet(
                            String url, HttpParameters parameters, HttpHeaders headers) {}

                    @Override
                    public void requestPost(
                            String url, HttpParameters parameters, HttpHeaders headers) {}

                    @Override
                    public int getStatusCode() {
                        return statusCode;
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
                        return response;
                    }
                };
            }
        };
    }

    private HttpHeaders getRequestHeaders(ApiClient client) throws Exception {
        Field requestHeadersFiled = ApiClient.class.getDeclaredField("requestHeaders");
        requestHeadersFiled.setAccessible(true);
        return (HttpHeaders) requestHeadersFiled.get(client);
    }
}
