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

package jp.co.yahoo.yconnect.core.oidc;

import static org.junit.Assert.assertEquals;

import javax.json.Json;
import javax.json.JsonObject;
import org.junit.Test;

public class UserInfoObjectTest {

    @Test
    public void testGetAdditionalValue() {
        String sub = "xHOs7gRnw2kq6Nd2nMDYg0aAwxLbZmJky4OMrA8v4-7EYZZS00";
        String name = "矢風太郎";
        String givenName = "太郎";
        String givenNameJaKanaJp = "タロウ";
        String givenNameJaHaniJp = "太郎";
        String familyName = "矢風";
        String familyNameJaKanaJp = "ヤフウ";
        String familyNameJaHaniJp = "矢風";
        String email = "your_email@example.com";
        String gender = "male";
        String birthdate = "2000";
        String locale = "ja-JP";
        String zoneInfo = "Asia/Tokyo";
        String nickname = "やふうたろう";
        String picture = "https://dummy.img.yahoo.co.jp/example.png";
        String emailVerified = "true";
        String addressCountry = "JP";
        String addressPostalCode = "1028282";
        String addressRegion = "東京都";
        String addressLocality = "千代田区";
        String addressFormatted = "東京都千代田区";

        UserInfoObject uio = new UserInfoObject();
        JsonObject addressJsonObject =
                Json.createObjectBuilder()
                        .add("country", addressCountry)
                        .add("postal_code", addressPostalCode)
                        .add("region", addressRegion)
                        .add("locality", addressLocality)
                        .add("formatted", addressFormatted)
                        .build();
        JsonObject rootJsonObject =
                Json.createObjectBuilder()
                        .add("sub", sub)
                        .add("name", name)
                        .add("given_name", givenName)
                        .add("given_name#ja-Kana-JP", givenNameJaKanaJp)
                        .add("given_name#ja-Hani-JP", givenNameJaHaniJp)
                        .add("family_name", familyName)
                        .add("family_name#ja-Kana-JP", familyNameJaKanaJp)
                        .add("family_name#ja-Hani-JP", familyNameJaHaniJp)
                        .add("email", email)
                        .add("gender", gender)
                        .add("birthdate", birthdate)
                        .add("locale", locale)
                        .add("zoneinfo", zoneInfo)
                        .add("nickname", nickname)
                        .add("picture", picture)
                        .add("email_verified", emailVerified)
                        .add("address", addressJsonObject)
                        .build();

        uio.setJsonObject(rootJsonObject);
        assertEquals(uio.getAdditionalValue("sub"), sub);
        assertEquals(uio.getAdditionalValue("name"), name);
        assertEquals(uio.getAdditionalValue("given_name"), givenName);
        assertEquals(uio.getAdditionalValue("given_name#ja-Kana-JP"), givenNameJaKanaJp);
        assertEquals(uio.getAdditionalValue("given_name#ja-Hani-JP"), givenNameJaHaniJp);
        assertEquals(uio.getAdditionalValue("family_name"), familyName);
        assertEquals(uio.getAdditionalValue("family_name#ja-Kana-JP"), familyNameJaKanaJp);
        assertEquals(uio.getAdditionalValue("family_name#ja-Hani-JP"), familyNameJaHaniJp);
        assertEquals(uio.getAdditionalValue("email"), email);
        assertEquals(uio.getAdditionalValue("gender"), gender);
        assertEquals(uio.getAdditionalValue("birthdate"), birthdate);
        assertEquals(uio.getAdditionalValue("locale"), locale);
        assertEquals(uio.getAdditionalValue("zoneinfo"), zoneInfo);
        assertEquals(uio.getAdditionalValue("nickname"), nickname);
        assertEquals(uio.getAdditionalValue("picture"), picture);
        assertEquals(uio.getAdditionalValue("email_verified"), emailVerified);
        assertEquals(uio.getAdditionalValue("address/country"), addressCountry);
        assertEquals(uio.getAdditionalValue("address/postal_code"), addressPostalCode);
        assertEquals(uio.getAdditionalValue("address/region"), addressRegion);
        assertEquals(uio.getAdditionalValue("address/locality"), addressLocality);
        assertEquals(uio.getAdditionalValue("address/formatted"), addressFormatted);
    }

    @Test
    public void testErrorGetAdditionalValue() {
        UserInfoObject uio = new UserInfoObject();
        assertEquals(uio.getAdditionalValue("sub"), "");
        assertEquals(uio.getAdditionalValue("name"), "");
        assertEquals(uio.getAdditionalValue("given_name"), "");
        assertEquals(uio.getAdditionalValue("given_name#ja-Kana-JP"), "");
        assertEquals(uio.getAdditionalValue("given_name#ja-Hani-JP"), "");
        assertEquals(uio.getAdditionalValue("family_name"), "");
        assertEquals(uio.getAdditionalValue("family_name#ja-Kana-JP"), "");
        assertEquals(uio.getAdditionalValue("family_name#ja-Hani-JP"), "");
        assertEquals(uio.getAdditionalValue("email"), "");
        assertEquals(uio.getAdditionalValue("birthdate"), "");
        assertEquals(uio.getAdditionalValue("locale"), "");
        assertEquals(uio.getAdditionalValue("zoneinfo"), "");
        assertEquals(uio.getAdditionalValue("nickname"), "");
        assertEquals(uio.getAdditionalValue("picture"), "");
        assertEquals(uio.getAdditionalValue("email_verified"), "");
        assertEquals(uio.getAdditionalValue("address/country"), "");
        assertEquals(uio.getAdditionalValue("address/postal_code"), "");
        assertEquals(uio.getAdditionalValue("address/region"), "");
        assertEquals(uio.getAdditionalValue("address/locality"), "");
        assertEquals(uio.getAdditionalValue("address/formatted"), "");
    }

    @Test
    public void testInvalidGetAdditionalValue() {
        UserInfoObject uio = new UserInfoObject();
        assertEquals(uio.getAdditionalValue("invalid"), "");
    }
}
