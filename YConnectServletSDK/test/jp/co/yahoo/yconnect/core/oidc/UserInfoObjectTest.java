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

package jp.co.yahoo.yconnect.core.oidc;

import static org.junit.Assert.*;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.Test;

public class UserInfoObjectTest {

  private static String sub = "xHOs7gRnw2kq6Nd2nMDYg0aAwxLbZmJky4OMrA8v4-7EYZZS00";
  private static String name = "矢風太郎";
  private static String givenName = "太郎";
  private static String givenNameJaKanaJp = "タロウ";
  private static String givenNameJaHaniJp = "太郎";
  private static String familyName = "矢風";
  private static String familyNameJaKanaJp = "ヤフウ";
  private static String familyNameJaHaniJp = "矢風";
  private static String email = "your_email@example.com";
  private static String gender = "male";
  private static String birthdate = "2000";
  private static String locale = "ja-JP";
  private static String zoneInfo = "Asia/Tokyo";
  private static String nickname = "やふうたろう";
  private static String picture = "https://dummy.img.yahoo.co.jp/example.png";
  private static String emailVerified = "true";
  private static String addressCountry = "JP";
  private static String addressPostalCode = "1028282";
  private static String addressRegion = "東京都";
  private static String addressLocality = "千代田区";
  private static String addressFormatted = "東京都千代田区";

  @Test
  public void testGetAdditionalValue() {
    UserInfoObject uio = new UserInfoObject();
    JsonObject addressJsonObject =
        Json.createObjectBuilder().add("country", "JP").add("postal_code", "1028282")
            .add("region", "東京都").add("locality", "千代田区")
            .add("formatted", "東京都千代田区").build();
    JsonObject rootJsonObject =
        Json.createObjectBuilder().add("sub", "xHOs7gRnw2kq6Nd2nMDYg0aAwxLbZmJky4OMrA8v4-7EYZZS00")
            .add("name", "矢風太郎").add("given_name", "太郎")
            .add("given_name#ja-Kana-JP", "タロウ").add("given_name#ja-Hani-JP", "太郎")
            .add("family_name", "矢風").add("family_name#ja-Kana-JP", "ヤフウ")
            .add("family_name#ja-Hani-JP", "矢風").add("email", "your_email@example.com")
            .add("gender", "male").add("birthdate", "2000")
            .add("locale", "ja-JP").add("zoneinfo", "Asia/Tokyo")
            .add("nickname", "やふうたろう").add("picture", "https://dummy.img.yahoo.co.jp/example.png")
            .add("email_verified", "true").add("address", addressJsonObject).build();

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
