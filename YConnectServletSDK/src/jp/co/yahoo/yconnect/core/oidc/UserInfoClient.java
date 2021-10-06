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

import jp.co.yahoo.yconnect.core.api.ApiClient;
import jp.co.yahoo.yconnect.core.api.ApiClientException;
import jp.co.yahoo.yconnect.core.oauth2.BearerToken;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

/**
 * UserInfo Client Class
 *
 * @author Copyright (C) 2016 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
public class UserInfoClient extends ApiClient {

  private UserInfoObject userInfoObject;

  public UserInfoClient(String accessTokenString) {
    super(accessTokenString);
  }

  public UserInfoClient(BearerToken accessToken) {
    super(accessToken);
  }

  @Override
  public void fetchResouce(String url, String method) throws ApiClientException {

    setParameter("schema", OIDCScope.OPENID);

    super.fetchResouce(url, method);

    String json = getResponse();

    userInfoParser(json);
  }

  private void userInfoParser(String json) {

    JsonReader jsonReader = Json.createReader(new StringReader(json));
    JsonObject rootObject = jsonReader.readObject();
    jsonReader.close();
    JsonObject rootJsonObject = (JsonObject) rootObject;

    if (rootJsonObject.containsKey("sub")) {
      userInfoObject = new UserInfoObject((String) rootJsonObject.getString("sub"));
    }
    if (rootJsonObject.containsKey("ppid_sub")) {
      userInfoObject.setPpidSub((String) rootJsonObject.getString("ppid_sub"));
    }
    if (rootJsonObject.containsKey("locale")) {
      userInfoObject.setLocale((String) rootJsonObject.getString("locale"));
    }
    if (rootJsonObject.containsKey("name")) {
      userInfoObject.setName((String) rootJsonObject.getString("name"));
    }
    if (rootJsonObject.containsKey("given_name")) {
      userInfoObject.setGivenName((String) rootJsonObject.getString("given_name"));
    }
    if (rootJsonObject.containsKey("given_name#ja-Kana-JP")) {
      userInfoObject.setGivenNameJaKanaJp((String) rootJsonObject
          .getString("given_name#ja-Kana-JP"));
    }
    if (rootJsonObject.containsKey("given_name#ja-Hani-JP")) {
      userInfoObject.setGivenNameJaHaniJp((String) rootJsonObject
          .getString("given_name#ja-Hani-JP"));
    }
    if (rootJsonObject.containsKey("family_name")) {
      userInfoObject.setFamilyName((String) rootJsonObject.getString("family_name"));
    }
    if (rootJsonObject.containsKey("family_name#ja-Kana-JP")) {
      userInfoObject.setFamilyNameJaKanaJp((String) rootJsonObject
          .getString("family_name#ja-Kana-JP"));
    }
    if (rootJsonObject.containsKey("family_name#ja-Hani-JP")) {
      userInfoObject.setFamilyNameJaHaniJp((String) rootJsonObject
          .getString("family_name#ja-Hani-JP"));
    }
    if (rootJsonObject.containsKey("email")) {
      userInfoObject.setEmail((String) rootJsonObject.getString("email"));
    }
    if (rootJsonObject.containsKey("email_verified")) {
      try{
        userInfoObject.setEmailVerified(((Boolean) rootJsonObject.getBoolean("email_verified"))
            .toString());
      } catch(ClassCastException e) {
        userInfoObject.setEmailVerified("false");
      }
    }
    if (rootJsonObject.containsKey("gender")) {
      userInfoObject.setGender((String) rootJsonObject.getString("gender"));
    }
    if (rootJsonObject.containsKey("zoneinfo")) {
      userInfoObject.setZoneinfo((String) rootJsonObject.getString("zoneinfo"));
    }
    if (rootJsonObject.containsKey("birthdate")) {
      userInfoObject.setBirthdate((String) rootJsonObject.getString("birthdate"));
    }
    if (rootJsonObject.containsKey("nickname")) {
      userInfoObject.setNickname((String) rootJsonObject.getString("nickname"));
    }
    if (rootJsonObject.containsKey("picture")) {
      userInfoObject.setPicture((String) rootJsonObject.getString("picture"));
    }
    if (rootJsonObject.containsKey("address")) {
      if (rootJsonObject.get("address") != null) {
        JsonObject addressJSONObject = (JsonObject) rootJsonObject.get("address");
        addressParser(addressJSONObject);
      }
    }

    userInfoObject.setJsonObject(rootJsonObject);
  }

  private void addressParser(JsonObject addressJSONObject) {
    if (addressJSONObject.containsKey("country")) {
      userInfoObject.setAddressCountry((String) addressJSONObject.getString("country"));
    }
    if(addressJSONObject.containsKey("postal_code")) {
      userInfoObject.setAddressPostalCode((String) addressJSONObject.getString("postal_code"));
    }
    if(addressJSONObject.containsKey("region")) {
      userInfoObject.setAddressRegion((String) addressJSONObject.getString("region"));
    }
    if(addressJSONObject.containsKey("locality")) {
      userInfoObject.setAddressLocality((String) addressJSONObject.getString("locality"));
    }
    if(addressJSONObject.containsKey("formatted")) {
      userInfoObject.setAddressFormatted((String) addressJSONObject.getString("formatted"));
    }
  }

  public UserInfoObject getUserInfoObject() {
    return userInfoObject;
  }
}
