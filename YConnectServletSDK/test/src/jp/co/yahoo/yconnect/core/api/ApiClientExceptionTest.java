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

package jp.co.yahoo.yconnect.core.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ApiClientExceptionTest {

    private static final String INVALID_REQUEST = "invalid_request";
    private static final String INVALID_TOKEN = "invalid_token";
    private static final String INSUFFICIENT_SCOPE = "insufficient_scope";

    @Test
    public void testIsInvalidRequestTrue() {
        ApiClientException exception = new ApiClientException(INVALID_REQUEST, "message");
        assertTrue(exception.isInvalidRequest());
    }

    @Test
    public void testIsInvalidRequestFalse() {
        ApiClientException exception = new ApiClientException(INVALID_TOKEN, "message");
        assertFalse(exception.isInvalidRequest());
    }

    @Test
    public void testIsInvalidTokenTrue() {
        ApiClientException exception = new ApiClientException(INVALID_TOKEN, "message");
        assertTrue(exception.isInvalidToken());
    }

    @Test
    public void testIsInvalidTokenFalse() {
        ApiClientException exception = new ApiClientException(INSUFFICIENT_SCOPE, "message");
        assertFalse(exception.isInvalidToken());
    }

    @Test
    public void testIsInsufficientScopeTrue() {
        ApiClientException exception = new ApiClientException(INSUFFICIENT_SCOPE, "message");
        assertTrue(exception.isInsufficientScope());
    }

    @Test
    public void testIsInsufficientScopeFalse() {
        ApiClientException exception = new ApiClientException(INVALID_REQUEST, "message");
        assertFalse(exception.isInsufficientScope());
    }
}
