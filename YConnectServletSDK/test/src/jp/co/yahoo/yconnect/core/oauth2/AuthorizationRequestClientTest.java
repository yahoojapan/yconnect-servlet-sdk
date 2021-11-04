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

import jp.co.yahoo.yconnect.core.oidc.OIDCDisplay;
import jp.co.yahoo.yconnect.core.oidc.OIDCPrompt;
import jp.co.yahoo.yconnect.core.oidc.OIDCScope;
import org.junit.Test;

public class AuthorizationRequestClientTest {

    @Test
    public void testGenerateAuthorizationUri() {
        AuthorizationRequestClient client =
                new AuthorizationRequestClient("https://example.co.jp", "client_id");

        client.setState("state");
        client.setRedirectUri("https://example.co.jp/callback");
        client.setResponseType("code");
        client.setParameter("display", OIDCDisplay.DEFAULT);
        client.setParameter("prompt", OIDCPrompt.LOGIN + " " + OIDCPrompt.CONSENT);
        client.setParameter("scope", OIDCScope.OPENID + " " + OIDCScope.PROFILE);
        client.setParameter("nonce", "nonce_sample");
        client.setParameter("max_age", "3600");
        client.setParameter("code_challenge", "code_challenge_sample");
        client.setParameter("code_challenge_method", "S256");

        String expect =
                "https://example.co.jp?max_age=3600&display=page&scope=openid+profile&response_type=code&"
                        + "code_challenge_method=S256&state=state&redirect_uri=https%3A%2F%2Fexample.co.jp%2Fcallback&"
                        + "prompt=login+consent&nonce=nonce_sample&code_challenge=code_challenge_sample&client_id=client_id";
        assertEquals(expect, client.generateAuthorizationUri().toString());
    }

    @Test
    public void testGenerateAuthorizationUriReturnsNull() {
        AuthorizationRequestClient client =
                new AuthorizationRequestClient("https://example.co.jp^/", "client_id");

        client.setState("state");
        client.setRedirectUri("https://example.co.jp/callback");
        client.setResponseType("code");

        assertNull(client.generateAuthorizationUri());
    }
}
