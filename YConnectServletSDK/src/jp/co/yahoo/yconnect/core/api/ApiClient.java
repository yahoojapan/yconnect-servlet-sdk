/*
 * The MIT License (MIT)
 *
 * Copyright (C) 2016 Yahoo Japan Corporation. All Rights Reserved.
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

import java.util.HashMap;
import jp.co.yahoo.yconnect.core.http.HttpHeaders;
import jp.co.yahoo.yconnect.core.http.HttpParameters;
import jp.co.yahoo.yconnect.core.http.YHttpClient;
import jp.co.yahoo.yconnect.core.oauth2.BearerToken;
import jp.co.yahoo.yconnect.core.util.YConnectLogger;

/**
 * API Client with Access Token.
 *
 * <p>This client fetch body and headers of response from endpoint. Set Access Token, others
 * parameters and headers for API. Select POST or GET HTTP method. Supported HTTPS and HTTP. No
 * function to parsing XML or JSON response body.
 *
 * @author Copyright (C) 2016 Yahoo Japan Corporation. All Rights Reserved.
 */
public class ApiClient {

    private static final String TAG = ApiClient.class.getSimpleName();

    public static final String POST_METHOD = "POST";

    public static final String GET_METHOD = "GET";

    private final HttpParameters parameters;

    private final HttpHeaders requestHeaders;

    private int responseCode;

    private String responseMessage;

    private String responseBody;

    private HttpHeaders responseHeaders;

    /** ApiClient Constructor. initialize HttpParameters and HttpHeaders Object. */
    public ApiClient() {
        this.parameters = new HttpParameters();
        this.requestHeaders = new HttpHeaders();
    }

    /**
     * ApiClient Constructor. initialize HttpParameters and HttpHeaders Object and set Access Token.
     *
     * @param accessToken Access Token of type String obtained from authorization endpoint or token
     *     endpoint.
     */
    public ApiClient(String accessToken) {
        this.parameters = new HttpParameters();
        this.requestHeaders = new HttpHeaders();
        setHeader("Authorization", "Bearer " + accessToken);
    }

    /**
     * ApiClient Constructor. initialize HttpParameters and HttpHeaders Object and set Access Token.
     *
     * @param accessToken Access Token of type BearerToken obtained from authorization endpoint or
     *     token endpoint.
     */
    public ApiClient(BearerToken accessToken) {
        this.parameters = new HttpParameters();
        this.requestHeaders = new HttpHeaders();
        setHeader("Authorization", "Bearer " + accessToken.toAuthorizationHeader());
    }

    /**
     * Fetch resources from endpoint.
     *
     * @param url URL of endpoint.
     * @param method POST_METHOD or GET_METHOD.
     * @throws ApiClientException Throws when specify undefined method.
     */
    public void fetchResource(String url, String method) throws ApiClientException {
        YConnectLogger.debug(TAG, "request parameters: " + parameters.toQueryString());
        YConnectLogger.debug(TAG, "request headers: " + requestHeaders.toHeaderString());
        YHttpClient client;
        if (POST_METHOD.equalsIgnoreCase(method)) {
            client = newHttpClient();
            client.requestPost(url, parameters, requestHeaders);
        } else if (GET_METHOD.equalsIgnoreCase(method)) {
            client = newHttpClient();
            client.requestGet(url, parameters, requestHeaders);
        } else {
            throw new ApiClientException("Undefined Http method.", "");
        }
        responseCode = client.getStatusCode();
        responseMessage = client.getStatusMessage();
        responseHeaders = client.getResponseHeaders();
        responseBody = client.getResponseBody();

        checkErrorResponse();
    }

    /**
     * Get status code of response. Call this method after fetch method.
     *
     * @return Status code of type integer.
     */
    public int getStatusCode() {
        return responseCode;
    }

    /**
     * Get status message of response. Call this method after fetch method.
     *
     * @return Status message of type String.
     */
    public String getStatusMessage() {
        return responseMessage;
    }

    /**
     * Get Response headers fetched from endpoint. Call this method after fetch method.
     *
     * @return Response headers of type HttpHeaders.
     */
    public HttpHeaders getHeaders() {
        return responseHeaders;
    }

    /**
     * Get Response body fetched from endpoint. Call this method after fetch method.
     *
     * @return Response body of type String.
     */
    public String getResponse() {
        return responseBody;
    }

    /**
     * Set the parameters that are specified in each API. Call this method before fetch method.
     *
     * @param name Parameter name of type String.
     * @param value Parameter value of type String.
     */
    public void setParameter(String name, String value) {
        parameters.put(name, value);
    }

    /**
     * Set the headers that are specified in each API. Call this method before fetch method.
     *
     * @param field Field name of type String.
     * @param value Field value of type String.
     */
    public void setHeader(String field, String value) {
        field = field.replace(":", "");
        field = field.trim();
        requestHeaders.put(field, value);
    }

    /**
     * Set Access Token of type BearerToken obtained from authorization endpoint or token endpoint.
     *
     * @param accessToken Access Token of type BearerToken.
     */
    public void setAccessToken(BearerToken accessToken) {
        setParameter("access_token", accessToken.getAccessToken());
    }

    private void checkErrorResponse() throws ApiClientException {

        String wwwAuthHeader = responseHeaders.get("WWW-Authenticate");
        if (responseCode != 200) {
            if (wwwAuthHeader != null) {
                YConnectLogger.debug(TAG, wwwAuthHeader);
                HashMap<String, String> wwwAuthHeaderHashMap = extractWWWAuthHeader(wwwAuthHeader);
                YConnectLogger.debug(TAG, wwwAuthHeaderHashMap.toString());
                String error = wwwAuthHeaderHashMap.get("error");
                String errorDescription = wwwAuthHeaderHashMap.get("error_description");
                throw new ApiClientException(error, errorDescription);
            } else {
                throw new ApiClientException(
                        "Failed Request.(status code: "
                                + responseCode
                                + " status message: "
                                + responseMessage
                                + ")",
                        responseHeaders.toString());
            }
        }
    }

    private static HashMap<String, String> extractWWWAuthHeader(String wwwAuthHeader) {
        HashMap<String, String> map = new HashMap<String, String>();
        String[] parameterArray = wwwAuthHeader.split(",");
        for (String parameter : parameterArray) {
            parameter = parameter.replaceAll("\"", "");
            String[] kv = parameter.split("=");
            map.put(kv[0].trim(), kv[1].trim());
        }
        return map;
    }

    protected YHttpClient newHttpClient() {
        return new YHttpClient();
    }
}
