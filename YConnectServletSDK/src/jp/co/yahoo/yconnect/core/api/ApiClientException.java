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

/**
 * API Client Exception.
 *
 * @author © Yahoo Japan
 */
public class ApiClientException extends Exception {

    private static final long serialVersionUID = 1L;

    /** Error code. */
    private String error = "";

    /** Description of error. */
    private String errorDescription = "";

    /** ApiClientException Constructor. */
    public ApiClientException() {
        super();
    }

    /**
     * ApiClientException Constructor.
     *
     * @param error Error code.
     * @param message Description of error.
     */
    public ApiClientException(String error, String message) {
        super(message);
        this.error = error;
        this.errorDescription = message;
    }

    /**
     * ApiClientException Constructor.
     *
     * @param error Error code.
     * @param message Description of error.
     * @param cause The cause.
     */
    public ApiClientException(String error, String message, Throwable cause) {
        super(message, cause);
        this.error = error;
        this.errorDescription = message;
    }

    /**
     * ApiClientException Constructor.
     *
     * @param error Error code.
     * @param cause The cause.
     */
    public ApiClientException(String error, Throwable cause) {
        super(cause);
        this.error = error;
    }

    /**
     * Get error code.
     *
     * @return error code of type String.
     */
    public String getError() {
        return this.error;
    }

    /**
     * Get description of error.
     *
     * @return description of type String.
     */
    public String getErrorDescription() {
        return this.errorDescription;
    }

    /**
     * Determine if this error is "invalid_request".
     *
     * @return true if this error is "invalid_request"; false otherwise.
     */
    public boolean isInvalidRequest() {
        return "invalid_request".equals(error);
    }

    /**
     * Determine if this error is "invalid_token".
     *
     * @return true if this error is "invalid_token"; false otherwise.
     */
    public boolean isInvalidToken() {
        return "invalid_token".equals(error);
    }

    /**
     * Determine if this error is "insufficient_scope".
     *
     * @return true if this error is "insufficient_scope"; false otherwise.
     */
    public boolean isInsufficientScope() {
        return "insufficient_scope".equals(error);
    }

    public String toString() {
        return "error: " + this.error + " error_description: " + errorDescription;
    }
}
