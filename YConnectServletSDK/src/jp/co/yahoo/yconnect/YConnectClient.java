/*
 * The MIT License (MIT)
 *
 * Copyright (C) 2021 Yahoo Japan Corporation. All Rights Reserved.
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

package jp.co.yahoo.yconnect;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.zip.DataFormatException;
import jp.co.yahoo.yconnect.core.api.ApiClient;
import jp.co.yahoo.yconnect.core.api.ApiClientException;
import jp.co.yahoo.yconnect.core.http.YHttpClient;
import jp.co.yahoo.yconnect.core.oauth2.*;
import jp.co.yahoo.yconnect.core.oauth2.ClientCallbackUriParser;
import jp.co.yahoo.yconnect.core.oidc.*;
import jp.co.yahoo.yconnect.core.util.StringUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * YConnect Client(Authorization Code Flow) Class
 *
 * @author Copyright (C) 2021 Yahoo Japan Corporation. All Rights Reserved.
 */
public class YConnectClient {

    private static final String AUTHORIZATION_ENDPOINT_URL =
            "https://auth.login.yahoo.co.jp/yconnect/v2/authorization";

    private static final String TOKEN_ENDPOINT_URL =
            "https://auth.login.yahoo.co.jp/yconnect/v2/token";

    private static final String USERINFO_ENDPOINT_URL =
            "https://userinfo.yahooapis.jp/yconnect/v2/attribute";

    private static final String PUBLIC_KEYS_ENDPOINT_URL =
            "https://auth.login.yahoo.co.jp/yconnect/v2/public-keys";

    private static final String ISSUER = "https://auth.login.yahoo.co.jp/yconnect/v2";

    // Default Parameters
    private String responseType = OAuth2ResponseType.CODE;
    private String display = OIDCDisplay.DEFAULT;
    private String prompt = OIDCPrompt.LOGIN;
    private String scope = null;
    private String nonce = null;
    private Long maxAge = null;
    private String plainCodeChallenge = null;

    private AuthorizationRequestClient requestClient;
    private ClientCallbackUriParser responseParser;
    private BearerToken accessToken;
    private String idToken;
    private UserInfoObject userInfoObject;
    private IdTokenVerification idTokenVerification;

    /** YConnectClientのコンストラクタ。 */
    public YConnectClient() {}

    /**
     * 初期化メソッド。 各パラメーターを設定する。
     *
     * @param clientId アプリケーションID
     * @param redirectUri リダイレクトURL
     * @param state リクエストとコールバック間の検証用のランダムな文字列を指定してください
     */
    public void init(String clientId, String redirectUri, String state) {
        requestClient = getAuthorizationRequestClient(clientId);
        requestClient.setRedirectUri(redirectUri);
        requestClient.setState(state);
    }

    /**
     * 初期化メソッド。 各パラメーターを設定する。
     *
     * @param clientId アプリケーションID
     * @param redirectUri リダイレクトURL
     * @param state リクエストとコールバック間の検証用のランダムな文字列を指定してください
     * @param responseType レスポンスタイプ（response_type）
     * @param display テンプレートの種類（display）
     * @param prompt クライアントが強制させたいアクション（prompt）
     * @param scope UserInfo APIから取得できる属性情報の指定（scope）
     * @param nonce リプレイアタック対策のランダムな文字列を指定してください
     */
    public void init(
            String clientId,
            String redirectUri,
            String state,
            String responseType,
            String display,
            String[] prompt,
            String[] scope,
            String nonce) {
        requestClient = getAuthorizationRequestClient(clientId);
        requestClient.setRedirectUri(redirectUri);
        requestClient.setState(state);
        this.responseType = responseType;
        this.display = display;
        this.prompt = StringUtil.implode(prompt);
        this.scope = StringUtil.implode(scope);
        this.nonce = nonce;
    }

