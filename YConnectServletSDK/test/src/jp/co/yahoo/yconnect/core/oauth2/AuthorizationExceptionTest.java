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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AuthorizationExceptionTest {

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
        AuthorizationException exception =
                new AuthorizationException(INVALID_REQUEST, "message", "1000");
        assertTrue(exception.isInvalidRequest());
    }

    @Test
    public void testIsInvalidRequestFalse() {
        AuthorizationException exception =
                new AuthorizationException(UNAUTHORIZED_CLIENT, "message", "1000");
        assertFalse(exception.isInvalidRequest());
    }

    @Test
    public void testIsUnauthorizedClientTrue() {
        AuthorizationException exception =
                new AuthorizationException(UNAUTHORIZED_CLIENT, "message", "1000");
        assertTrue(exception.isUnauthorizedClient());
    }

    @Test
    public void testIsUnauthorizedClientFalse() {
        AuthorizationException exception =
                new AuthorizationException(ACCESS_DENIED, "message", "1000");
        assertFalse(exception.isUnauthorizedClient());
    }

    @Test
    public void testIsAccessDeniedTrue() {
        AuthorizationException exception =
                new AuthorizationException(ACCESS_DENIED, "message", "1000");
        assertTrue(exception.isAccessDenied());
    }

    @Test
    public void testIsAccessDeniedFalse() {
        AuthorizationException exception =
                new AuthorizationException(UNSUPPORTED_RESPONSE_TYPE, "message", "1000");
        assertFalse(exception.isAccessDenied());
    }

    @Test
    public void testIsUnsupportedResponseTypeTrue() {
        AuthorizationException exception =
                new AuthorizationException(UNSUPPORTED_RESPONSE_TYPE, "message", "1000");
        assertTrue(exception.isUnsupportedResponseType());
    }

    @Test
    public void testIsUnsupportedResponseTypeFalse() {
        AuthorizationException exception =
                new AuthorizationException(INVALID_SCOPE, "message", "1000");
        assertFalse(exception.isUnsupportedResponseType());
    }

    @Test
    public void testIsInvalidScopeTrue() {
        AuthorizationException exception =
                new AuthorizationException(INVALID_SCOPE, "message", "1000");
        assertTrue(exception.isInvalidScope());
    }

    @Test
    public void testIsInvalidScopeFalse() {
        AuthorizationException exception =
                new AuthorizationException(SERVER_ERROR, "message", "1000");
        assertFalse(exception.isInvalidScope());
    }

    @Test
    public void testIsServerErrorTrue() {
        AuthorizationException exception =
                new AuthorizationException(SERVER_ERROR, "message", "1000");
        assertTrue(exception.isServerError());
    }

    @Test
    public void testIsServerErrorFalse() {
        AuthorizationException exception =
                new AuthorizationException(TEMPORARILY_AVAILABLE, "message", "1000");
        assertFalse(exception.isServerError());
    }

    @Test
    public void testIsTemporarilyUnavailableTrue() {
        AuthorizationException exception =
                new AuthorizationException(TEMPORARILY_AVAILABLE, "message", "1000");
        assertTrue(exception.isTemporarilyUnavailable());
    }

    @Test
    public void testIsTemporarilyUnavailableFalse() {
        AuthorizationException exception =
                new AuthorizationException(INVALID_GRANT, "message", "1000");
        assertFalse(exception.isTemporarilyUnavailable());
    }

    @Test
    public void testIsInvalidGrantTrue() {
        AuthorizationException exception =
                new AuthorizationException(INVALID_GRANT, "message", "1000");
        assertTrue(exception.isInvalidGrant());
    }

    @Test
    public void testIsInvalidGrantFalse() {
        AuthorizationException exception =
                new AuthorizationException(INVALID_REQUEST, "message", "1000");
        assertFalse(exception.isInvalidGrant());
    }
}
