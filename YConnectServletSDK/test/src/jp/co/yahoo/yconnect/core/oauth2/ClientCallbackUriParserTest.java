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

package jp.co.yahoo.yconnect.core.oauth2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import jp.co.yahoo.yconnect.core.http.HttpParameters;
import org.junit.Test;

public class ClientCallbackUriParserTest {

    @Test
    public void testHasAuthorizationCodeReturnsTrue() throws Exception {
        String code = "sample_code";
        ClientCallbackUriParser parser =
                new ClientCallbackUriParser("code=" + code + "&" + "state=state");

        assertTrue(parser.hasAuthorizationCode());
    }

    @Test
    public void testHasAuthorizationCodeReturnsFalse() throws Exception {
        ClientCallbackUriParser parser = new ClientCallbackUriParser("state=state");

        assertFalse(parser.hasAuthorizationCode());
    }

    @Test
    public void testHasAuthorizationCodeGivenEmptyThenReturnsFalse() throws Exception {
        ClientCallbackUriParser parser = new ClientCallbackUriParser("");

        assertFalse(parser.hasAuthorizationCode());
    }

    @Test
    public void testGetAuthorizationCode() throws Exception {
        String code = "sample_code";
        ClientCallbackUriParser parser =
                new ClientCallbackUriParser("code=" + code + "&" + "state=state");

        assertEquals(code, parser.getAuthorizationCode("state"));
    }

    @Test
    public void testGetAuthorizationCodeReturnsNull() throws Exception {
        ClientCallbackUriParser parser = new ClientCallbackUriParser("state=state");

        assertNull(parser.getAuthorizationCode("state"));
    }

    @Test()
    public void testGetAuthorizationCodeThrowAuthorizationException() throws Exception {
        String code = "sample_code";
        ClientCallbackUriParser parser =
                new ClientCallbackUriParser("code=" + code + "&" + "state=state");

        AuthorizationException ex =
                assertThrows(
                        AuthorizationException.class,
                        () -> parser.getAuthorizationCode("invalid_state"));
        assertEquals("Not Match State.", ex.getError());
        assertEquals("", ex.getMessage());
    }

    @Test
    public void testParseUri() throws Exception {
        String code = "sample_code";
        String state = "sample_state";
        ClientCallbackUriParser parser =
                new ClientCallbackUriParser("code=" + code + "&" + "state=" + state);

        Field parametersField = ClientCallbackUriParser.class.getDeclaredField("parameters");
        parametersField.setAccessible(true);
        HttpParameters parameters = (HttpParameters) parametersField.get(parser);

        assertEquals(code, parameters.get("code"));
        assertEquals(state, parameters.get("state"));
    }

    @Test
    public void testParseUriByNull() throws Exception {
        ClientCallbackUriParser parser = new ClientCallbackUriParser(null);

        Field parametersField = ClientCallbackUriParser.class.getDeclaredField("parameters");
        parametersField.setAccessible(true);
        HttpParameters parameters = (HttpParameters) parametersField.get(parser);

        assertNull(parameters.get("code"));
        assertNull(parameters.get("state"));
    }

    @Test
    public void testParseUriThrowsAuthorizationExceptionByError() {
        String error = "sample_error";
        String errorDescription = "sample_error_description";
        String errorCode = "1000";

        AuthorizationException ex =
                assertThrows(
                        AuthorizationException.class,
                        () ->
                                new ClientCallbackUriParser(
                                        "error="
                                                + error
                                                + "&error_description="
                                                + errorDescription
                                                + "&error_code="
                                                + errorCode));

        assertEquals(error, ex.getError());
        assertEquals(errorDescription, ex.getErrorDescription());
        assertEquals(errorCode, ex.getErrorCode());
    }
}
