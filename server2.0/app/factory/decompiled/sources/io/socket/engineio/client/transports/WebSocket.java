package io.socket.engineio.client.transports;

import io.socket.engineio.client.Transport;
import io.socket.engineio.parser.Packet;
import io.socket.engineio.parser.Parser;
import io.socket.parseqs.ParseQS;
import io.socket.thread.EventThread;
import io.socket.utf8.UTF8Exception;
import io.socket.yeast.Yeast;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocket extends Transport {
    public static final String NAME = "websocket";
    private static final Logger logger = Logger.getLogger(PollingXHR.class.getName());
    private okhttp3.WebSocket ws;

    public WebSocket(Transport.Options options) {
        super(options);
        this.name = NAME;
    }

    /* access modifiers changed from: protected */
    @Override // io.socket.engineio.client.Transport
    public void doOpen() {
        TreeMap treeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        emit("requestHeaders", treeMap);
        OkHttpClient.Builder writeTimeout = new OkHttpClient.Builder().connectTimeout(0, TimeUnit.MILLISECONDS).readTimeout(0, TimeUnit.MILLISECONDS).writeTimeout(0, TimeUnit.MILLISECONDS);
        if (this.sslContext != null) {
            writeTimeout.sslSocketFactory(this.sslContext.getSocketFactory());
        }
        if (this.hostnameVerifier != null) {
            writeTimeout.hostnameVerifier(this.hostnameVerifier);
        }
        if (this.proxy != null) {
            writeTimeout.proxy(this.proxy);
        }
        if (this.proxyLogin != null && !this.proxyLogin.isEmpty()) {
            final String basic = Credentials.basic(this.proxyLogin, this.proxyPassword);
            writeTimeout.proxyAuthenticator(new Authenticator() {
                /* class io.socket.engineio.client.transports.WebSocket.AnonymousClass1 */

                @Override // okhttp3.Authenticator
                public Request authenticate(Route route, Response response) throws IOException {
                    return response.request().newBuilder().header("Proxy-Authorization", basic).build();
                }
            });
        }
        Request.Builder url = new Request.Builder().url(uri());
        for (Map.Entry entry : treeMap.entrySet()) {
            for (String str : (List) entry.getValue()) {
                url.addHeader((String) entry.getKey(), str);
            }
        }
        Request build = url.build();
        OkHttpClient build2 = writeTimeout.build();
        this.ws = build2.newWebSocket(build, new WebSocketListener() {
            /* class io.socket.engineio.client.transports.WebSocket.AnonymousClass2 */

            @Override // okhttp3.WebSocketListener
            public void onOpen(okhttp3.WebSocket webSocket, Response response) {
                final Map<String, List<String>> multimap = response.headers().toMultimap();
                EventThread.exec(new Runnable() {
                    /* class io.socket.engineio.client.transports.WebSocket.AnonymousClass2.AnonymousClass1 */

                    public void run() {
                        this.emit("responseHeaders", multimap);
                        this.onOpen();
                    }
                });
            }

            @Override // okhttp3.WebSocketListener
            public void onMessage(okhttp3.WebSocket webSocket, final String str) {
                if (str != null) {
                    EventThread.exec(new Runnable() {
                        /* class io.socket.engineio.client.transports.WebSocket.AnonymousClass2.AnonymousClass2 */

                        public void run() {
                            this.onData((WebSocket) str);
                        }
                    });
                }
            }

            @Override // okhttp3.WebSocketListener
            public void onMessage(okhttp3.WebSocket webSocket, final ByteString byteString) {
                if (byteString != null) {
                    EventThread.exec(new Runnable() {
                        /* class io.socket.engineio.client.transports.WebSocket.AnonymousClass2.AnonymousClass3 */

                        public void run() {
                            this.onData((WebSocket) byteString.toByteArray());
                        }
                    });
                }
            }

            @Override // okhttp3.WebSocketListener
            public void onClosed(okhttp3.WebSocket webSocket, int i, String str) {
                EventThread.exec(new Runnable() {
                    /* class io.socket.engineio.client.transports.WebSocket.AnonymousClass2.AnonymousClass4 */

                    public void run() {
                        this.onClose();
                    }
                });
            }

            @Override // okhttp3.WebSocketListener
            public void onFailure(okhttp3.WebSocket webSocket, final Throwable th, Response response) {
                if (th instanceof Exception) {
                    EventThread.exec(new Runnable() {
                        /* class io.socket.engineio.client.transports.WebSocket.AnonymousClass2.AnonymousClass5 */

                        public void run() {
                            this.onError("websocket error", (Exception) th);
                        }
                    });
                }
            }
        });
        build2.dispatcher().executorService().shutdown();
    }

    /* access modifiers changed from: protected */
    @Override // io.socket.engineio.client.Transport
    public void write(Packet[] packetArr) throws UTF8Exception {
        this.writable = false;
        final AnonymousClass3 r1 = new Runnable() {
            /* class io.socket.engineio.client.transports.WebSocket.AnonymousClass3 */

            public void run() {
                EventThread.nextTick(new Runnable() {
                    /* class io.socket.engineio.client.transports.WebSocket.AnonymousClass3.AnonymousClass1 */

                    public void run() {
                        this.writable = true;
                        this.emit("drain", new Object[0]);
                    }
                });
            }
        };
        final int[] iArr = {packetArr.length};
        for (Packet packet : packetArr) {
            if (this.readyState == Transport.ReadyState.OPENING || this.readyState == Transport.ReadyState.OPEN) {
                Parser.encodePacket(packet, new Parser.EncodeCallback() {
                    /* class io.socket.engineio.client.transports.WebSocket.AnonymousClass4 */

                    @Override // io.socket.engineio.parser.Parser.EncodeCallback
                    public void call(Object obj) {
                        try {
                            if (obj instanceof String) {
                                this.ws.send((String) obj);
                            } else if (obj instanceof byte[]) {
                                this.ws.send(ByteString.of((byte[]) obj));
                            }
                        } catch (IllegalStateException unused) {
                            WebSocket.logger.fine("websocket closed before we could write");
                        }
                        int[] iArr = iArr;
                        int i = iArr[0] - 1;
                        iArr[0] = i;
                        if (i == 0) {
                            r1.run();
                        }
                    }
                });
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    @Override // io.socket.engineio.client.Transport
    public void doClose() {
        okhttp3.WebSocket webSocket = this.ws;
        if (webSocket != null) {
            try {
                webSocket.close(1000, "");
            } catch (IllegalStateException unused) {
            }
        }
        okhttp3.WebSocket webSocket2 = this.ws;
        if (webSocket2 != null) {
            webSocket2.cancel();
        }
    }

    /* access modifiers changed from: protected */
    public String uri() {
        String str;
        String str2;
        Map map = this.query;
        if (map == null) {
            map = new HashMap();
        }
        String str3 = this.secure ? "wss" : "ws";
        if (this.port <= 0 || ((!"wss".equals(str3) || this.port == 443) && (!"ws".equals(str3) || this.port == 80))) {
            str = "";
        } else {
            str = ":" + this.port;
        }
        if (this.timestampRequests) {
            map.put(this.timestampParam, Yeast.yeast());
        }
        String encode = ParseQS.encode(map);
        if (encode.length() > 0) {
            encode = "?" + encode;
        }
        boolean contains = this.hostname.contains(":");
        StringBuilder sb = new StringBuilder();
        sb.append(str3);
        sb.append("://");
        if (contains) {
            str2 = "[" + this.hostname + "]";
        } else {
            str2 = this.hostname;
        }
        sb.append(str2);
        sb.append(str);
        sb.append(this.path);
        sb.append(encode);
        return sb.toString();
    }
}
