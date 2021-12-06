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

package jp.co.yahoo.yconnect.core;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.DataFormatException;
import jp.co.yahoo.yconnect.YConnectClient;
import jp.co.yahoo.yconnect.core.api.ApiClientException;
import jp.co.yahoo.yconnect.core.http.HttpHeaders;
import jp.co.yahoo.yconnect.core.http.HttpParameters;
import jp.co.yahoo.yconnect.core.http.YHttpClient;
import jp.co.yahoo.yconnect.core.oauth2.*;
import jp.co.yahoo.yconnect.core.oidc.*;
import jp.co.yahoo.yconnect.core.util.StringUtil;
import jp.co.yahoo.yconnect.util.IdTokenGenerator;
import org.junit.Test;

public class YConnectClientTest {

    private static final String AUTHORIZATION_ENDPOINT_URL =
            "https://auth.login.yahoo.co.jp/yconnect/v2/authorization";

    private static final String TOKEN_ENDPOINT_URL =
            "https://auth.login.yahoo.co.jp/yconnect/v2/token";

    private static final String ISSUER = "https://auth.login.yahoo.co.jp/yconnect/v2";

    private final String responseType = OAuth2ResponseType.CODE;
    private final String clientId = "sample_client_id";
    private final String clientSecret = "clientSecret";
    private final String redirectUri = "https://example.co.jp/callback";
    private final String display = OIDCDisplay.DEFAULT;
    private final String[] prompt = new String[] {OIDCPrompt.DEFAULT};
    private final String[] scope = new String[] {OIDCScope.OPENID, OIDCScope.EMAIL};
    private final String state = "sampleState";
    private final String nonce = "sampleNonce";
    private final String plainCodeChallenge = "samplePlainCodeChallenge";

    @Test
    public void testGenerateAuthorizationUriInitializedThreeParams() throws Exception {
        AuthorizationRequestClient client =
                new AuthorizationRequestClient(AUTHORIZATION_ENDPOINT_URL, clientId);

        YConnectClient explicit =
                new YConnectClient() {
                    @Override
                    protected AuthorizationRequestClient getAuthorizationRequestClient(
                            String clientId) {
                        return client;
                    }
                };

        explicit.init(clientId, redirectUri, state);
        explicit.setResponseType(responseType);
        explicit.setPrompt(prompt);
        explicit.setDisplay(display);

        // AuthorizationRequestClient#generateAuthorizationUriは検証済みのためここでは検証しない
        explicit.generateAuthorizationUri();

        Field parametersField = AuthorizationRequestClient.class.getDeclaredField("parameters");
        parametersField.setAccessible(true);
        HttpParameters httpParameters = (HttpParameters) parametersField.get(client);

        assertEquals(redirectUri, httpParameters.get("redirect_uri"));
        assertEquals(responseType, httpParameters.get("response_type"));
        assertEquals(display, httpParameters.get("display"));
        assertEquals(StringUtil.implode(prompt), httpParameters.get("prompt"));
    }

    @Test
    public void testGenerateAuthorizationUriInitializedEightParams() throws Exception {
        AuthorizationRequestClient client =
                new AuthorizationRequestClient(AUTHORIZATION_ENDPOINT_URL, clientId);

        YConnectClient explicit =
                new YConnectClient() {
                    @Override
                    protected AuthorizationRequestClient getAuthorizationRequestClient(
                            String clientId) {
                        return client;
                    }
                };

        explicit.init(clientId, redirectUri, state, responseType, display, prompt, scope, nonce);

        // AuthorizationRequestClient#generateAuthorizationUriは検証済みのためここでは検証しない
        explicit.generateAuthorizationUri();

        Field parametersField = AuthorizationRequestClient.class.getDeclaredField("parameters");
        parametersField.setAccessible(true);
        HttpParameters httpParameters = (HttpParameters) parametersField.get(client);

        assertEquals(redirectUri, httpParameters.get("redirect_uri"));
        assertEquals(responseType, httpParameters.get("response_type"));
        assertEquals(display, httpParameters.get("display"));
        assertEquals(StringUtil.implode(prompt), httpParameters.get("prompt"));
        assertEquals(StringUtil.implode(scope), httpParameters.get("scope"));
        assertEquals(nonce, httpParameters.get("nonce"));
    }

