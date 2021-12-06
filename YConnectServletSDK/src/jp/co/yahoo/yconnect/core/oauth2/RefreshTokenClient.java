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

import javax.json.JsonObject;
import jp.co.yahoo.yconnect.core.http.HttpParameters;

/**
 * Refresh Token Client Class
 *
 * @author © Yahoo Japan
 */
public class RefreshTokenClient extends AbstractTokenClient {

    private static final String TAG = RefreshTokenClient.class.getSimpleName();

    private final String refreshToken;

    private BearerToken accessToken;

    public RefreshTokenClient(
            String endpointUrl, String refreshToken, String clientId, String clientSecret) {
        super(endpointUrl, clientId, clientSecret);
        this.refreshToken = refreshToken;
    }

    /**
     * リフレッシュトークン取得をリクエストします
     *
     * @throws TokenException レスポンスにエラーが含まれているときに発生
     */
    public void fetch() throws TokenException {

        HttpParameters parameters = new HttpParameters();
        parameters.put("grant_type", OAuth2GrantType.REFRESH_TOKEN);
        parameters.put("refresh_token", refreshToken);

        JsonObject jsonObject = request(parameters);

        String accessTokenString = jsonObject.getString("access_token");
        long expiresIn = jsonObject.getJsonNumber("expires_in").longValue();
        accessToken = new BearerToken(accessTokenString, expiresIn, refreshToken);
    }

    public BearerToken getAccessToken() {
        return accessToken;
    }
}