    /**
     * 初期化メソッド。 各パラメーターを設定する。
     *
     * @param clientId アプリケーションID
     * @param redirectUri リダイレクトURL
     * @param state リクエストとコールバック間の検証用のランダムな文字列を指定してください
     * @param responseType レスポンスタイプ（response_type）
     * @param display テンプレートの種類（display）
     * @param prompt クライアントが強制させたいアクション（prompt）
     * @param scope UserInfo APIから取得できる属性情報の指定（scope）
     * @param nonce リプレイアタック対策のランダムな文字列を指定してください
     * @param maxAge 最大認証経過時間を指定してください
     * @param plainCodeChallenge ハッシュ化前の認可コード横取り攻撃対策文字列を指定してください
     */
    public void init(
            String clientId,
            String redirectUri,
            String state,
            String responseType,
            String display,
            String[] prompt,
            String[] scope,
            String nonce,
            Long maxAge,
            String plainCodeChallenge) {
        this.init(clientId, redirectUri, state, responseType, display, prompt, scope, nonce);
        this.maxAge = maxAge;
        this.plainCodeChallenge = plainCodeChallenge;
    }

    /**
     * AuthorizationエンドポイントへのリクエストURIを生成する。
     *
     * @return リクエストURI
     */
    public URI generateAuthorizationUri() throws UnsupportedEncodingException {
        requestClient.setResponseType(responseType);
        requestClient.setParameter("display", display);
        requestClient.setParameter("prompt", prompt);
        if (scope != null) requestClient.setParameter("scope", scope);
        if (nonce != null) requestClient.setParameter("nonce", nonce);
        if (maxAge != null) requestClient.setParameter("max_age", maxAge.toString());
        if (plainCodeChallenge != null) {
            requestClient.setParameter("code_challenge", generateCodeChallenge(plainCodeChallenge));
            requestClient.setParameter("code_challenge_method", "S256");
        }
        return requestClient.generateAuthorizationUri();
    }

    /**
     * コールバックURLの認可コードが付加されているか確認する。
     *
     * @param query クエリ文字列(ex. code=abc&state=xyz)
     * @return 認可コードの有無
     * @throws AuthorizationException クエリ文字列にエラーが含まれているときに発生
     */
    public boolean hasAuthorizationCode(String query) throws AuthorizationException {
        responseParser = new ClientCallbackUriParser(query);
        return responseParser.hasAuthorizationCode();
    }

    /**
     * コールバックURLに認可コードが付加されているか確認する。
     *
     * @param uri QueryStringを含むURIインスタンス (ex. https://example.com/callback?code=abc&state=xyz)
     * @return 認可コードの有無
     * @throws AuthorizationException クエリ文字列にエラーが含まれているときに発生
     */
    public boolean hasAuthorizationCode(URI uri) throws AuthorizationException {
        String requestQuery = null;
        if (uri != null && !uri.getQuery().equals("null")) {
            requestQuery = uri.getQuery();
        }
        return hasAuthorizationCode(requestQuery);
    }

    /**
     * 認可コードを取得する。
     *
     * @param state 初期化時に指定したstate値
     * @return 認可コードの文字列
     * @throws AuthorizationException stateが異なるときに発生
     */
    public String getAuthorizationCode(String state) throws AuthorizationException {
        return responseParser.getAuthorizationCode(state);
    }

    /**
     * Tokenエンドポイントにリクエストする。
     *
     * @param code 認可コード
     * @param clientId アプリケーションID
     * @param clientSecret シークレット
     * @param redirectUri リダイレクトURL
     * @throws TokenException レスポンスにエラーが含まれているときに発生
     */
    public void requestToken(String code, String clientId, String clientSecret, String redirectUri)
            throws TokenException {
        TokenClient tokenClient = getTokenClient(code, redirectUri, clientId, clientSecret);
        tokenClient.fetch();
        accessToken = tokenClient.getAccessToken();
        idToken = tokenClient.getIdToken();
    }

