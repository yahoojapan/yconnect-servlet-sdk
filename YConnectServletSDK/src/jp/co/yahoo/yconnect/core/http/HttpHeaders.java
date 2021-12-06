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

package jp.co.yahoo.yconnect.core.http;

import java.util.HashMap;

/**
 * HTTP Headers Class
 *
 * @author © Yahoo Japan
 */
public class HttpHeaders extends HashMap<String, String> {

    private static final long serialVersionUID = 1L;

    /** HttpHeaders Constructor. */
    public HttpHeaders() {
        super();
    }

    /**
     * Get all headers.
     *
     * @return all headers of type string.
     */
    public String toHeaderString() {
        StringBuilder headers = new StringBuilder();
        String indent = "";
        for (String key : this.keySet()) {
            headers.append(indent).append(key).append(": ").append(this.get(key));
            indent = "\n";
        }
        return headers.toString();
    }
}
