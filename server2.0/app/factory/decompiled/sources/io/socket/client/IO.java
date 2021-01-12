package io.socket.client;

import io.socket.client.Manager;
import io.socket.parser.Parser;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

public class IO {
    private static final Logger logger = Logger.getLogger(IO.class.getName());
    private static final ConcurrentHashMap<String, Manager> managers = new ConcurrentHashMap<>();
    public static int protocol = Parser.protocol;

    public static class Options extends Manager.Options {
        public boolean forceNew;
        public boolean multiplex = true;
    }

    public static void setDefaultSSLContext(SSLContext sSLContext) {
        Manager.defaultSSLContext = sSLContext;
    }

    public static void setDefaultHostnameVerifier(HostnameVerifier hostnameVerifier) {
        Manager.defaultHostnameVerifier = hostnameVerifier;
    }

    private IO() {
    }

    public static Socket socket(String str) throws URISyntaxException {
        return socket(str, (Options) null);
    }

    public static Socket socket(String str, Options options) throws URISyntaxException {
        return socket(new URI(str), options);
    }

    public static Socket socket(URI uri) {
        return socket(uri, (Options) null);
    }

    public static Socket socket(URI uri, Options options) {
        Manager manager;
        if (options == null) {
            options = new Options();
        }
        URL parse = Url.parse(uri);
        try {
            URI uri2 = parse.toURI();
            String extractId = Url.extractId(parse);
            String path = parse.getPath();
            ConcurrentHashMap<String, Manager> concurrentHashMap = managers;
            if (options.forceNew || !options.multiplex || (concurrentHashMap.containsKey(extractId) && concurrentHashMap.get(extractId).nsps.containsKey(path))) {
                logger.fine(String.format("ignoring socket cache for %s", uri2));
                manager = new Manager(uri2, options);
            } else {
                if (!concurrentHashMap.containsKey(extractId)) {
                    logger.fine(String.format("new io instance for %s", uri2));
                    concurrentHashMap.putIfAbsent(extractId, new Manager(uri2, options));
                }
                manager = concurrentHashMap.get(extractId);
            }
            return manager.socket(parse.getPath());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