    @Test
    public void testGenerateAuthorizationUriInitializedTenParams() throws Exception {
        AuthorizationRequestClient client =
                new AuthorizationRequestClient(AUTHORIZATION_ENDPOINT_URL, clientId);

        YConnectClient explicit =
                new YConnectClient() {
                    @Override
                    protected AuthorizationRequestClient getAuthorizationRequestClient(
                            String clientId) {
                        return client;
                    }
                };

        Long maxAge = 3600L;
        explicit.init(
                clientId,
                redirectUri,
                state,
                responseType,
                display,
                prompt,
                scope,
                nonce,
                maxAge,
                plainCodeChallenge);
        explicit.setResponseType(responseType);
        explicit.setPrompt(prompt);
        explicit.setDisplay(display);

        // AuthorizationRequestClient#generateAuthorizationUriは検証済みのためここでは検証しない
        explicit.generateAuthorizationUri();

        Field parametersField = AuthorizationRequestClient.class.getDeclaredField("parameters");
        parametersField.setAccessible(true);
        HttpParameters httpParameters = (HttpParameters) parametersField.get(client);

        Method generateCodeChallengeMethod =
                YConnectClient.class.getDeclaredMethod("generateCodeChallenge", String.class);
        generateCodeChallengeMethod.setAccessible(true);

        assertEquals(redirectUri, httpParameters.get("redirect_uri"));
        assertEquals(responseType, httpParameters.get("response_type"));
        assertEquals(display, httpParameters.get("display"));
        assertEquals(StringUtil.implode(prompt), httpParameters.get("prompt"));
        assertEquals(StringUtil.implode(scope), httpParameters.get("scope"));
        assertEquals(nonce, httpParameters.get("nonce"));
        assertEquals(maxAge.toString(), httpParameters.get("max_age"));
        assertEquals(
                generateCodeChallengeMethod.invoke(explicit, plainCodeChallenge),
                httpParameters.get("code_challenge"));
        String codeChallengeMethod = "S256";
        assertEquals(codeChallengeMethod, httpParameters.get("code_challenge_method"));
    }

    @Test
    public void testHasAuthorizationCodeByQueryString() throws AuthorizationException {
        YConnectClient explicit = new YConnectClient();

        assertTrue(explicit.hasAuthorizationCode("code=sample_code&state=" + state));
    }

    @Test
    public void testHasAuthorizationCodeByURI() throws AuthorizationException, URISyntaxException {
        YConnectClient explicit = new YConnectClient();

        URI uri = new URI("https://example.co.jp/callback?code=sample_code&state=" + state);
        assertTrue(explicit.hasAuthorizationCode(uri));
    }

    @Test
    public void testHasAuthorizationCodeInputsEmpty() throws AuthorizationException {
        YConnectClient explicit = new YConnectClient();

        assertFalse(explicit.hasAuthorizationCode(""));
    }

    @Test
    public void testHasAuthorizationCodeInputsNull() throws AuthorizationException {
        YConnectClient explicit = new YConnectClient();

        assertFalse(explicit.hasAuthorizationCode((String) null));
        assertFalse(explicit.hasAuthorizationCode((URI) null));
    }

    @Test(expected = AuthorizationException.class)
    public void testHasAuthorizationCodeThrowsAuthorizationException()
            throws AuthorizationException {
        YConnectClient explicit = new YConnectClient();

        explicit.hasAuthorizationCode(
                "error=sample_error&error_description=sample_error_description");
    }

    @Test
    public void testGetAuthorizationCode() throws AuthorizationException {
        YConnectClient explicit = new YConnectClient();
        explicit.init(clientId, redirectUri, state);

        String code = "sample_code";
        explicit.hasAuthorizationCode("code=" + code + "&state=" + state);

        assertEquals(code, explicit.getAuthorizationCode(state));
    }

    @Test
    public void testGetAuthorizationCodeReturnsNull() throws AuthorizationException {
        YConnectClient explicit = new YConnectClient();
        explicit.init(clientId, redirectUri, state);

        explicit.hasAuthorizationCode("state=" + state);

        assertNull(explicit.getAuthorizationCode(state));
    }

    @Test(expected = AuthorizationException.class)
    public void testGetAuthorizationCodeWhenStateInvalid() throws AuthorizationException {
        YConnectClient explicit = new YConnectClient();
        explicit.init(clientId, redirectUri, state);

        String code = "sample_code";
        explicit.hasAuthorizationCode("code=" + code + "&state=" + state);

        explicit.getAuthorizationCode("invalid_state");
    }

