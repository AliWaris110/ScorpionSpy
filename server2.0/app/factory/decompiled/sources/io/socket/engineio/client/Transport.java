package io.socket.engineio.client;

import io.socket.emitter.Emitter;
import io.socket.engineio.parser.Packet;
import io.socket.engineio.parser.Parser;
import io.socket.thread.EventThread;
import io.socket.utf8.UTF8Exception;
import java.net.Proxy;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

public abstract class Transport extends Emitter {
    public static final String EVENT_CLOSE = "close";
    public static final String EVENT_DRAIN = "drain";
    public static final String EVENT_ERROR = "error";
    public static final String EVENT_OPEN = "open";
    public static final String EVENT_PACKET = "packet";
    public static final String EVENT_REQUEST_HEADERS = "requestHeaders";
    public static final String EVENT_RESPONSE_HEADERS = "responseHeaders";
    protected String hostname;
    protected HostnameVerifier hostnameVerifier;
    public String name;
    protected String path;
    protected int port;
    protected Proxy proxy;
    protected String proxyLogin;
    protected String proxyPassword;
    public Map<String, String> query;
    protected ReadyState readyState;
    protected boolean secure;
    protected Socket socket;
    protected SSLContext sslContext;
    protected String timestampParam;
    protected boolean timestampRequests;
    public boolean writable;

    public static class Options {
        public String hostname;
        public HostnameVerifier hostnameVerifier;
        public String path;
        public int policyPort = -1;
        public int port = -1;
        public Proxy proxy;
        public String proxyLogin;
        public String proxyPassword;
        public Map<String, String> query;
        public boolean secure;
        protected Socket socket;
        public SSLContext sslContext;
        public String timestampParam;
        public boolean timestampRequests;
    }

    /* access modifiers changed from: protected */
    public abstract void doClose();

    /* access modifiers changed from: protected */
    public abstract void doOpen();

    /* access modifiers changed from: protected */
    public abstract void write(Packet[] packetArr) throws UTF8Exception;

    /* access modifiers changed from: protected */
    public enum ReadyState {
        OPENING,
        OPEN,
        CLOSED,
        PAUSED;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public Transport(Options options) {
        this.path = options.path;
        this.hostname = options.hostname;
        this.port = options.port;
        this.secure = options.secure;
        this.query = options.query;
        this.timestampParam = options.timestampParam;
        this.timestampRequests = options.timestampRequests;
        this.sslContext = options.sslContext;
        this.socket = options.socket;
        this.hostnameVerifier = options.hostnameVerifier;
        this.proxy = options.proxy;
        this.proxyLogin = options.proxyLogin;
        this.proxyPassword = options.proxyPassword;
    }

    /* access modifiers changed from: protected */
    public Transport onError(String str, Exception exc) {
        emit("error", new EngineIOException(str, exc));
        return this;
    }

    public Transport open() {
        EventThread.exec(new Runnable() {
            /* class io.socket.engineio.client.Transport.AnonymousClass1 */

            public void run() {
                if (Transport.this.readyState == ReadyState.CLOSED || Transport.this.readyState == null) {
                    Transport.this.readyState = ReadyState.OPENING;
                    Transport.this.doOpen();
                }
            }
        });
        return this;
    }

    public Transport close() {
        EventThread.exec(new Runnable() {
            /* class io.socket.engineio.client.Transport.AnonymousClass2 */

            public void run() {
                if (Transport.this.readyState == ReadyState.OPENING || Transport.this.readyState == ReadyState.OPEN) {
                    Transport.this.doClose();
                    Transport.this.onClose();
                }
            }
        });
        return this;
    }

    public void send(final Packet[] packetArr) {
        EventThread.exec(new Runnable() {
            /* class io.socket.engineio.client.Transport.AnonymousClass3 */

            public void run() {
                if (Transport.this.readyState == ReadyState.OPEN) {
                    try {
                        Transport.this.write(packetArr);
                    } catch (UTF8Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new RuntimeException("Transport not open");
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onOpen() {
        this.readyState = ReadyState.OPEN;
        this.writable = true;
        emit("open", new Object[0]);
    }

    /* access modifiers changed from: protected */
    public void onData(String str) {
        onPacket(Parser.decodePacket(str));
    }

    /* access modifiers changed from: protected */
    public void onData(byte[] bArr) {
        onPacket(Parser.decodePacket(bArr));
    }

    /* access modifiers changed from: protected */
    public void onPacket(Packet packet) {
        emit("packet", packet);
    }

    /* access modifiers changed from: protected */
    public void onClose() {
        this.readyState = ReadyState.CLOSED;
        emit("close", new Object[0]);
    }
}
