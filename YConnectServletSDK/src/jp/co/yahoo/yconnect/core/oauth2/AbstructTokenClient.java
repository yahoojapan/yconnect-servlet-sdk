/**
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

package jp.co.yahoo.yconnect.core.oauth2;


import javax.json.JsonObject;

import jp.co.yahoo.yconnect.core.util.YConnectLogger;

/**
 * Abstract Token Client Class
 *
 * @author Copyright (C) 2016 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
abstract class AbstractTokenClient {

  private final static String TAG = AbstractTokenClient.class.getSimpleName();

  protected String endpointUrl;

  protected String clientId;

  protected String clientSecret;

  protected BearerToken accessToken;

  public AbstractTokenClient(String endpointUrl, String clientId, String clientSecret) {
    this.endpointUrl = endpointUrl;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  abstract void fetch() throws TokenException, Exception;

  public BearerToken getAccessToken() {
    return accessToken;
  }

  protected void checkErrorResponse(int statusCode, JsonObject jsonObject) throws TokenException {

    if (statusCode >= 400) {
      String error = (String) jsonObject.getString("error");
      if (error != null) {
        String errorDescription = (String) jsonObject.getString("error_description");
        YConnectLogger.error(TAG, error + " / " + errorDescription);
        throw new TokenException(error, errorDescription);
      } else {
        YConnectLogger.error(TAG, "An unexpected error has occurred.");
        throw new TokenException("An unexpected error has occurred.", "");
      }
    } else if (statusCode == 200) {
      return;
    } else {
      YConnectLogger.error(TAG, "An unexpected error has occurred.");
      throw new TokenException("An unexpected error has occurred.", "");
    }

  }

}
