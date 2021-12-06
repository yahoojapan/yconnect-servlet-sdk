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

package jp.co.yahoo.yconnect.core.oauth2;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import jp.co.yahoo.yconnect.core.http.HttpHeaders;
import jp.co.yahoo.yconnect.core.http.HttpParameters;
import jp.co.yahoo.yconnect.core.http.YHttpClient;
import jp.co.yahoo.yconnect.core.util.YConnectLogger;
import org.apache.commons.codec.binary.Base64;

/**
 * Abstract Token Client Class
 *
 * @author © Yahoo Japan
 */
abstract class AbstractTokenClient {

    private static final String TAG = AbstractTokenClient.class.getSimpleName();

    protected String endpointUrl;

    protected String clientId;

    protected String clientSecret;

    protected BearerToken accessToken;

    public AbstractTokenClient(String endpointUrl, String clientId, String clientSecret) {
        this.endpointUrl = endpointUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    abstract void fetch() throws TokenException;

    public BearerToken getAccessToken() {
        return accessToken;
    }

    /**
     * エンドポイントに対してHTTPリクエストします。
     *
     * @param parameters postパラメータ
     * @return HTTPレスポンスボディのJSONオブジェクト
     * @throws TokenException レスポンスにエラーが含まれているときに発生
     */
    protected JsonObject request(HttpParameters parameters) throws TokenException {
        String credential = clientId + ":" + clientSecret;
        String basic = new String(Base64.encodeBase64(credential.getBytes()));

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        requestHeaders.put("Authorization", "Basic " + basic);

        YHttpClient client = getYHttpClient();
        client.requestPost(endpointUrl, parameters, requestHeaders);

        YConnectLogger.debug(TAG, client.getResponseHeaders().toString());
        YConnectLogger.debug(TAG, client.getResponseBody());

        String json = client.getResponseBody();
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        int statusCode = client.getStatusCode();

        checkErrorResponse(statusCode, jsonObject);

        return jsonObject;
    }

    protected void checkErrorResponse(int statusCode, JsonObject jsonObject) throws TokenException {
        if (statusCode == 200) {
            return;
        }
        if (statusCode < 400) {
            YConnectLogger.error(TAG, "An unexpected error has occurred.");
            throw new TokenException("An unexpected error has occurred.", "", (Integer) null);
        }

        JsonString errorJsonString = jsonObject.getJsonString("error");

        if (errorJsonString == null) {
            YConnectLogger.error(TAG, "An unexpected error has occurred.");
            throw new TokenException("An unexpected error has occurred.", "", (Integer) null);
        }

        String error = errorJsonString.getString();
        String errorDescription = jsonObject.getString("error_description");
        int errorCode = jsonObject.getInt("error_code");

        YConnectLogger.error(TAG, error + " / " + errorDescription + " / " + errorCode);
        throw new TokenException(error, errorDescription, errorCode);
    }

    protected YHttpClient getYHttpClient() {
        return new YHttpClient();
    }
}