    @Test
    public void testRequestTokenGivenFourParameters() throws Exception {
        String code = "sample_code";
        String accessTokenString = "sample_access_token";
        long expiresIn = 1635638400;
        String refreshToken = "sample_refresh_token";
        String idTokenString = "sample_id_token";

        YHttpClient httpClient =
                new YHttpClient() {
                    @Override
                    public void requestPost(
                            String endpointUrl,
                            HttpParameters parameters,
                            HttpHeaders requestHeaders) {
                        assertEquals(
                                OAuth2GrantType.AUTHORIZATION_CODE, parameters.get("grant_type"));
                        assertEquals(code, parameters.get("code"));
                        assertEquals(redirectUri, parameters.get("redirect_uri"));
                    }

                    @Override
                    public HttpHeaders getResponseHeaders() {
                        return new HttpHeaders();
                    }

                    @Override
                    public String getResponseBody() {
                        return "{\"access_token\":\""
                                + accessTokenString
                                + "\", \"expires_in\":"
                                + expiresIn
                                + ", \"refresh_token\":\""
                                + refreshToken
                                + "\", \"id_token\":\""
                                + idTokenString
                                + "\"}";
                    }

                    @Override
                    public int getStatusCode() {
                        return 200;
                    }
                };

        TokenClient tokenClient =
                new TokenClient(TOKEN_ENDPOINT_URL, code, redirectUri, clientId, clientSecret) {

                    @Override
                    public BearerToken getAccessToken() {
                        return new BearerToken(accessTokenString, expiresIn, refreshToken);
                    }

                    @Override
                    public String getIdToken() {
                        return idTokenString;
                    }

                    @Override
                    protected YHttpClient getYHttpClient() {
                        return httpClient;
                    }
                };

        YConnectClient explicit =
                new YConnectClient() {
                    @Override
                    protected TokenClient getTokenClient(
                            String code, String redirectUri, String clientId, String clientSecret) {
                        return tokenClient;
                    }
                };

        explicit.requestToken(code, redirectUri, clientId, clientSecret);

        assertEquals(accessTokenString, explicit.getAccessToken());
        assertEquals(expiresIn, explicit.getAccessTokenExpiration());
        assertEquals(refreshToken, explicit.getRefreshToken());
        assertEquals(idTokenString, explicit.getIdToken());
    }

    @Test
    public void testRequestTokenGivenFiveParameters() throws Exception {
        String code = "sample_code";
        String accessTokenString = "sample_access_token";
        long expiresIn = 1635638400;
        String refreshToken = "sample_refresh_token";
        String idTokenString = "sample_id_token";

        YHttpClient httpClient =
                new YHttpClient() {
                    @Override
                    public void requestPost(
                            String endpointUrl,
                            HttpParameters parameters,
                            HttpHeaders requestHeaders) {
                        assertEquals(
                                OAuth2GrantType.AUTHORIZATION_CODE, parameters.get("grant_type"));
                        assertEquals(code, parameters.get("code"));
                        assertEquals(redirectUri, parameters.get("redirect_uri"));
                        assertEquals(plainCodeChallenge, parameters.get("code_verifier"));
                    }

                    @Override
                    public HttpHeaders getResponseHeaders() {
                        return new HttpHeaders();
                    }

                    @Override
                    public String getResponseBody() {
                        return "{\"access_token\":\""
                                + accessTokenString
                                + "\", \"expires_in\":"
                                + expiresIn
                                + ", \"refresh_token\":\""
                                + refreshToken
                                + "\", \"id_token\":\""
                                + idTokenString
                                + "\"}";
                    }

                    @Override
                    public int getStatusCode() {
                        return 200;
                    }
                };

        TokenClient tokenClient =
                new TokenClient(
                        TOKEN_ENDPOINT_URL,
                        code,
                        redirectUri,
                        clientId,
                        clientSecret,
                        plainCodeChallenge) {

                    @Override
                    public BearerToken getAccessToken() {
                        return new BearerToken(accessTokenString, expiresIn, refreshToken);
                    }

                    @Override
                    public String getIdToken() {
                        return idTokenString;
                    }

                    @Override
                    protected YHttpClient getYHttpClient() {
                        return httpClient;
                    }
                };

        YConnectClient explicit =
                new YConnectClient() {
                    @Override
                    protected TokenClient getTokenClient(
                            String code,
                            String redirectUri,
                            String clientId,
                            String clientSecret,
                            String plainCodeChallenge) {
                        return tokenClient;
                    }
                };

        explicit.requestToken(code, redirectUri, clientId, clientSecret, plainCodeChallenge);

        assertEquals(accessTokenString, explicit.getAccessToken());
        assertEquals(expiresIn, explicit.getAccessTokenExpiration());
        assertEquals(refreshToken, explicit.getRefreshToken());
        assertEquals(idTokenString, explicit.getIdToken());
    }

