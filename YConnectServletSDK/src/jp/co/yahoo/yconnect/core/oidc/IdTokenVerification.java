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

package jp.co.yahoo.yconnect.core.oidc;

import java.util.ArrayList;
import java.util.zip.DataFormatException;

import jp.co.yahoo.yconnect.core.util.YConnectLogger;

/**
 * IdToken Verification Class
 *
 * @author Copyright (C) 2016 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
public class IdTokenVerification {

  private String error = "";

  private String errorDescription = "";

  private final static String TAG = IdTokenVerification.class.getSimpleName();

  // 10 minutes
  private final static long ACCEPTABLE_RANGE = 600L; // sec

  public boolean check(String issuer, String authNonce, String clientId,
      IdTokenObject idTokenObject, String clientSecret, String idTokenString)
      throws DataFormatException {

    YConnectLogger.info(TAG, "Check ID Token in the Claim from check id endpoint.");

    String type = idTokenObject.getType();
    String algorithm = idTokenObject.getAlgorithm();
    String iss = idTokenObject.getIss();
    ArrayList<String> aud = idTokenObject.getAud();
    String audString = aud.get(0);
    long exp = idTokenObject.getExp();
    long iat = idTokenObject.getIat();
    String nonce = idTokenObject.getNonce();

    if (!"JWT".equals(type)) {
      YConnectLogger.error(TAG, "Invalid type.");
      this.error = "invalid_type";
      this.errorDescription = "The type is not JWT. (" + type + ")";
      return false;
    }

    if (!"HS256".equals(algorithm)) {
      YConnectLogger.error(TAG, "Invalid algorithm.");
      this.error = "invalid_algorithm";
      this.errorDescription = "The algorithm is not HmacSHA256. (" + algorithm + ")";
      return false;
    }

    // Is iss equal to issuer ? (https://auth.login.yahoo.co.jp)
    if (!issuer.equals(iss)) {
      YConnectLogger.error(TAG, "Invalid issuer.");
      this.error = "invalid_issuer";
      this.errorDescription = "The issuer did not match. (" + iss + ")";
      return false;
    }

    // Is nonce equal to this nonce (was issued at the request authorization) ?
    if (!authNonce.equals(nonce)) {
      YConnectLogger.error(TAG, "Not match nonce.");
      this.error = "not_match_nonce";
      this.errorDescription = "The nonce did not match. (" + nonce + ")";
      return false;
    }

    // Is aud equal to the client_id (Application ID) ?
    if (!audString.equals(clientId)) {
      YConnectLogger.error(TAG, "Invalid audience.");
      this.error = "invalid_audience";
      this.errorDescription = "The client id did not match. (" + audString + ")";
      return false;
    }

    long currentTime = this.getCurrentTime();

    // Is current time less than exp ?
    if (exp < currentTime) {
      YConnectLogger.error(TAG, "Expired ID Token.");
      this.error = "expired_id_token";
      this.errorDescription = "Re-issue Id Token. (" + exp + ")";
      return false;
    }

    YConnectLogger.debug(TAG,
        "Expiraiton: " + Long.toString(exp) + "(Current Tme: " + Long.toString(currentTime) + ")");

    // current time - iat > ??? for prevent attacks
    if ((currentTime - iat) > ACCEPTABLE_RANGE) {
      YConnectLogger.error(TAG, "Over acceptable range.");
      this.error = "over_acceptable_range";
      this.errorDescription = "This access has expired possible. (" + iat + ")";
      return false;
    }

    YConnectLogger.debug(TAG, "Current time - iat = " + Long.toString(currentTime - iat) + " sec");
    YConnectLogger.debug(TAG,
        "Issued time: " + Long.toString(iat) + "(Current Tme: " + Long.toString(currentTime) + ")");

    // JWTの検証を行なう
    JWTVerification verifier = new JWTVerification(clientSecret, idTokenString);

    if (!verifier.verifyJWT()) {
      YConnectLogger.error(TAG, "Signature verification failed.");
      this.error = "invalid_signature";
      this.errorDescription = "Signature verification failed.";
      return false;
    }
    return true;
  }

  protected long getCurrentTime() {
    return System.currentTimeMillis() / 1000;
  }

  public String getErrorMessage() {
    return this.error;
  }

  public String getErrorDescriptionMessage() {
    return this.errorDescription;
  }

}
