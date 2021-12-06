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

import java.net.URI;
import java.net.URISyntaxException;
import jp.co.yahoo.yconnect.core.http.HttpParameters;
import jp.co.yahoo.yconnect.core.util.YConnectLogger;

/**
 * Authorization Request Client Class
 *
 * @author © Yahoo Japan
 */
public class AuthorizationRequestClient {

    private static final String TAG = AuthorizationRequestClient.class.getSimpleName();

    private final HttpParameters parameters;

    private final String endpointUri;

    private final String clientId;

    private String redirectUri;

    private String responseType;

    private String state;

    public AuthorizationRequestClient(String endpointUri, String clientId) {
        this.endpointUri = endpointUri;
        this.clientId = clientId;
        this.parameters = new HttpParameters();
    }

    public URI generateAuthorizationUri() {
        parameters.put("client_id", clientId);
        parameters.put("response_type", responseType);
        parameters.put("state", state);
        parameters.put("redirect_uri", redirectUri);
        YConnectLogger.info(TAG, parameters.toString());
        String uriString = endpointUri;
        uriString += "?" + parameters.toQueryString();
        URI requestUri = null;
        try {
            requestUri = new URI(uriString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return requestUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setParameter(String name, String value) {
        parameters.put(name, value);
    }
}
