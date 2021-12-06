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

package jp.co.yahoo.yconnect.core.oidc;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import jp.co.yahoo.yconnect.core.api.ApiClient;
import jp.co.yahoo.yconnect.core.api.ApiClientException;
import jp.co.yahoo.yconnect.core.oauth2.BearerToken;

/**
 * UserInfo Client Class
 *
 * @author © Yahoo Japan
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
    public void fetchResource(String url, String method) throws ApiClientException {

        setParameter("schema", OIDCScope.OPENID);

        super.fetchResource(url, method);

        String json = getResponse();

        userInfoParser(json);
    }

    private void userInfoParser(String json) {

        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject rootObject = jsonReader.readObject();
        jsonReader.close();

        if (rootObject.containsKey("sub")) {
            userInfoObject = new UserInfoObject(rootObject.getString("sub"));
        }
        if (rootObject.containsKey("ppid_sub")) {
            userInfoObject.setPpidSub(rootObject.getString("ppid_sub"));
        }
        if (rootObject.containsKey("locale")) {
            userInfoObject.setLocale(rootObject.getString("locale"));
        }
        if (rootObject.containsKey("name")) {
            userInfoObject.setName(rootObject.getString("name"));
        }
        if (rootObject.containsKey("given_name")) {
            userInfoObject.setGivenName(rootObject.getString("given_name"));
        }
        if (rootObject.containsKey("given_name#ja-Kana-JP")) {
            userInfoObject.setGivenNameJaKanaJp(rootObject.getString("given_name#ja-Kana-JP"));
        }
        if (rootObject.containsKey("given_name#ja-Hani-JP")) {
            userInfoObject.setGivenNameJaHaniJp(rootObject.getString("given_name#ja-Hani-JP"));
        }
        if (rootObject.containsKey("family_name")) {
            userInfoObject.setFamilyName(rootObject.getString("family_name"));
        }
        if (rootObject.containsKey("family_name#ja-Kana-JP")) {
            userInfoObject.setFamilyNameJaKanaJp(rootObject.getString("family_name#ja-Kana-JP"));
        }
        if (rootObject.containsKey("family_name#ja-Hani-JP")) {
            userInfoObject.setFamilyNameJaHaniJp(rootObject.getString("family_name#ja-Hani-JP"));
        }
        if (rootObject.containsKey("email")) {
            userInfoObject.setEmail(rootObject.getString("email"));
        }
        if (rootObject.containsKey("email_verified")) {
            try {
                userInfoObject.setEmailVerified(
                        ((Boolean) rootObject.getBoolean("email_verified")).toString());
            } catch (ClassCastException e) {
                userInfoObject.setEmailVerified("false");
            }
        }
        if (rootObject.containsKey("gender")) {
            userInfoObject.setGender(rootObject.getString("gender"));
        }
        if (rootObject.containsKey("zoneinfo")) {
            userInfoObject.setZoneinfo(rootObject.getString("zoneinfo"));
        }
        if (rootObject.containsKey("birthdate")) {
            userInfoObject.setBirthdate(rootObject.getString("birthdate"));
        }
        if (rootObject.containsKey("nickname")) {
            userInfoObject.setNickname(rootObject.getString("nickname"));
        }
        if (rootObject.containsKey("picture")) {
            userInfoObject.setPicture(rootObject.getString("picture"));
        }
        if (rootObject.containsKey("address")) {
            if (rootObject.get("address") != null) {
                JsonObject addressJSONObject = (JsonObject) rootObject.get("address");
                addressParser(addressJSONObject);
            }
        }

        userInfoObject.setJsonObject(rootObject);
    }

    private void addressParser(JsonObject addressJSONObject) {
        if (addressJSONObject.containsKey("country")) {
            userInfoObject.setAddressCountry(addressJSONObject.getString("country"));
        }
        if (addressJSONObject.containsKey("postal_code")) {
            userInfoObject.setAddressPostalCode(addressJSONObject.getString("postal_code"));
        }
        if (addressJSONObject.containsKey("region")) {
            userInfoObject.setAddressRegion(addressJSONObject.getString("region"));
        }
        if (addressJSONObject.containsKey("locality")) {
            userInfoObject.setAddressLocality(addressJSONObject.getString("locality"));
        }
        if (addressJSONObject.containsKey("formatted")) {
            userInfoObject.setAddressFormatted(addressJSONObject.getString("formatted"));
        }
    }

    public UserInfoObject getUserInfoObject() {
        return userInfoObject;
    }
}