    /**
     * Tokenエンドポイントにリクエストする。
     *
     * @param code 認可コード
     * @param clientId アプリケーションID
     * @param clientSecret シークレット
     * @param redirectUri リダイレクトURL
     * @param codeVerifier 認可コード横取り攻撃対策用パラメータ
     * @throws TokenException レスポンスにエラーが含まれているときに発生
     */
    public void requestToken(
            String code,
            String clientId,
            String clientSecret,
            String redirectUri,
            String codeVerifier)
            throws TokenException {
        TokenClient tokenClient =
                getTokenClient(code, redirectUri, clientId, clientSecret, codeVerifier);
        tokenClient.fetch();
        accessToken = tokenClient.getAccessToken();
        idToken = tokenClient.getIdToken();
    }

    /**
     * アクセストークンを取得する。
     *
     * @return アクセストークンの文字列
     */
    public String getAccessToken() {
        return accessToken.getAccessToken();
    }

    /**
     * アクセストークンの有効期限を取得する。
     *
     * @return アクセストークンの有効期限(タイムスタンプ)
     */
    public long getAccessTokenExpiration() {
        return accessToken.getExpiration();
    }

    /**
     * リフレッシュトークンを取得する。
     *
     * @return リフレッシュトークンの文字列
     */
    public String getRefreshToken() {
        return accessToken.getRefreshToken();
    }

    /**
     * IDトークンを取得する。
     *
     * @return 暗号化されているIDトークンの文字列
     */
    public String getIdToken() {
        return idToken;
    }

    /**
     * 暗号化されたIDトークンを復号する。
     *
     * @param idTokenString 取得したIDトークンの文字列
     * @return IDトークンの文字列から生成したIdTokenObjectを返却
     * @throws DataFormatException 入力された文字列がJWTフォーマットではないときに発生
     */
    public IdTokenObject decodeIdToken(String idTokenString) throws DataFormatException {
        IdTokenDecoder idTokenDecoder = new IdTokenDecoder(idTokenString);
        return idTokenDecoder.decode();
    }

    /**
     * トークン自体に異常があるかどうかを判定する。
     *
     * @param nonce Authorizationリクエスト時に指定したnonce値
     * @param clientId アプリケーションID
     * @param idTokenString 取得したIDトークンの文字列
     * @return IDトークン検証が正しい場合にはtrue, それ以外の場合にはfalseを返却
     * @throws DataFormatException 無効なIDトークンが指定された場合に発生します
     */
    public boolean verifyIdToken(String nonce, String clientId, String idTokenString)
            throws DataFormatException, ApiClientException {
        PublicKeysClient publicKeysClient = getPublicKeysClient();
        publicKeysClient.fetchResource(PUBLIC_KEYS_ENDPOINT_URL);
        PublicKeysObject publicKeysObject = publicKeysClient.getPublicKeysObject();

        IdTokenDecoder idTokenDecoder = new IdTokenDecoder(idTokenString);
        IdTokenObject idTokenObject = idTokenDecoder.decode();
        this.idTokenVerification = getIdTokenVerification();
        return this.idTokenVerification.check(
                ISSUER,
                nonce,
                clientId,
                idTokenObject,
                publicKeysObject,
                idTokenString,
                accessToken.getAccessToken());
    }

    /**
     * IdTokenの値が一致していなかった際のエラーコードを返却する。
     *
     * @return エラーコード
     */
    public String getIdTokenErrorMessage() {
        return idTokenVerification.getErrorMessage();
    }

    /**
     * IdTokenの値が一致していなかった際のエラー概要を返却する。
     *
     * @return エラー概要
     */
    public String getIdTokenErrorDescriptionMessage() {
        return idTokenVerification.getErrorDescriptionMessage();
    }

