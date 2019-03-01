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

package jp.co.yahoo.yconnect.core.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;

import jp.co.yahoo.yconnect.core.util.YConnectLogger;

/**
 * HTTP Client Class for YConnect
 *
 * @author Copyright (C) 2016 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
public class YHttpClient {

  /**
   * {@link HttpURLConnection}インスタンス
   */
  private HttpClient httpClient = null;

  /**
   * レスポンスのHTTPステータスコード
   */
  private int responseCode = 0;

  /**
   * レスポンスのHTTP応答メッセージ
   */
  private String responseMessage = "";

  /**
   * レスポンスヘッダ<br>
   * フィールド名と値の{@link HashMap}
   */
  private HttpHeaders responseHeaders = new HttpHeaders();

  /**
   * レスポンスボディ
   */
  private String responseBody = "";

  /**
   * SSL証明書チェック
   */
  private static boolean checkSSL = true; // default true

  /**
   * HttpClientのコンストラクタです。
   */
  public YHttpClient() {
    // 初期化
    responseCode = 0;
    responseMessage = "";
    responseBody = "";
    responseHeaders = new HttpHeaders();
  }

  /**
   * ステータスコードを返します。
   * 
   * @return ステータスコード
   */
  public int getStatusCode() {
    return responseCode;
  }

  /**
   * ステータスメッセージを返します。
   * 
   * @return ステータスメッセージ
   */
  public String getStatusMessage() {
    return responseMessage;
  }

  /**
   * レスポンスヘッダを返します。
   * 
   * @return レスポンスヘッダ
   */
  public HttpHeaders getResponseHeaders() {
    return responseHeaders;
  }

  /**
   * レスポンスボディを返します。
   * 
   * @return レスポンスボディ
   */
  public String getResponseBody() {
    return responseBody;
  }

  /**
   * GETメソッドによるHTTPリクエストをします。
   * 
   * @param urlString URL文字列
   * @param parameters {@link HttpParameters} の {@link ArrayList}
   * @param requestHeaders リクエストヘッダの {@link HashMap}
   * @throws ProtocolException
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws KeyManagementException
   */
  public void requestGet(String urlString, HttpParameters parameters, HttpHeaders requestHeaders) {

    // リクエストパラメータ設定
    if (parameters != null) {
      String queryString = parameters.toQueryString();
      urlString += "?" + queryString;
    }

    YConnectLogger.debug(this, "URL: " + urlString);

    // リクエストヘッダ設定
    HttpGet method = new HttpGet(urlString);
    if (requestHeaders != null) {
      Iterator<String> itReqHeaders = requestHeaders.keySet().iterator();
      while (itReqHeaders.hasNext()) {
        String key = (String) itReqHeaders.next();
        String value = requestHeaders.get(key);
        method.setHeader(key, value);
        YConnectLogger.debug(this, key + ": " + value);
      }
    }

    if (urlString.startsWith("https")) {
      if (!checkSSL) {
        YConnectLogger.debug(this, "HTTPS ignore SSL Certification");
        httpClient = new DefaultHttpClient();
        ignoreSSLCertification(httpClient);
      } else {
        SSLContext sslContext;
        try {
          sslContext = SSLContext.getInstance("TLSv1.2");
          sslContext.init(null, null, null);
          SSLSocketFactory sf = new SSLSocketFactory(sslContext);
          Scheme httpsScheme = new Scheme("https", 443, sf);
          SchemeRegistry schemeRegistry = new SchemeRegistry();
          schemeRegistry.register(httpsScheme);
          ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
          httpClient = new DefaultHttpClient(cm);
        } catch (NoSuchAlgorithmException e1) {
          e1.printStackTrace();
        } catch (KeyManagementException e1) {
          e1.printStackTrace();
        }
      }
    }

    try {

      HttpResponse httpResponse = httpClient.execute(method);

      // レスポンスコード、メッセージ取得
      responseCode = httpResponse.getStatusLine().getStatusCode();
      responseMessage = httpResponse.getStatusLine().getReasonPhrase();

      YConnectLogger.debug(this, "responseCode: " + responseCode);
      YConnectLogger.debug(this, "responseMessage: " + responseMessage);

      // レスポンスヘッダ取得
      Header[] headers = httpResponse.getAllHeaders();
      for (Header header : headers) {
        responseHeaders.put(header.getName(), header.getValue());
      }

      YConnectLogger.debug(this, "responseHeaders: " + responseHeaders);

      // レスポンスボディ取得
      HttpEntity httpEntity = httpResponse.getEntity();
      responseBody = EntityUtils.toString(httpEntity);
      httpEntity.getContent().close();

      YConnectLogger.debug(this, "responseBody: " + responseBody);

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      method.releaseConnection();
      httpClient.getConnectionManager().shutdown();
    }

  }

  /**
   * POSTメソッドによるHTTPリクエストをします。
   * 
   * @param urlString URL文字列
   * @param parameters {@link HttpParameters} の {@link ArrayList}
   * @param requestHeaders リクエストヘッダの {@link HashMap}
   * @throws ProtocolException
   * @throws IOException
   */
  public void requestPost(String urlString, HttpParameters parameters, HttpHeaders requestHeaders) {

    YConnectLogger.debug(this, "URL: " + urlString);

    HttpPost method = new HttpPost(urlString);

    // リクエストヘッダ設定
    if (requestHeaders != null) {
      Iterator<String> itReqHeaders = requestHeaders.keySet().iterator();
      while (itReqHeaders.hasNext()) {
        String key = (String) itReqHeaders.next();
        String value = requestHeaders.get(key);
        method.setHeader(key, value);
        YConnectLogger.debug(this, key + ": " + value);
      }
    }

    try {

      // リクエストパラメータ設定
      String queryString = parameters.toQueryString();
      StringEntity paramEntity = new StringEntity(queryString);
      paramEntity.setChunked(false);
      paramEntity.setContentType("application/x-www-form-urlencoded");
      method.setEntity(paramEntity);

      if (urlString.startsWith("https")) {
        if (!checkSSL) {
          YConnectLogger.debug(this, "HTTPS ignore SSL Certification");
          httpClient = new DefaultHttpClient();
          ignoreSSLCertification(httpClient);
        } else {
          SSLContext sslContext;
          try {
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            SSLSocketFactory sf = new SSLSocketFactory(sslContext);
            Scheme httpsScheme = new Scheme("https", 443, sf);
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(httpsScheme);
            ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
            httpClient = new DefaultHttpClient(cm);
          } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
          } catch (KeyManagementException e1) {
            e1.printStackTrace();
          }
        }
      }

      YConnectLogger.debug(this, "POST Body: " + queryString);

      HttpResponse httpResponse = httpClient.execute(method);

      // レスポンスコード、メッセージ取得
      responseCode = httpResponse.getStatusLine().getStatusCode();
      responseMessage = httpResponse.getStatusLine().getReasonPhrase();

      YConnectLogger.debug(this, "responseCode: " + responseCode);
      YConnectLogger.debug(this, "responseMessage: " + responseMessage);

      // レスポンスヘッダ取得
      Header[] headers = httpResponse.getAllHeaders();
      for (Header header : headers) {
        responseHeaders.put(header.getName(), header.getValue());
      }

      YConnectLogger.debug(this, "responseHeaders: " + responseHeaders);

      // レスポンスボディ取得
      HttpEntity httpEntity = httpResponse.getEntity();
      responseBody = EntityUtils.toString(httpEntity);
      httpEntity.getContent().close();

      YConnectLogger.debug(this, "responseBody: " + responseBody);

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      method.releaseConnection();
      httpClient.getConnectionManager().shutdown();
    }

  }

  /**
   * SSL証明書のチェックを無効化します。
   * 
   * @param httpsconnection
   */
  @SuppressWarnings("deprecation")
  private static void ignoreSSLCertification(HttpClient httpClient) {

    try {

      KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      keyStore.load(null, null);
      SSLSocketFactory sslSocketFactory = new CostomSSLSocketFactory(keyStore);

      sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
      Scheme scheme = new Scheme("https", sslSocketFactory, 443);
      httpClient.getConnectionManager().getSchemeRegistry().register(scheme);

    } catch (Exception e) {
      YConnectLogger.error(YHttpClient.class, e.getMessage());
      e.printStackTrace();
    }

  }

  public static void enableSSLCheck() {
    checkSSL = true;
  }

  public static void disableSSLCheck() {
    checkSSL = false;
  }

}
