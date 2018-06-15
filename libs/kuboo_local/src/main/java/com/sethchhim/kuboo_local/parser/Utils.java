package com.sethchhim.kuboo_local.parser;

import java.security.MessageDigest;

public class Utils {

    public static String MD5(String string) {
        try {
            byte[] strBytes = string.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] digest = messageDigest.digest(strBytes);
            StringBuilder stringBuilder = new StringBuilder();
            for (byte aDigest : digest) {
                stringBuilder.append(Integer.toHexString((aDigest & 0xFF) | 0x100).substring(1, 3));
            }
            return stringBuilder.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            return string.replace("/", ".");
        }
    }

}
