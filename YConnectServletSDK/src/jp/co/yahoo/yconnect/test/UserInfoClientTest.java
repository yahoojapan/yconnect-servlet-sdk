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

package jp.co.yahoo.yconnect.test;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jp.co.yahoo.yconnect.core.oidc.UserInfoClient;
import jp.co.yahoo.yconnect.core.oidc.UserInfoObject;

import org.junit.Test;

public class UserInfoClientTest {

  private static String userId = "43M63NAGMHBAYMXRMY3WODOWS4";
  private static String name = "矢風太郎";
  private static String givenName = "太郎";
  private static String givenNameJaKanaJp = "タロウ";
  private static String givenNameJaHaniJp = "太郎";
  private static String familyName = "矢風";
  private static String familyNameJaKanaJp = "ヤフウ";
  private static String familyNameJaHaniJp = "矢風";
  private static String gender = "male";
  private static String birthday = "2000";
  private static String locale = "ja-JP";
  private static String email = "your_email@example.com";
  private static String emailVerified = "true";
  private static String addressLocality = "港区";
  private static String addressRegion = "東京都";
  private static String addressPostalCode = "1076211";
  private static String addressCountry = "jp";

  @Test
  public void testUserInfoParser() throws SecurityException, NoSuchMethodException,
      IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    final String json =
        "{\"user_id\":\"43M63NAGMHBAYMXRMY3WODOWS4\"," + "\"name\":\"矢風太郎\","
            + "\"given_name\":\"太郎\"," + "\"given_name#ja-Kana-JP\":\"タロウ\","
            + "\"given_name#ja-Hani-JP\":\"太郎\"," + "\"family_name\":\"矢風\","
            + "\"family_name#ja-Kana-JP\":\"ヤフウ\"," + "\"family_name#ja-Hani-JP\":\"矢風\","
            + "\"gender\":\"male\"," + "\"birthday\":\"2000\"," + "\"locale\":\"ja-JP\","
            + "\"email\":\"your_email@example.com\"," + "\"email_verified\":true,"
            + "\"address\":{" + "\"locality\":\"港区\"," + "\"region\":\"東京都\","
            + "\"postal_code\":\"1076211\"," + "\"country\":\"jp\"}}";

    UserInfoClient uic = new UserInfoClient("");

    Class<UserInfoClient> c = UserInfoClient.class;
    Method uip = c.getDeclaredMethod("userInfoParser", String.class);
    uip.setAccessible(true);
    uip.invoke(uic, json);
    UserInfoObject uio = uic.getUserInfoObject();

    assertEquals(uio.getUserId(), userId);
    assertEquals(uio.getName(), name);
    assertEquals(uio.getGivenName(), givenName);
    assertEquals(uio.getGivenNameJaKanaJp(), givenNameJaKanaJp);
    assertEquals(uio.getGivenNameJaHaniJp(), givenNameJaHaniJp);
    assertEquals(uio.getFamilyName(), familyName);
    assertEquals(uio.getFamilyNameJaKanaJp(), familyNameJaKanaJp);
    assertEquals(uio.getFamilyNameJaHaniJp(), familyNameJaHaniJp);
    assertEquals(uio.getEmail(), email);
    assertEquals(uio.getEmailVerified(), emailVerified);
    assertEquals(uio.getGender(), gender);
    assertEquals(uio.getBirthday(), birthday);
    assertEquals(uio.getLocale(), locale);
    assertEquals(uio.getAddressLocality(), addressLocality);
    assertEquals(uio.getAddressRegion(), addressRegion);
    assertEquals(uio.getAddressPostalCode(), addressPostalCode);
    assertEquals(uio.getAddressCountry(), addressCountry);

  }

  @Test
  public void testUserInfoParserInvalid() throws SecurityException, NoSuchMethodException,
      IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    final String json =
        "{\"user_id\":\"43M63NAGMHBAYMXRMY3WODOWS4\"," + "\"name\":\"矢風太郎\","
            + "\"given_name\":\"太郎\"," + "\"given_name#ja-Kana-JP\":\"タロウ\","
            + "\"given_name#ja-Hani-JP\":\"太郎\"," + "\"family_name\":\"矢風\","
            + "\"family_name#ja-Kana-JP\":\"ヤフウ\"," + "\"family_name#ja-Hani-JP\":\"矢風\","
            + "\"gender\":\"male\"," + "\"birthday\":\"2000\"," + "\"locale\":\"ja-JP\","
            + "\"email\":\"your_email@example.com\"," + "\"email_verified\":true,"
            + "\"address\":{" + "\"locality\":\"港区\"," + "\"region\":\"東京都\","
            + "\"postal_code\":\"1076211\"," + "\"country\":\"jp\","
            + "\"additional_attribute\":\"add\"}}";

    UserInfoClient uic = new UserInfoClient("");

    Class<UserInfoClient> c = UserInfoClient.class;
    Method uip = c.getDeclaredMethod("userInfoParser", String.class);
    uip.setAccessible(true);
    uip.invoke(uic, json);
    UserInfoObject uio = uic.getUserInfoObject();

    assertEquals(uio.getAdditionalValue("additional_attribute2"), "");
  }
}
