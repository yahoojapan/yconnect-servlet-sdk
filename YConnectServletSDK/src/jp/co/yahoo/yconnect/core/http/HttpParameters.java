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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

/**
 * HTTP Parameters Class
 *
 * @author Copyright (C) 2016 Yahoo Japan Corporation. All Rights Reserved.
 *
 */
public class HttpParameters extends HashMap<String, String> {

  private static final long serialVersionUID = 1L;

  /**
   * HttpParameters Constructor.
   */
  public HttpParameters() {
    super();
  }

  /**
   * Get all parameters of URL encoded query string.
   * 
   * @return query string.
   */
  public String toQueryString() {
    String query = "";
    String ampersand = "";
    Iterator<String> iterator = this.keySet().iterator();
    while (iterator.hasNext()) {
      String key = iterator.next();
      try {
        query += ampersand + key + "=" + URLEncoder.encode(this.get(key), "UTF-8");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      ampersand = "&";
    }
    return query;
  }

}
