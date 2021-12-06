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
import java.util.Set;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import jp.co.yahoo.yconnect.core.api.ApiClientException;
import jp.co.yahoo.yconnect.core.http.HttpHeaders;
import jp.co.yahoo.yconnect.core.http.YHttpClient;

/**
 * PublicKeys Client Class
 *
 * @author © Yahoo Japan
 */
public class PublicKeysClient {

    private PublicKeysObject publicKeysObject;

    public void fetchResource(String url) throws ApiClientException {
        YHttpClient client = getYHttpClient();
        client.requestGet(url, null, null);

        int responseCode = client.getStatusCode();
        String responseMessage = client.getStatusMessage();
        HttpHeaders responseHeaders = client.getResponseHeaders();
        String responseBody = client.getResponseBody();

        if (responseCode != 200) {
            throw new ApiClientException(
                    "Failed Request.(status code: "
                            + responseCode
                            + " status message: "
                            + responseMessage
                            + ")",
                    responseHeaders.toString());
        }

        publicKeysParser(responseBody);
    }

    private void publicKeysParser(String json) {
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject rootObject = jsonReader.readObject();
        jsonReader.close();

        publicKeysObject = new PublicKeysObject();

        Set<String> kids = rootObject.keySet();
        for (String kid : kids) {
            publicKeysObject.register(kid, rootObject.getString(kid));
        }

        publicKeysObject.setJsonObject(rootObject);
    }

    public PublicKeysObject getPublicKeysObject() {
        return publicKeysObject;
    }

    protected YHttpClient getYHttpClient() {
        return new YHttpClient();
    }
}
