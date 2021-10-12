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

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.commons.codec.binary.Base64;

import jp.co.yahoo.yconnect.core.http.HttpHeaders;
import jp.co.yahoo.yconnect.core.http.HttpParameters;
import jp.co.yahoo.yconnect.core.http.YHttpClient;
import jp.co.yahoo.yconnect.core.util.YConnectLogger;

/**
 * Token Client Class
 *
 * @author Copyright (C) 2016 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
public class TokenClient extends AbstractTokenClient {

  private final static String TAG = TokenClient.class.getSimpleName();

  private YHttpClient client;

  private String authorizationCode;

  private String redirectUri;

  private String codeVerifier = null;

  private String idToken;

  public TokenClient(String endpointUrl, String authorizationCode, String redirectUri,
      String clientId, String clientSecret) {
    super(endpointUrl, clientId, clientSecret);
    this.authorizationCode = authorizationCode;
    this.redirectUri = redirectUri;
  }

  public TokenClient(String endpointUrl, String authorizationCode, String redirectUri,
                     String clientId, String clientSecret, String codeVerifier) {
    super(endpointUrl, clientId, clientSecret);
    this.authorizationCode = authorizationCode;
    this.redirectUri = redirectUri;
    this.codeVerifier = codeVerifier;
  }

  @Override
  public void fetch() throws TokenException, Exception {

    HttpParameters parameters = new HttpParameters();
    parameters.put("grant_type", OAuth2GrantType.AUTHORIZATION_CODE);
    parameters.put("code", authorizationCode);
    parameters.put("redirect_uri", redirectUri);

    if(codeVerifier != null)
      parameters.put("code_verifier", codeVerifier);

    String credential = clientId + ":" + clientSecret;
    String basic = new String(Base64.encodeBase64(credential.getBytes()));

    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
    requestHeaders.put("Authorization", "Basic " + basic);

    client = getYHttpClient();
    client.requestPost(endpointUrl, parameters, requestHeaders);

    YConnectLogger.debug(TAG, client.getResponseHeaders().toString());
    YConnectLogger.debug(TAG, client.getResponseBody().toString());

    String json = client.getResponseBody();
    JsonReader jsonReader = Json.createReader(new StringReader(json));

    JsonObject jsonObject = jsonReader.readObject();
    jsonReader.close();

    int statusCode = client.getStatusCode();

    checkErrorResponse(statusCode, jsonObject);

    String accessTokenString = (String) jsonObject.getString("access_token");
    long expiresIn = jsonObject.getJsonNumber("expires_in").longValue();
    String refreshToken = (String) jsonObject.getString("refresh_token");
    accessToken = new BearerToken(accessTokenString, expiresIn, refreshToken);
    idToken = (String) jsonObject.getString("id_token");

  }

  public String getIdToken() {
    return idToken;
  }

  protected YHttpClient getYHttpClient() {
    return new YHttpClient();
  }
}
