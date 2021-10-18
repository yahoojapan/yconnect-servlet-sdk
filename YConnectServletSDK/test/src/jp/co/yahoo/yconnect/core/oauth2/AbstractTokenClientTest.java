/**
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

package jp.co.yahoo.yconnect.core.oauth2;

import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class AbstractTokenClientTest {

    private final AbstractTokenClient client = new AbstractTokenClient(null, null, null) {
        @Override
        void fetch() {

        }
    };

    @Test
    public void checkErrorResponse() throws Exception {
        String json = "{\"access_token\":\"sample_token\", \"expires_in\":3600}";
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        client.checkErrorResponse(200, jsonObject);
    }

    @Test
    public void testErrorResponseThrowsTokenExceptionWhenJsonHasError() {
        String error = "sample_error";
        String errorDescription = "sample_error_description";
        Integer errorCode = 1000;
        String json = "{\"error\":\"" + error + "\", \"error_description\":\"" + errorDescription
                + "\", \"error_code\":" + errorCode + "}";
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        TokenException ex = assertThrows(TokenException.class, () -> client.checkErrorResponse(400, jsonObject));

        assertEquals(error, ex.getError());
        assertEquals(errorDescription, ex.getErrorDescription());
        assertEquals(errorCode, ex.getErrorCode());
    }

    @Test
    public void testErrorResponseThrowsTokenExceptionWhenJsonHasNotError() {
        String json = "{}";
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        TokenException ex = assertThrows(TokenException.class, () -> client.checkErrorResponse(400, jsonObject));

        assertEquals("An unexpected error has occurred.", ex.getError());
        assertEquals("", ex.getErrorDescription());
    }

    @Test
    public void testErrorResponseThrowsTokenExceptionWhenStatusCodeInvalid() {
        String json = "{\"error\":\"sample_error\", \"error_description\":\"sample_error_description\"}";
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        TokenException ex = assertThrows(TokenException.class, () -> client.checkErrorResponse(301, jsonObject));

        assertEquals("An unexpected error has occurred.", ex.getError());
        assertEquals("", ex.getErrorDescription());
    }
}
