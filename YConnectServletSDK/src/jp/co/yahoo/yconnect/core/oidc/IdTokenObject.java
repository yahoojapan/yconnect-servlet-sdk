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

/**
 * IdToken Object Class
 *
 * @author Copyright (C) 2016 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
public class IdTokenObject {

  private String type;

  private String algorithm;

  private String iss;

  private String userId;

  private ArrayList<String> aud;

  private String nonce;

  private long exp;

  private long iat;

  private String signature;

  public IdTokenObject() {
  }

  public IdTokenObject(String type, String algorithm, String iss, String userId,
      ArrayList<String> aud, String nonce, long exp, long iat, String signature) {
    setType(type);
    setAlgorithm(algorithm);
    setIss(iss);
    setUserId(userId);
    setAud(aud);
    setNonce(nonce);
    setExp(exp);
    setIat(iat);
    setSignature(signature);
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getAlgorithm() {
    return this.algorithm;
  }

  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  public String getIss() {
    return iss;
  }

  public void setIss(String iss) {
    this.iss = iss;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public ArrayList<String> getAud() {
    return aud;
  }

  public void setAud(ArrayList<String> aud) {
    this.aud = aud;
  }

  public String getNonce() {
    return nonce;
  }

  public void setNonce(String nonce) {
    this.nonce = nonce;
  }

  public long getExp() {
    return exp;
  }

  public void setExp(long exp) {
    this.exp = exp;
  }

  public long getIat() {
    return iat;
  }

  public void setIat(long iat) {
    this.iat = iat;
  }

  public String getSignature() {
    return this.signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public String toString() {
    String json =
        "{" + "\"iss\":\"" + iss + "\"," + "\"user_id\":\"" + userId + "\"," + "\"aud\":\""
            + aud.toString() + "\"," + "\"nonce\":\"" + nonce + "\"," + "\"exp\":" + exp + ","
            + "\"iat\":" + iat +
            "}";
    return json;
  }

}
