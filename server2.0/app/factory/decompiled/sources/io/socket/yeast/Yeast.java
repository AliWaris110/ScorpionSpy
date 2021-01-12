package io.socket.yeast;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class Yeast {
    private static char[] alphabet;
    private static int length;
    private static Map<Character, Integer> map = new HashMap(length);
    private static String prev;
    private static int seed = 0;

    static {
        char[] charArray = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_".toCharArray();
        alphabet = charArray;
        length = charArray.length;
        for (int i = 0; i < length; i++) {
            map.put(Character.valueOf(alphabet[i]), Integer.valueOf(i));
        }
    }

    private Yeast() {
    }

    public static String encode(long j) {
        StringBuilder sb = new StringBuilder();
        do {
            sb.insert(0, alphabet[(int) (j % ((long) length))]);
            j /= (long) length;
        } while (j > 0);
        return sb.toString();
    }

    public static long decode(String str) {
        long j = 0;
        for (char c : str.toCharArray()) {
            j = (j * ((long) length)) + ((long) map.get(Character.valueOf(c)).intValue());
        }
        return j;
    }

    public static String yeast() {
        String encode = encode(new Date().getTime());
        if (!encode.equals(prev)) {
            seed = 0;
            prev = encode;
            return encode;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(encode);
        sb.append(".");
        int i = seed;
        seed = i + 1;
        sb.append(encode((long) i));
        return sb.toString();
    }
}
