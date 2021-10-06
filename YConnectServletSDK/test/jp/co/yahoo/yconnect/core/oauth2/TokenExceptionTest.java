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

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TokenExceptionTest {

    private static final String INVALID_REQUEST = "invalid_request";
    private static final String UNAUTHORIZED_CLIENT = "unauthorized_client";
    private static final String ACCESS_DENIED = "access_denied";
    private static final String UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";
    private static final String INVALID_SCOPE = "invalid_scope";
    private static final String SERVER_ERROR = "server_error";
    private static final String TEMPORARILY_AVAILABLE = "temporarily_available";
    private static final String INVALID_GRANT = "invalid_grant";

    @Test
    public void testIsInvalidRequestTrue() {
        TokenException exception = new TokenException(INVALID_REQUEST, "message", 1000);
        assertTrue(exception.isInvalidRequest());
    }

    @Test
    public void testIsInvalidRequestFalse() {
        TokenException exception = new TokenException(UNAUTHORIZED_CLIENT, "message", 1000);
        assertFalse(exception.isInvalidRequest());
    }

    @Test
    public void testIsUnauthorizedClientTrue() {
        TokenException exception = new TokenException(UNAUTHORIZED_CLIENT, "message", 1000);
        assertTrue(exception.isUnauthorizedClient());
    }

    @Test
    public void testIsUnauthorizedClientFalse() {
        TokenException exception = new TokenException(ACCESS_DENIED, "message", 1000);
        assertFalse(exception.isUnauthorizedClient());
    }

    @Test
    public void testIsAccessDeniedTrue() {
        TokenException exception = new TokenException(ACCESS_DENIED, "message", 1000);
        assertTrue(exception.isAccessDenied());
    }

    @Test
    public void testIsAccessDeniedFalse() {
        TokenException exception = new TokenException(UNSUPPORTED_RESPONSE_TYPE, "message", 1000);
        assertFalse(exception.isAccessDenied());
    }

    @Test
    public void testIsUnsupportedResponseTypeTrue() {
        TokenException exception = new TokenException(UNSUPPORTED_RESPONSE_TYPE, "message", 1000);
        assertTrue(exception.isUnsupportedResponseType());
    }

    @Test
    public void testIsUnsupportedResponseTypeFalse() {
        TokenException exception = new TokenException(INVALID_SCOPE, "message", 1000);
        assertFalse(exception.isUnsupportedResponseType());
    }

    @Test
    public void testIsInvalidScopeTrue() {
        TokenException exception = new TokenException(INVALID_SCOPE, "message", 1000);
        assertTrue(exception.isInvalidScope());
    }

    @Test
    public void testIsInvalidScopeFalse() {
        TokenException exception = new TokenException(SERVER_ERROR, "message", 1000);
        assertFalse(exception.isInvalidScope());
    }

    @Test
    public void testIsServerErrorTrue() {
        TokenException exception = new TokenException(SERVER_ERROR, "message", 1000);
        assertTrue(exception.isServerError());
    }

    @Test
    public void testIsServerErrorFalse() {
        TokenException exception = new TokenException(TEMPORARILY_AVAILABLE, "message", 1000);
        assertFalse(exception.isServerError());
    }

    @Test
    public void testIsTemporarilyUnavailableTrue() {
        TokenException exception = new TokenException(TEMPORARILY_AVAILABLE, "message", 1000);
        assertTrue(exception.isTemporarilyUnavailable());
    }

    @Test
    public void testIsTemporarilyUnavailableFalse() {
        TokenException exception = new TokenException(INVALID_GRANT, "message", 1000);
        assertFalse(exception.isTemporarilyUnavailable());
    }

    @Test
    public void testIsInvalidGrantTrue() {
        TokenException exception = new TokenException(INVALID_GRANT, "message", 1000);
        assertTrue(exception.isInvalidGrant());
    }

    @Test
    public void testIsInvalidGrantFalse() {
        TokenException exception = new TokenException(INVALID_REQUEST, "message", 1000);
        assertFalse(exception.isInvalidGrant());
    }
}