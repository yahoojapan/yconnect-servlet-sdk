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

import java.util.ArrayList;

/**
 * IdToken Object Class
 *
 * @author © Yahoo Japan
 */
public class IdTokenObject {

    private String type;

    private String algorithm;

    private String kid;

    private String iss;

    private String sub;

    private String ppidSub;

    private ArrayList<String> aud;

    private String nonce;

    private String atHash;

    private long exp;

    private long iat;

    private long authTime;

    private String signature;

    public IdTokenObject() {}

    public IdTokenObject(
            String type,
            String algorithm,
            String kid,
            String iss,
            String sub,
            String ppidSub,
            ArrayList<String> aud,
            String nonce,
            String atHash,
            long exp,
            long iat,
            long authTime,
            String signature) {
        setType(type);
        setAlgorithm(algorithm);
        setKid(kid);
        setIss(iss);
        setSub(sub);
        setPpidSub(ppidSub);
        setAud(aud);
        setNonce(nonce);
        setAtHash(atHash);
        setExp(exp);
        setIat(iat);
        setAuthTime(authTime);
        setSignature(signature);
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
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

    public ArrayList<String> getAud() {
        return aud;
    }

    public void setAud(ArrayList<String> aud) {
        this.aud = aud;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getAtHash() {
        return atHash;
    }

    public void setAtHash(String atHash) {
        this.atHash = atHash;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getIat() {
        return iat;
    }

    public void setIat(long iat) {
        this.iat = iat;
    }

    public long getAuthTime() {
        return authTime;
    }

    public void setAuthTime(long authTime) {
        this.authTime = authTime;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String toString() {
        return "{"
                + "\"iss\":\""
                + iss
                + "\","
                + "\"sub\":\""
                + sub
                + "\","
                + "\"ppid_sub\":\""
                + ppidSub
                + "\","
                + "\"aud\":\""
                + aud.toString()
                + "\","
                + "\"nonce\":\""
                + nonce
                + "\","
                + "\"at_hash\":"
                + atHash
                + ","
                + "\"exp\":"
                + exp
                + ","
                + "\"iat\":"
                + iat
                + ","
                + "\"auth_time\":"
                + authTime
                + "}";
    }
}
