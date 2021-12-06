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

/**
 * OAuth 2.0 Bearer Token
 *
 * @author © Yahoo Japan
 */
public class BearerToken {

    /** Access Tokenの文字列 */
    private final String accessToken;

    /** Access Tokenの有効期限（Unixタイムスタンプ） */
    private final long expiration;

    /** Refresh Tokenの文字列 */
    private String refreshToken = null;

    /** Scopeの文字列 */
    private String scope = null;

    /**
     * OAuth2BearerTokenのコンストラクタです。
     *
     * @param accessToken Access Tokenの文字列
     * @param expiration Access Tokenの有効期限（Unixタイムスタンプ）
     */
    public BearerToken(String accessToken, long expiration) {
        this.accessToken = accessToken;
        this.expiration = expiration;
    }

    public BearerToken(String accessToken, long expiration, String refreshToken) {
        this.accessToken = accessToken;
        this.expiration = expiration;
        this.refreshToken = refreshToken;
    }

    public BearerToken(String accessToken, long expiration, String refreshToken, String scope) {
        this.accessToken = accessToken;
        this.expiration = expiration;
        this.refreshToken = refreshToken;
        this.scope = scope;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiration() {
        return expiration;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getScope() {
        return scope;
    }

    /**
     * Authorizationヘッダ用の文字列を返します。
     *
     * @return Access Tokenの文字列
     */
    public String toAuthorizationHeader() {
        return getAccessToken();
    }

    /**
     * Query String形式の文字列を返します。
     *
     * @return クエリ文字列
     */
    public String toQueryString() {
        return "access_token=" + accessToken;
    }

    public String toString() {
        String result = "{ access_token: " + accessToken + ", expiration: " + expiration;
        if (refreshToken != null) {
            result += ", refresh_token: " + refreshToken;
        }
        if (scope != null) {
            result += ", scope: " + scope;
        }
        result += "}";
        return result;
    }
}
