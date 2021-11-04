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

import javax.json.JsonObject;

/**
 * UserInfo Object Class
 *
 * @author Copyright (C) 2021 Yahoo Japan Corporation. All Rights Reserved.
 */
public class UserInfoObject {

    private JsonObject jsonObject;

    private String sub = "";

    private String ppidSub = "";

    private String locale = "";

    private String name = "";

    private String givenName = "";

    private String givenNameJaKanaJp = "";

    private String givenNameJaHaniJp = "";

    private String familyName = "";

    private String familyNameJaKanaJp = "";

    private String familyNameJaHaniJp = "";

    private String email = "";

    private String emailVerified = "false";

    private String gender = "";

    private String zoneinfo = "";

    private String birthdate = "";

    private String nickname = "";

    private String picture = "";

    private String addressCountry = "";

    private String addressPostalCode = "";

    private String addressRegion = "";

    private String addressLocality = "";

    private String addressFormatted = "";

    public UserInfoObject() {}

    public UserInfoObject(String sub) {
        setSub(sub);
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getPpidSub() {
        return ppidSub;
    }

    public void setPpidSub(String ppidSub) {
        this.ppidSub = ppidSub;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getGivenNameJaKanaJp() {
        return givenNameJaKanaJp;
    }

    public void setGivenNameJaKanaJp(String givenNameJaKanaJp) {
        this.givenNameJaKanaJp = givenNameJaKanaJp;
    }

    public String getGivenNameJaHaniJp() {
        return givenNameJaHaniJp;
    }

    public void setGivenNameJaHaniJp(String givenNameJaHaniJp) {
        this.givenNameJaHaniJp = givenNameJaHaniJp;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFamilyNameJaKanaJp() {
        return familyNameJaKanaJp;
    }

    public void setFamilyNameJaKanaJp(String familyNameJaKanaJp) {
        this.familyNameJaKanaJp = familyNameJaKanaJp;
    }

    public String getFamilyNameJaHaniJp() {
        return familyNameJaHaniJp;
    }

    public void setFamilyNameJaHaniJp(String familyNameJaHaniJp) {
        this.familyNameJaHaniJp = familyNameJaHaniJp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(String emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getZoneinfo() {
        return zoneinfo;
    }

    public void setZoneinfo(String zoneinfo) {
        this.zoneinfo = zoneinfo;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }

    public String getAddressPostalCode() {
        return addressPostalCode;
    }

    public void setAddressPostalCode(String addressPostalCode) {
        this.addressPostalCode = addressPostalCode;
    }

    public String getAddressRegion() {
        return addressRegion;
    }

    public void setAddressRegion(String addressRegion) {
        this.addressRegion = addressRegion;
    }

    public String getAddressLocality() {
        return addressLocality;
    }

    public void setAddressLocality(String addressLocality) {
        this.addressLocality = addressLocality;
    }

    public String getAddressFormatted() {
        return addressFormatted;
    }

    public void setAddressFormatted(String addressFormatted) {
        this.addressFormatted = addressFormatted;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getAdditionalValue(String keyStr) {
        try {
            String[] keys = keyStr.split("/");

            JsonObject jsonObject = this.jsonObject;
            for (int i = 0; i < keys.length - 1; i++) {
                jsonObject = jsonObject.getJsonObject(keys[i]);
            }
            return jsonObject.getString(keys[keys.length - 1]);
        } catch (Exception e) {
            return "";
        }
    }

    public JsonObject getJsonObject() {
        return this.jsonObject;
    }

    public String toString() {
        return "{"
                + "\"sub\":\""
                + sub
                + "\","
                + "\"ppid_sub\":\""
                + ppidSub
                + "\","
                + "\"locale\":\""
                + locale
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
                + "\"email\":\""
                + email
                + "\","
                + "\"email_verified\":\""
                + emailVerified
                + "\","
                + "\"gender\":"
                + gender
                + ","
                + "\"zoneinfo\":\""
                + zoneinfo
                + "\","
                + "\"birthdate\":\""
                + birthdate
                + "\","
                + "\"nickname\":\""
                + nickname
                + "\","
                + "\"picture\":\""
                + picture
                + "\","
                + "\"address/country\":\""
                + addressCountry
                + "\","
                + "\"address/postal_code\":\""
                + addressPostalCode
                + "\","
                + "\"address/region\":\""
                + addressRegion
                + "\","
                + "\"address/locality\":\""
                + addressLocality
                + "\","
                + "\"address/formatted\":\""
                + addressFormatted
                + "\""
                + "}";
    }
}
