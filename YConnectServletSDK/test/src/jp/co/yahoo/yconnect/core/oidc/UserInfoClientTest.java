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

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Test;

public class UserInfoClientTest {

    @Test
    public void testUserInfoParser()
            throws SecurityException, NoSuchMethodException, IllegalArgumentException,
                    IllegalAccessException, InvocationTargetException {
        String sub = "43M63NAGMHBAYMXRMY3WODOWS4";
        String name = "矢風太郎";
        String givenName = "太郎";
        String givenNameJaKanaJp = "タロウ";
        String givenNameJaHaniJp = "太郎";
        String familyName = "矢風";
        String familyNameJaKanaJp = "ヤフウ";
        String familyNameJaHaniJp = "矢風";
        String gender = "male";
        String birthdate = "2000";
        String locale = "ja-JP";
        String email = "your_email@example.com";
        String emailVerified = "true";
        String addressLocality = "港区";
        String addressFormatted = "東京都港区";
        String addressRegion = "東京都";
        String addressPostalCode = "1076211";
        String addressCountry = "jp";

        final String json =
                "{\"sub\":\""
                        + sub
                        + "\","
                        + "\"name\":\""
                        + name
                        + "\","
                        + "\"given_name\":\""
                        + givenName
                        + "\","
                        + "\"given_name#ja-Kana-JP\":\""
                        + givenNameJaKanaJp
                        + "\","
                        + "\"given_name#ja-Hani-JP\":\""
                        + givenNameJaHaniJp
                        + "\","
                        + "\"family_name\":\""
                        + familyName
                        + "\","
                        + "\"family_name#ja-Kana-JP\":\""
                        + familyNameJaKanaJp
                        + "\","
                        + "\"family_name#ja-Hani-JP\":\""
                        + familyNameJaHaniJp
                        + "\","
                        + "\"gender\":\""
                        + gender
                        + "\","
                        + "\"birthdate\":\""
                        + birthdate
                        + "\","
                        + "\"locale\":\""
                        + locale
                        + "\","
                        + "\"email\":\""
                        + email
                        + "\","
                        + "\"email_verified\":"
                        + emailVerified
                        + ","
                        + "\"address\":{"
                        + "\"locality\":\""
                        + addressLocality
                        + "\","
                        + "\"region\":\""
                        + addressRegion
                        + "\","
                        + "\"postal_code\":\""
                        + addressPostalCode
                        + "\","
                        + "\"country\":\""
                        + addressCountry
                        + "\","
                        + "\"formatted\":\""
                        + addressFormatted
                        + "\"}}";

        UserInfoClient uic = new UserInfoClient("");

        Class<UserInfoClient> c = UserInfoClient.class;
        Method uip = c.getDeclaredMethod("userInfoParser", String.class);
        uip.setAccessible(true);
        uip.invoke(uic, json);
        UserInfoObject uio = uic.getUserInfoObject();

        assertEquals(uio.getSub(), sub);
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
        assertEquals(uio.getBirthdate(), birthdate);
        assertEquals(uio.getLocale(), locale);
        assertEquals(uio.getAddressLocality(), addressLocality);
        assertEquals(uio.getAddressFormatted(), addressFormatted);
        assertEquals(uio.getAddressRegion(), addressRegion);
        assertEquals(uio.getAddressPostalCode(), addressPostalCode);
        assertEquals(uio.getAddressCountry(), addressCountry);
    }

    @Test
    public void testUserInfoParserInvalid()
            throws SecurityException, NoSuchMethodException, IllegalArgumentException,
                    IllegalAccessException, InvocationTargetException {
        final String json =
                "{\"sub\":\"43M63NAGMHBAYMXRMY3WODOWS4\","
                        + "\"name\":\"矢風太郎\","
                        + "\"given_name\":\"太郎\","
                        + "\"given_name#ja-Kana-JP\":\"タロウ\","
                        + "\"given_name#ja-Hani-JP\":\"太郎\","
                        + "\"family_name\":\"矢風\","
                        + "\"family_name#ja-Kana-JP\":\"ヤフウ\","
                        + "\"family_name#ja-Hani-JP\":\"矢風\","
                        + "\"gender\":\"male\","
                        + "\"birthdate\":\"2000\","
                        + "\"locale\":\"ja-JP\","
                        + "\"email\":\"your_email@example.com\","
                        + "\"email_verified\":true,"
                        + "\"address\":{"
                        + "\"locality\":\"港区\","
                        + "\"region\":\"東京都\","
                        + "\"postal_code\":\"1076211\","
                        + "\"country\":\"jp\","
                        + "\"formatted\":\"東京都港区\"}}";

        UserInfoClient uic = new UserInfoClient("");

        Class<UserInfoClient> c = UserInfoClient.class;
        Method uip = c.getDeclaredMethod("userInfoParser", String.class);
        uip.setAccessible(true);
        uip.invoke(uic, json);
        UserInfoObject uio = uic.getUserInfoObject();

        assertEquals(uio.getAdditionalValue("additional_attribute2"), "");
    }
}
