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

/**
 * OAuth 2.0 Token Exception.
 *
 * @author Copyright (C) 2021 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
public class TokenException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Error code.
   */
  private String error = "";

  /**
   * Description of error.
   */
  private String errorDescription = "";

  /**
   * Error judge code.
   */
  private Integer errorCode = null;

  /**
   * TokenException Constructor.
   */
  public TokenException() {
    super();
  }

  /**
   * TokenException Constructor.
   * 
   * @param error Error code.
   * @param message Description of error.
   * @param errorCode Error judge code.
   */
  public TokenException(String error, String message, Integer errorCode) {
    super(message);
    this.error = error;
    this.errorDescription = message;
    this.errorCode = errorCode;
  }

  /**
   * TokenException Constructor.
   * 
   * @param error Error code.
   * @param message Description of error.
   * @param cause
   */
  public TokenException(String error, String message, Throwable cause) {
    super(message, cause);
    this.error = error;
    this.errorDescription = message;
  }

  /**
   * TokenException Constructor.
   * 
   * @param error Error code.
   * @param cause
   */
  public TokenException(String error, Throwable cause) {
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
   * Get error judge code.
   *
   * @return error judge code of type String.
   */
  public Integer getErrorCode() {
    return this.errorCode;
  }

  /**
   * Determine if this error is "invalid_request".
   * 
   * @return true if this error is "invalid_request"; false otherwise.
   */
  public boolean isInvalidRequest() {
    if ("invalid_request".equals(error)) {
      return true;
    }
    return false;
  }

  /**
   * Determine if this error is "unauthorized_client".
   * 
   * @return true if this error is "unauthorized_client"; false otherwise.
   */
  public boolean isUnauthorizedClient() {
    if ("unauthorized_client".equals(error)) {
      return true;
    }
    return false;
  }

  /**
   * Determine if this error is "access_deined".
   * 
   * @return true if this error is "access_denied"; false otherwise.
   */
  public boolean isAccessDenied() {
    if ("access_denied".equals(error)) {
      return true;
    }
    return false;
  }

  /**
   * Determine if this error is "unsupported_response_tyupe".
   * 
   * @return true if this error is "unsupported_response_type"; false otherwise.
   */
  public boolean isUnsupportedResponseType() {
    if ("unsupported_response_type".equals(error)) {
      return true;
    }
    return false;
  }

  /**
   * Determine if this error is "invalid_scope"; false otherwise.
   * 
   * @return true if this error is "invalid_scope"; false otherwise.
   */
  public boolean isInvalidScope() {
    if ("invalid_scope".equals(error)) {
      return true;
    }
    return false;
  }

  /**
   * Determine if this error is "server_error".
   * 
   * @return true if this error is "server_error"; false otherwise.
   */
  public boolean isServerError() {
    if ("server_error".equals(error)) {
      return true;
    }
    return false;
  }

  /**
   * Determine if this error is "temporarily_avaliable".
   * 
   * @return true if this error is "temporarily_available"; false otherwise.
   */
  public boolean isTemporarilyUnavailable() {
    if ("temporarily_available".equals(error)) {
      return true;
    }
    return false;
  }

  /**
   * Determine if this error is "invalid_grant".
   * 
   * @return true if this error is "invalid_grant"; false otherwise.
   */
  public boolean isInvalidGrant() {
    if ("invalid_grant".equals(error)) {
      return true;
    }
    return false;
  }

  public String toString() {
    return "error: " + this.error + " error_description: " + errorDescription;
  }

}
