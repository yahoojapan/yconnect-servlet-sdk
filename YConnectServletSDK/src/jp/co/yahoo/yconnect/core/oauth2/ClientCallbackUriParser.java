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

import jp.co.yahoo.yconnect.core.http.HttpParameters;
import jp.co.yahoo.yconnect.core.util.YConnectLogger;

/**
 * Client Callback URI Parser Class
 *
 * @author Copyright (C) 2021 Yahoo Japan Corporation. All Rights Reserved.
 */
public class ClientCallbackUriParser {

    private final HttpParameters parameters = new HttpParameters();

    public ClientCallbackUriParser(String request) throws AuthorizationException {
        parseUri(request);
    }

    public boolean hasAuthorizationCode() {
        return parameters.get("code") != null;
    }

    public String getAuthorizationCode(String state) throws AuthorizationException {
        YConnectLogger.debug(
                this, "Response state=" + parameters.get("state") + ", Input state=" + state);

        if (!state.equals(parameters.get("state"))) {
            YConnectLogger.error(this, "Not Match State.");
            throw new AuthorizationException("Not Match State.", "", "");
        }

        String authCodeString = parameters.get("code");
        if (authCodeString == null) {
            YConnectLogger.info(this, "No authorization code parameters.");
            return null;
        }

        return authCodeString;
    }

    /**
     * URIの中のcodeとstateをパースする
     *
     * @param request パース対象のURI
     * @throws AuthorizationException レスポンスにエラーが含まれているときに発生
     */
    private void parseUri(String request) throws AuthorizationException {
        YConnectLogger.debug(this, "Response Uri: " + request);

        if (request == null) {
            return;
        }

        for (String query : request.split("&")) {
            if (query.contains("=")) {
                String name = query.split("=")[0];
                String value = query.split("=")[1];
                parameters.put(name, value);
                YConnectLogger.debug(this, "put param: " + name + "=>" + value);
            }
        }
        YConnectLogger.debug(this, "all params: " + parameters.toQueryString());

        if (parameters.containsKey("error")) {
            String error = parameters.get("error");
            String errorDescription = parameters.get("error_description");
            String errorCode = parameters.get("error_code");

            YConnectLogger.error(
                    this,
                    "error="
                            + error
                            + ", error_description="
                            + errorDescription
                            + ", error_code="
                            + errorCode);

            throw new AuthorizationException(error, errorDescription, errorCode);
        }

        YConnectLogger.debug(this, "Finished Parsing: " + parameters);
    }
}
