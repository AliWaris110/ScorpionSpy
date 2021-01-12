package io.socket.client;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

public class Url {
    private static Pattern PATTERN_HTTP = Pattern.compile("^http|ws$");
    private static Pattern PATTERN_HTTPS = Pattern.compile("^(http|ws)s$");

    private Url() {
    }

    public static URL parse(String str) throws URISyntaxException {
        return parse(new URI(str));
    }

    public static URL parse(URI uri) {
        String str;
        String str2;
        String str3;
        String scheme = uri.getScheme();
        if (scheme == null || !scheme.matches("^https?|wss?$")) {
            scheme = "https";
        }
        int port = uri.getPort();
        if (port == -1) {
            if (PATTERN_HTTP.matcher(scheme).matches()) {
                port = 80;
            } else if (PATTERN_HTTPS.matcher(scheme).matches()) {
                port = 443;
            }
        }
        String rawPath = uri.getRawPath();
        if (rawPath == null || rawPath.length() == 0) {
            rawPath = "/";
        }
        String rawUserInfo = uri.getRawUserInfo();
        String rawQuery = uri.getRawQuery();
        String rawFragment = uri.getRawFragment();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(scheme);
            sb.append("://");
            String str4 = "";
            if (rawUserInfo != null) {
                str = rawUserInfo + "@";
            } else {
                str = str4;
            }
            sb.append(str);
            sb.append(uri.getHost());
            if (port != -1) {
                str2 = ":" + port;
            } else {
                str2 = str4;
            }
            sb.append(str2);
            sb.append(rawPath);
            if (rawQuery != null) {
                str3 = "?" + rawQuery;
            } else {
                str3 = str4;
            }
            sb.append(str3);
            if (rawFragment != null) {
                str4 = "#" + rawFragment;
            }
            sb.append(str4);
            return new URL(sb.toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String extractId(String str) throws MalformedURLException {
        return extractId(new URL(str));
    }

    public static String extractId(URL url) {
        String protocol = url.getProtocol();
        int port = url.getPort();
        if (port == -1) {
            if (PATTERN_HTTP.matcher(protocol).matches()) {
                port = 80;
            } else if (PATTERN_HTTPS.matcher(protocol).matches()) {
                port = 443;
            }
        }
        return protocol + "://" + url.getHost() + ":" + port;
    }
}
