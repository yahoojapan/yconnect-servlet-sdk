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

package jp.co.yahoo.sample;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.yahoo.yconnect.YConnectClient;
import jp.co.yahoo.yconnect.core.api.ApiClientException;
import jp.co.yahoo.yconnect.core.oauth2.AuthorizationException;
import jp.co.yahoo.yconnect.core.oauth2.OAuth2ResponseType;
import jp.co.yahoo.yconnect.core.oauth2.TokenException;
import jp.co.yahoo.yconnect.core.oidc.IdTokenObject;
import jp.co.yahoo.yconnect.core.oidc.OIDCDisplay;
import jp.co.yahoo.yconnect.core.oidc.OIDCPrompt;
import jp.co.yahoo.yconnect.core.oidc.OIDCScope;
import jp.co.yahoo.yconnect.core.oidc.UserInfoObject;
import jp.co.yahoo.yconnect.core.util.YConnectLogger;

/**
 * Servlet implementation class YConnectServlet
 *
 * @author Copyright (C) 2016 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
public class YConnectServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  // アプリケーションID, シークレット
  private final static String clientId = "YOUR_APPLICATION_ID";
  private final static String clientSecret = "YOUR_APPLICATION_SECRET";

  // コールバックURL
  // (アプリケーションID発行時に登録したURL)
  private static final String redirectUri =
      "http://localhost:8080/YConnectServletSDK/YConnectServlet";

  public YConnectServlet() {
    super();
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // ログ出力レベル設定ファイルが配置されている任意のパスを指定してください
    YConnectLogger.setFilePath("./yconnect_log_conf.xml");

    response.setContentType("text/html; charset=UTF-8");

    // state, nonceにランダムな値を初期化
    String state = "5Ye65oi744KKT0vjgafjgZnjgojjgIHlhYjovKnjg4M"; // リクエストとコールバック間の検証用のランダムな文字列を指定してください
    String nonce = "SUTljqjjga8uLi7jgrrjg4Plj4vjgaDjgoc"; // リプレイアタック対策のランダムな文字列を指定してください
    String plainCodeChallenge = "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM._~"; //認可コード横取り攻撃対策文字列を指定してください
    // YConnectインスタンス生成
    YConnectClient yconnect = new YConnectClient();

    // SSL証明書チェック無効 ※通信テスト用のメソッドなのでサービス時にはコメントアウト推奨
    // YConnectClient.disableSSLCheck();

    try {
      // コールバックURLから各パラメーターを抽出
      String fullUri = request.getRequestURL().toString() + "?" + request.getQueryString();
      URI requestUri = new URI(fullUri);

      if (yconnect.hasAuthorizationCode(requestUri)) {
        /*********************************************************
         * Parse the Callback URI and Save the Access Token.
         *********************************************************/

        StringBuffer sb = new StringBuffer();

        // 認可コードを取得
        String code = yconnect.getAuthorizationCode(state);

        sb.append("<h1>Authorization Request</h1>");
        sb.append("Authorization Code: " + code + "<br/><br/>");

        /***********************************************
         * Request Access Token adn Refresh Token.
         ***********************************************/

        // Tokenエンドポイントにリクエスト
        yconnect.requestToken(code, clientId, clientSecret, redirectUri, plainCodeChallenge);
        // アクセストークン, リフレッシュトークン, IDトークンを取得
        String accessTokenString = yconnect.getAccessToken();
        long expiration = yconnect.getAccessTokenExpiration();
        String refreshToken = yconnect.getRefreshToken();
        String idTokenString = yconnect.getIdToken();

        sb.append("<h1>Access Token Request</h1>");
        sb.append("Access Token: " + accessTokenString + "<br/><br/>");
        sb.append("Expiration: " + Long.toString(expiration) + "<br/><br/>");
        sb.append("Refresh Token: " + refreshToken + "<br/><br/>");

        /************************
         * Decode ID Token.
         ************************/

        // IDトークンの検証
        if (yconnect.verifyIdToken(nonce, clientId, idTokenString)) {
          // IDトークンの復号
          IdTokenObject idTokenObject = yconnect.decodeIdToken(idTokenString);
          sb.append("<h1>ID Token</h1>");
          sb.append("ID Token: " + idTokenObject.toString() + "<br/><br/>");
        } else {
          // 検証に失敗したのでエラー文を出力
          sb.append("<h1>ID Token</h1>");
          sb.append("ID Token error: " + yconnect.getIdTokenErrorMessage() + "<br/><br/>");
          sb.append("ID Token error description: " + yconnect.getIdTokenErrorDescriptionMessage()
              + "<br/><br/>");
        }

        /*************************
         * Request UserInfo.
         *************************/

        // UserInfoエンドポイントへリクエスト
        yconnect.requestUserInfo(accessTokenString);
        // UserInfo情報を取得
        UserInfoObject userInfoObject = yconnect.getUserInfoObject();
        sb.append("<h1>UserInfo Request</h1>");
        sb.append("UserInfo: <pre>" + userInfoObject + "</pre><br/>");
        sb.append("sub: <pre>" + userInfoObject.getAdditionalValue("sub") + "</pre><br/>");
        // JsonObjectから任意のkeyを指定して値を取得する方法
        // String userId = userInfoObject.getJsonObject().getString("user_id");
        /*************************************************
         * Request Access Token Using Refresh Token.
         *************************************************/

        // Tokenエンドポイントにリクエストしてアクセストークンを更新
        yconnect.refreshToken(refreshToken, clientId, clientSecret);
        accessTokenString = yconnect.getAccessToken();
        expiration = yconnect.getAccessTokenExpiration();

        sb.append("<h1>Refresh Token</h1>");
        sb.append("Access Token: " + accessTokenString + "<br/><br/>");
        sb.append("Expiration: " + Long.toString(expiration) + "<br/><br/>");

        PrintWriter out = response.getWriter();
        out.println(new String(sb));
        out.close();

      } else {

        /****************************************************************
         * Request Authorization Endpoint for getting Access Token.
         ****************************************************************/

        // 各パラメーター初期化
        String responseType = OAuth2ResponseType.CODE;
        String display = OIDCDisplay.DEFAULT;
        String[] prompt = {OIDCPrompt.DEFAULT};
        String[] scope = {OIDCScope.OPENID, OIDCScope.PROFILE, OIDCScope.EMAIL, OIDCScope.ADDRESS};

        // 各パラメーターを設定（推奨）
        yconnect.init(clientId, redirectUri, state, responseType, display, prompt, scope, nonce, 3600L, plainCodeChallenge);
        // (Option) v2.1.2時と同様の設定も可能です
        // yconnect.init(clientId, redirectUri, state, responseType, display, prompt, scope, nonce);
        URI uri = yconnect.generateAuthorizationUri();

        // Authorizationエンドポイントにリダイレクト(同意画面を表示)
        response.sendRedirect(uri.toString());
      }

    } catch (ApiClientException ace) {

      // エラーレスポンスが"Invalid_Token"であるかチェック
      if (ace.isInvalidToken()) {

        /*****************************
         * Refresh Access Token.
         *****************************/

        try {

          // 保存していたリフレッシュトークンを指定してください
          String refreshToken = "STORED_REFRESH_TOKEN";

          // Tokenエンドポイントにリクエストしてアクセストークンを更新
          yconnect.refreshToken(refreshToken, clientId, clientSecret);
          String accessTokenString = yconnect.getAccessToken();
          long expiration = yconnect.getAccessTokenExpiration();

          StringBuffer sb = new StringBuffer();
          sb.append("<h1>Refresh Token</h1>");
          sb.append("Access Token: " + accessTokenString + "<br/><br/>");
          sb.append("Expiration: " + Long.toString(expiration) + "<br/><br/>");

          PrintWriter out = response.getWriter();
          out.println(new String(sb));
          out.close();

        } catch (TokenException te) {

          // リフレッシュトークンの有効期限切れチェック
          if (te.isInvalidGrant()) {
            // はじめのAuthorizationエンドポイントリクエストからやり直してください
          }

          te.printStackTrace();
        } catch (Exception e) {
          e.printStackTrace();
        }

      }
      ace.printStackTrace();
    } catch (AuthorizationException e) {
      e.printStackTrace();
    } catch (TokenException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
