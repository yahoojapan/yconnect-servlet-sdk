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

import jp.co.yahoo.yconnect.core.oidc.IdTokenDecoder;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DataFormatException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * JSON Web Token 検証クラス
 * 
 * @author Copyright (C) 2016 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
public class JWTVerification {

  private String idTokenString;

  private String dataPart;

  private String clientSecret;

  /**
   * JWTVerificationのコンストラクタ
   * 
   * @param clientSecret
   * @param idTokenString
   */
  public JWTVerification(String clientSecret, String idTokenString) {
    this.clientSecret = clientSecret;
    this.idTokenString = idTokenString;
  }

  /**
   * headerとpayloadの部分からsignatureを生成して一致しているかどうかを返す
   * 
   * @return
   * @throws DataFormatException
   */
  public boolean verifyJWT() throws DataFormatException {
    IdTokenDecoder idTokenDecoder = new IdTokenDecoder(this.idTokenString);
    IdTokenObject idTokenObject = idTokenDecoder.decode();
    return idTokenObject.getSignature().equals(generateSignature());
  }

  /**
   * HMAC-SHA256の暗号化のチェックを行なうためのシグネチャを生成
   * 
   * @return 生成したシグネチャ
   */
  private String generateSignature() {
    // macのインスタンスを生成する
    Mac sha256_HMAC = null;
    try {
      sha256_HMAC = Mac.getInstance("HmacSHA256");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    // secret_keyの作成
    SecretKeySpec secret_key = null;
    try {
      secret_key = new SecretKeySpec(this.clientSecret.getBytes("UTF-8"), "HmacSHA256");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    // macの初期化
    try {
      sha256_HMAC.init(secret_key);
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    }

    String[] idTokenArray = this.idTokenString.split("\\.");
    this.dataPart = idTokenArray[0] + "." + idTokenArray[1];

    String hash = null;
    try {
      hash = Base64.encodeBase64String(sha256_HMAC.doFinal(this.dataPart.getBytes("UTF-8")));
    } catch (IllegalStateException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    // URLSafeなBase64に変換
    hash = hash.replace("=", "");
    hash = hash.replace("+", "-");
    hash = hash.replace("/", "_");

    return hash;
  }

}