    @Test
    public void testRequestTokenThrowsTokenException() {
        String error = "sample_error";
        String errorDescription = "sample_error_description";
        Integer errorCode = 1000;

        TokenClient tokenClient =
                new TokenClient(
                        TOKEN_ENDPOINT_URL,
                        "sample_code",
                        redirectUri,
                        clientId,
                        clientSecret,
                        plainCodeChallenge) {

                    @Override
                    public void fetch() throws TokenException {
                        throw new TokenException(error, errorDescription, errorCode);
                    }
                };

        YConnectClient explicit =
                new YConnectClient() {
                    @Override
                    protected TokenClient getTokenClient(
                            String code,
                            String redirectUri,
                            String clientId,
                            String clientSecret,
                            String plainCodeChallenge) {
                        return tokenClient;
                    }
                };

        TokenException ex =
                assertThrows(
                        TokenException.class,
                        () ->
                                explicit.requestToken(
                                        "sample_code",
                                        redirectUri,
                                        clientId,
                                        clientSecret,
                                        plainCodeChallenge));
        assertEquals(error, ex.getError());
        assertEquals(errorDescription, ex.getErrorDescription());
        assertEquals(errorCode, ex.getErrorCode());
    }

    @Test
    public void testDecodeIdToken() throws Exception {
        IdTokenObject expected = getSampleIdTokenObject();

        String idTokenString = new IdTokenGenerator(expected).getIdTokenString();

        YConnectClient explicit = new YConnectClient();
        IdTokenObject result = explicit.decodeIdToken(idTokenString);

        assertEquals(expected.getType(), result.getType());
        assertEquals(expected.getAlgorithm(), result.getAlgorithm());
        assertEquals(expected.getKid(), result.getKid());
        assertEquals(expected.getIss(), result.getIss());
        assertEquals(expected.getSub(), result.getSub());
        assertEquals(expected.getAud().get(0), result.getAud().get(0));
        assertEquals(expected.getNonce(), result.getNonce());
        assertEquals(expected.getAtHash(), result.getAtHash());
        assertEquals(expected.getExp(), result.getExp());
        assertEquals(expected.getIat(), result.getIat());
        assertEquals(expected.getAuthTime(), result.getAuthTime());
    }

    @Test(expected = DataFormatException.class)
    public void testDecodeIdTokenThrowsDataFormatException() throws Exception {
        String idTokenString = new IdTokenGenerator(getSampleIdTokenObject()).getIdTokenString();

        YConnectClient explicit = new YConnectClient();
        explicit.decodeIdToken(
                String.join(".", idTokenString.split("\\.")[0], idTokenString.split("\\.")[1]));
    }

    @Test
    public void testVerifyIdTokenReturnsTrue() throws Exception {
        IdTokenGenerator generator = new IdTokenGenerator(getSampleIdTokenObject());
        String idTokenString = generator.getIdTokenString();

        PublicKeysClient publicKeysClient =
                new PublicKeysClient() {
                    @Override
                    public void fetchResource(String endpoint) {}

                    @Override
                    public PublicKeysObject getPublicKeysObject() {
                        return new PublicKeysObject();
                    }
                };

        IdTokenVerification idTokenVerification =
                new IdTokenVerification() {
                    @Override
                    public boolean check(
                            String iss,
                            String nonce,
                            String clientId,
                            IdTokenObject idTokenObject,
                            PublicKeysObject publicKeysObject,
                            String idTokenString,
                            String accessToken) {
                        return true;
                    }
                };

        YConnectClient explicit =
                new YConnectClient() {
                    @Override
                    protected PublicKeysClient getPublicKeysClient() {
                        return publicKeysClient;
                    }

                    @Override
                    protected IdTokenVerification getIdTokenVerification() {
                        return idTokenVerification;
                    }
                };

        Field accessTokenField = YConnectClient.class.getDeclaredField("accessToken");
        accessTokenField.setAccessible(true);
        accessTokenField.set(
                explicit, new BearerToken("accessTokenSample", 1635638400, "refreshTokenSample"));

        explicit.init(clientId, redirectUri, state, responseType, display, prompt, scope, nonce);
        assertTrue(explicit.verifyIdToken(nonce, clientId, idTokenString));
    }

