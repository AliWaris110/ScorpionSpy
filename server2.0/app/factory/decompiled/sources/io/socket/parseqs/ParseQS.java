package io.socket.parseqs;

import io.socket.global.Global;
import java.util.HashMap;
import java.util.Map;

public class ParseQS {
    private ParseQS() {
    }

    public static String encode(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(Global.encodeURIComponent(entry.getKey()));
            sb.append("=");
            sb.append(Global.encodeURIComponent(entry.getValue()));
        }
        return sb.toString();
    }

    public static Map<String, String> decode(String str) {
        HashMap hashMap = new HashMap();
        for (String str2 : str.split("&")) {
            String[] split = str2.split("=");
            hashMap.put(Global.decodeURIComponent(split[0]), split.length > 1 ? Global.decodeURIComponent(split[1]) : "");
        }
        return hashMap;
    }
}