    /**
     * アクセストークンを更新する。
     *
     * @param refreshToken リフレッシュトークンの文字列
     * @param clientId アプリケーションID
     * @param clientSecret シークレット
     * @throws TokenException レスポンスにエラーが含まれているときに発生
     */
    public void refreshToken(String refreshToken, String clientId, String clientSecret)
            throws TokenException {
        RefreshTokenClient refreshTokenClient =
                getRefreshTokenClient(refreshToken, clientId, clientSecret);
        refreshTokenClient.fetch();
        accessToken = refreshTokenClient.getAccessToken();
    }

    /**
     * UserInfoエンドポイントにリクエストする。
     *
     * @param accessTokenString アクセストークンの文字列
     * @throws ApiClientException 定義されていないメソッドを指定したときに発生
     */
    public void requestUserInfo(String accessTokenString) throws ApiClientException {
        UserInfoClient userInfoClient = getUserInfoClient(accessTokenString);
        userInfoClient.fetchResource(USERINFO_ENDPOINT_URL, ApiClient.GET_METHOD);
        userInfoObject = userInfoClient.getUserInfoObject();
    }

    /**
     * UserInfoオブジェクトを取得する。
     *
     * @return UserInfo情報のオブジェクト
     */
    public UserInfoObject getUserInfoObject() {
        return userInfoObject;
    }

    /**
     * レスポンスタイプを設定する。
     *
     * @param responseType レスポンスタイプ（response_type）
     */
    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    /**
     * displayを設定する。
     *
     * @param display テンプレートの種類（display）
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    /**
     * promptを設定する。
     *
     * @param prompt クライアントが強制させたいアクション（prompt）
     */
    public void setPrompt(String[] prompt) {
        this.prompt = StringUtil.implode(prompt);
    }

    /**
     * scopeを設定する。
     *
     * @param scope UserInfo APIから取得できる属性情報の指定（scope）
     */
    public void setScope(String[] scope) {
        this.scope = StringUtil.implode(scope);
    }

    /**
     * nonceを設定する。
     *
     * @param nonce リプレイアタック対策のランダムな文字列を指定してください
     */
    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    /** SSL証明書チェックを無効にする。 */
    public static void disableSSLCheck() {
        YHttpClient.disableSSLCheck();
    }

    /** SSL証明書チェックを有効にする。 */
    public static void enableSSLCheck() {
        YHttpClient.enableSSLCheck();
    }

    protected AuthorizationRequestClient getAuthorizationRequestClient(String clientId) {
        return new AuthorizationRequestClient(AUTHORIZATION_ENDPOINT_URL, clientId);
    }

    protected TokenClient getTokenClient(
            String code, String redirectUri, String clientId, String clientSecret) {
        return new TokenClient(TOKEN_ENDPOINT_URL, code, redirectUri, clientId, clientSecret);
    }

    protected TokenClient getTokenClient(
            String code,
            String redirectUri,
            String clientId,
            String clientSecret,
            String codeVerifier) {
        return new TokenClient(
                TOKEN_ENDPOINT_URL, code, redirectUri, clientId, clientSecret, codeVerifier);
    }

    protected PublicKeysClient getPublicKeysClient() {
        return new PublicKeysClient();
    }

    protected RefreshTokenClient getRefreshTokenClient(
            String refreshToken, String clientId, String clientSecret) {
        return new RefreshTokenClient(TOKEN_ENDPOINT_URL, refreshToken, clientId, clientSecret);
    }

    protected UserInfoClient getUserInfoClient(String accessTokenString) {
        return new UserInfoClient(accessTokenString);
    }

    protected IdTokenVerification getIdTokenVerification() {
        return new IdTokenVerification();
    }

    /**
     * codeChallengeを生成する。
     *
     * @param plainCodeChallenge ハッシュ化前のcodeChallenge
     * @return SHA-256でハッシュ化されたcode challenge
     */
    private String generateCodeChallenge(String plainCodeChallenge)
            throws UnsupportedEncodingException {
        byte[] hashBytes = DigestUtils.sha256(plainCodeChallenge.getBytes("UTF-8"));
        return Base64.encodeBase64URLSafeString(hashBytes);
    }
}