    @Test
    public void testVerifyIdTokenReturnsFalse() throws Exception {
        IdTokenGenerator generator = new IdTokenGenerator(getSampleIdTokenObject());
        String idTokenString = generator.getIdTokenString();

        PublicKeysClient publicKeysClient =
                new PublicKeysClient() {
                    @Override
                    public void fetchResource(String endpoint) {}

                    @Override
                    public PublicKeysObject getPublicKeysObject() {
                        return new PublicKeysObject();
                    }
                };

        IdTokenVerification idTokenVerification =
                new IdTokenVerification() {
                    @Override
                    public boolean check(
                            String iss,
                            String nonce,
                            String clientId,
                            IdTokenObject idTokenObject,
                            PublicKeysObject publicKeysObject,
                            String idTokenString,
                            String accessToken) {
                        return false;
                    }
                };

        YConnectClient explicit =
                new YConnectClient() {
                    @Override
                    protected PublicKeysClient getPublicKeysClient() {
                        return publicKeysClient;
                    }

                    @Override
                    protected IdTokenVerification getIdTokenVerification() {
                        return idTokenVerification;
                    }
                };

        Field accessTokenField = YConnectClient.class.getDeclaredField("accessToken");
        accessTokenField.setAccessible(true);
        accessTokenField.set(
                explicit, new BearerToken("accessTokenSample", 1635638400, "refreshTokenSample"));

        explicit.init(clientId, redirectUri, state, responseType, display, prompt, scope, nonce);
        assertFalse(explicit.verifyIdToken(nonce, clientId, idTokenString));
    }

    @Test(expected = DataFormatException.class)
    public void testVerifyIdTokenThrowsDataFormatException() throws Exception {
        IdTokenGenerator generator = new IdTokenGenerator(getSampleIdTokenObject());
        String idTokenString = generator.getIdTokenString();
        idTokenString =
                String.join(".", idTokenString.split("\\.")[0], idTokenString.split("\\.")[1]);

        PublicKeysClient publicKeysClient =
                new PublicKeysClient() {
                    @Override
                    public void fetchResource(String endpoint) {}

                    @Override
                    public PublicKeysObject getPublicKeysObject() {
                        return new PublicKeysObject();
                    }
                };

        IdTokenVerification idTokenVerification =
                new IdTokenVerification() {
                    @Override
                    public boolean check(
                            String iss,
                            String nonce,
                            String clientId,
                            IdTokenObject idTokenObject,
                            PublicKeysObject publicKeysObject,
                            String idTokenString,
                            String accessToken) {
                        return false;
                    }
                };

        YConnectClient explicit =
                new YConnectClient() {
                    @Override
                    protected PublicKeysClient getPublicKeysClient() {
                        return publicKeysClient;
                    }

                    @Override
                    protected IdTokenVerification getIdTokenVerification() {
                        return idTokenVerification;
                    }
                };

        explicit.init(clientId, redirectUri, state, responseType, display, prompt, scope, nonce);
        explicit.verifyIdToken(nonce, clientId, idTokenString);
    }

    @Test
    public void testVerifyIdTokenThrowsApiClientException() throws Exception {
        String error = "error_sample";
        String errorDescription = "error_description_sample";
        String idTokenString = new IdTokenGenerator(getSampleIdTokenObject()).getIdTokenString();

        PublicKeysClient publicKeysClient =
                new PublicKeysClient() {
                    @Override
                    public void fetchResource(String endpoint) throws ApiClientException {
                        throw new ApiClientException(error, errorDescription);
                    }
                };

        YConnectClient explicit =
                new YConnectClient() {
                    @Override
                    protected PublicKeysClient getPublicKeysClient() {
                        return publicKeysClient;
                    }
                };

        explicit.init(clientId, redirectUri, state, responseType, display, prompt, scope, nonce);
        ApiClientException ex =
                assertThrows(
                        ApiClientException.class,
                        () -> explicit.verifyIdToken(nonce, clientId, idTokenString));

        assertEquals(error, ex.getError());
        assertEquals(errorDescription, ex.getErrorDescription());
    }

    @Test
    public void testRefreshToken() throws Exception {
        String accessTokenString = "sample_access_token";
        long expiresIn = 1635638400;
        String refreshToken = "sample_refresh_token";

        YHttpClient httpClient =
                new YHttpClient() {
                    @Override
                    public void requestPost(
                            String endpointUrl,
                            HttpParameters parameters,
                            HttpHeaders requestHeaders) {
                        assertEquals(OAuth2GrantType.REFRESH_TOKEN, parameters.get("grant_type"));
                        assertEquals(refreshToken, parameters.get("refresh_token"));
                    }

                    @Override
                    public HttpHeaders getResponseHeaders() {
                        return new HttpHeaders();
                    }

                    @Override
                    public String getResponseBody() {
                        return "{\"access_token\":\""
                                + accessTokenString
                                + "\", \"expires_in\":"
                                + expiresIn
                                + "}";
                    }

                    @Override
                    public int getStatusCode() {
                        return 200;
                    }
                };

        RefreshTokenClient refreshTokenClient =
                new RefreshTokenClient(TOKEN_ENDPOINT_URL, refreshToken, clientId, clientSecret) {

                    @Override
                    public BearerToken getAccessToken() {
                        return new BearerToken(accessTokenString, expiresIn);
                    }

                    @Override
                    protected YHttpClient getYHttpClient() {
                        return httpClient;
                    }
                };

        YConnectClient explicit =
                new YConnectClient() {
                    @Override
                    protected RefreshTokenClient getRefreshTokenClient(
                            String refreshToken, String clientId, String clientSecret) {
                        return refreshTokenClient;
                    }
                };

        explicit.refreshToken(refreshToken, clientId, clientSecret);

        assertEquals(accessTokenString, explicit.getAccessToken());
        assertEquals(expiresIn, explicit.getAccessTokenExpiration());
    }

    @Test
    public void testRefreshTokenThrowsTokenException() {
        String error = "sample_error";
        String errorDescription = "sample_error_description";
        Integer errorCode = 1000;

        RefreshTokenClient refreshTokenClient =
                new RefreshTokenClient(
                        TOKEN_ENDPOINT_URL, "refreshTokenSample", clientId, clientSecret) {

                    @Override
                    public void fetch() throws TokenException {
                        throw new TokenException(error, errorDescription, errorCode);
                    }
                };

        YConnectClient explicit =
                new YConnectClient() {
                    @Override
                    protected RefreshTokenClient getRefreshTokenClient(
                            String refreshToken, String clientId, String clientSecret) {
                        return refreshTokenClient;
                    }
                };

        TokenException ex =
                assertThrows(
                        TokenException.class,
                        () -> explicit.refreshToken("refreshTokenSample", clientId, clientSecret));

        assertEquals(error, ex.getError());
        assertEquals(errorDescription, ex.getErrorDescription());
        assertEquals(errorCode, ex.getErrorCode());
    }

    @Test
    public void testRequestUserInfo() throws Exception {
        String accessTokenString = "sample_access_token";

        UserInfoObject userInfoObject = new UserInfoObject();

        UserInfoClient userInfoClient =
                new UserInfoClient(accessTokenString) {

                    @Override
                    public void fetchResource(String endpoint, String method) {}

                    @Override
                    public UserInfoObject getUserInfoObject() {
                        return userInfoObject;
                    }
                };

        YConnectClient explicit =
                new YConnectClient() {
                    @Override
                    protected UserInfoClient getUserInfoClient(String accessTokenString) {
                        return userInfoClient;
                    }
                };

        explicit.requestUserInfo(accessTokenString);

        assertEquals(userInfoObject, explicit.getUserInfoObject());
    }

    @Test(expected = ApiClientException.class)
    public void testRequestUserInfoThrowsApiClientException() throws Exception {
        UserInfoClient userInfoClient =
                new UserInfoClient("accessTokenSample") {

                    @Override
                    public void fetchResource(String endpoint, String method)
                            throws ApiClientException {
                        throw new ApiClientException();
                    }
                };

        YConnectClient explicit =
                new YConnectClient() {
                    @Override
                    protected UserInfoClient getUserInfoClient(String accessTokenString) {
                        return userInfoClient;
                    }
                };

        explicit.requestUserInfo("accessTokenSample");
    }

    private IdTokenObject getSampleIdTokenObject() {
        IdTokenObject idTokenObject = new IdTokenObject();
        idTokenObject.setType("JWT");
        idTokenObject.setAlgorithm("RS256");
        idTokenObject.setKid("sample_kid");
        idTokenObject.setIss(ISSUER);
        idTokenObject.setSub("sample_ppid");
        idTokenObject.setAud(new ArrayList<>(Collections.singletonList(clientId)));
        idTokenObject.setNonce(nonce);
        idTokenObject.setAtHash("sample_at_hash");
        idTokenObject.setExp(1635638400);
        idTokenObject.setIat(1635638400);
        idTokenObject.setAuthTime(1635638400);

        return idTokenObject;
    }
}
