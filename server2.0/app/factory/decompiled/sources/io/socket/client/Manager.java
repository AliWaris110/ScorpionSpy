package io.socket.client;

import androidx.appcompat.widget.ActivityChooserView;
import io.socket.backo.Backoff;
import io.socket.client.On;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Socket;
import io.socket.parser.Packet;
import io.socket.parser.Parser;
import io.socket.thread.EventThread;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

public class Manager extends Emitter {
    public static final String EVENT_CLOSE = "close";
    public static final String EVENT_CONNECT_ERROR = "connect_error";
    public static final String EVENT_CONNECT_TIMEOUT = "connect_timeout";
    public static final String EVENT_ERROR = "error";
    public static final String EVENT_OPEN = "open";
    public static final String EVENT_PACKET = "packet";
    public static final String EVENT_PING = "ping";
    public static final String EVENT_PONG = "pong";
    public static final String EVENT_RECONNECT = "reconnect";
    public static final String EVENT_RECONNECTING = "reconnecting";
    public static final String EVENT_RECONNECT_ATTEMPT = "reconnect_attempt";
    public static final String EVENT_RECONNECT_ERROR = "reconnect_error";
    public static final String EVENT_RECONNECT_FAILED = "reconnect_failed";
    public static final String EVENT_TRANSPORT = "transport";
    static HostnameVerifier defaultHostnameVerifier;
    static SSLContext defaultSSLContext;
    private static final Logger logger = Logger.getLogger(Manager.class.getName());
    private double _randomizationFactor;
    private boolean _reconnection;
    private int _reconnectionAttempts;
    private long _reconnectionDelay;
    private long _reconnectionDelayMax;
    private long _timeout;
    private Backoff backoff;
    private Set<Socket> connecting;
    private Parser.Decoder decoder;
    private Parser.Encoder encoder;
    private boolean encoding;
    Socket engine;
    private Date lastPing;
    ConcurrentHashMap<String, Socket> nsps;
    private Options opts;
    private List<Packet> packetBuffer;
    ReadyState readyState;
    private boolean reconnecting;
    private boolean skipReconnect;
    private Queue<On.Handle> subs;
    private URI uri;

    public interface OpenCallback {
        void call(Exception exc);
    }

    public static class Options extends Socket.Options {
        public double randomizationFactor;
        public boolean reconnection = true;
        public int reconnectionAttempts;
        public long reconnectionDelay;
        public long reconnectionDelayMax;
        public long timeout = 20000;
    }

    /* access modifiers changed from: package-private */
    public enum ReadyState {
        CLOSED,
        OPENING,
        OPEN
    }

    public Manager() {
        this(null, null);
    }

    public Manager(URI uri2) {
        this(uri2, null);
    }

    public Manager(Options options) {
        this(null, options);
    }

    public Manager(URI uri2, Options options) {
        this.connecting = new HashSet();
        options = options == null ? new Options() : options;
        if (options.path == null) {
            options.path = "/socket.io";
        }
        if (options.sslContext == null) {
            options.sslContext = defaultSSLContext;
        }
        if (options.hostnameVerifier == null) {
            options.hostnameVerifier = defaultHostnameVerifier;
        }
        this.opts = options;
        this.nsps = new ConcurrentHashMap<>();
        this.subs = new LinkedList();
        reconnection(options.reconnection);
        reconnectionAttempts(options.reconnectionAttempts != 0 ? options.reconnectionAttempts : ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
        reconnectionDelay(options.reconnectionDelay != 0 ? options.reconnectionDelay : 1000);
        reconnectionDelayMax(options.reconnectionDelayMax != 0 ? options.reconnectionDelayMax : 5000);
        randomizationFactor(options.randomizationFactor != 0.0d ? options.randomizationFactor : 0.5d);
        this.backoff = new Backoff().setMin(reconnectionDelay()).setMax(reconnectionDelayMax()).setJitter(randomizationFactor());
        timeout(options.timeout);
        this.readyState = ReadyState.CLOSED;
        this.uri = uri2;
        this.encoding = false;
        this.packetBuffer = new ArrayList();
        this.encoder = new Parser.Encoder();
        this.decoder = new Parser.Decoder();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void emitAll(String str, Object... objArr) {
        emit(str, objArr);
        for (Socket socket : this.nsps.values()) {
            socket.emit(str, objArr);
        }
    }

    private void updateSocketIds() {
        for (Socket socket : this.nsps.values()) {
            socket.id = this.engine.id();
        }
    }

    public boolean reconnection() {
        return this._reconnection;
    }

    public Manager reconnection(boolean z) {
        this._reconnection = z;
        return this;
    }

    public int reconnectionAttempts() {
        return this._reconnectionAttempts;
    }

    public Manager reconnectionAttempts(int i) {
        this._reconnectionAttempts = i;
        return this;
    }

    public final long reconnectionDelay() {
        return this._reconnectionDelay;
    }

    public Manager reconnectionDelay(long j) {
        this._reconnectionDelay = j;
        Backoff backoff2 = this.backoff;
        if (backoff2 != null) {
            backoff2.setMin(j);
        }
        return this;
    }

    public final double randomizationFactor() {
        return this._randomizationFactor;
    }

    public Manager randomizationFactor(double d) {
        this._randomizationFactor = d;
        Backoff backoff2 = this.backoff;
        if (backoff2 != null) {
            backoff2.setJitter(d);
        }
        return this;
    }

    public final long reconnectionDelayMax() {
        return this._reconnectionDelayMax;
    }

    public Manager reconnectionDelayMax(long j) {
        this._reconnectionDelayMax = j;
        Backoff backoff2 = this.backoff;
        if (backoff2 != null) {
            backoff2.setMax(j);
        }
        return this;
    }

    public long timeout() {
        return this._timeout;
    }

    public Manager timeout(long j) {
        this._timeout = j;
        return this;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void maybeReconnectOnOpen() {
        if (!this.reconnecting && this._reconnection && this.backoff.getAttempts() == 0) {
            reconnect();
        }
    }

    public Manager open() {
        return open(null);
    }

    public Manager open(final OpenCallback openCallback) {
        EventThread.exec(new Runnable() {
            /* class io.socket.client.Manager.AnonymousClass1 */

            public void run() {
                Manager.logger.fine(String.format("readyState %s", Manager.this.readyState));
                if (Manager.this.readyState != ReadyState.OPEN && Manager.this.readyState != ReadyState.OPENING) {
                    Manager.logger.fine(String.format("opening %s", Manager.this.uri));
                    Manager manager = Manager.this;
                    manager.engine = new Engine(manager.uri, Manager.this.opts);
                    final Socket socket = Manager.this.engine;
                    final Manager manager2 = Manager.this;
                    manager2.readyState = ReadyState.OPENING;
                    Manager.this.skipReconnect = false;
                    socket.on("transport", new Emitter.Listener() {
                        /* class io.socket.client.Manager.AnonymousClass1.AnonymousClass1 */

                        @Override // io.socket.emitter.Emitter.Listener
                        public void call(Object... objArr) {
                            manager2.emit("transport", objArr);
                        }
                    });
                    final On.Handle on = On.on(socket, "open", new Emitter.Listener() {
                        /* class io.socket.client.Manager.AnonymousClass1.AnonymousClass2 */

                        @Override // io.socket.emitter.Emitter.Listener
                        public void call(Object... objArr) {
                            manager2.onopen();
                            if (openCallback != null) {
                                openCallback.call(null);
                            }
                        }
                    });
                    On.Handle on2 = On.on(socket, "error", new Emitter.Listener() {
                        /* class io.socket.client.Manager.AnonymousClass1.AnonymousClass3 */

                        @Override // io.socket.emitter.Emitter.Listener
                        public void call(Object... objArr) {
                            Exception exc = null;
                            Exception exc2 = objArr.length > 0 ? objArr[0] : null;
                            Manager.logger.fine("connect_error");
                            manager2.cleanup();
                            manager2.readyState = ReadyState.CLOSED;
                            manager2.emitAll("connect_error", new Object[]{exc2});
                            if (openCallback != null) {
                                if (exc2 instanceof Exception) {
                                    exc = exc2;
                                }
                                openCallback.call(new SocketIOException("Connection error", exc));
                                return;
                            }
                            manager2.maybeReconnectOnOpen();
                        }
                    });
                    if (Manager.this._timeout >= 0) {
                        final long j = Manager.this._timeout;
                        Manager.logger.fine(String.format("connection attempt will timeout after %d", Long.valueOf(j)));
                        final Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            /* class io.socket.client.Manager.AnonymousClass1.AnonymousClass4 */

                            public void run() {
                                EventThread.exec(new Runnable() {
                                    /* class io.socket.client.Manager.AnonymousClass1.AnonymousClass4.AnonymousClass1 */

                                    public void run() {
                                        Manager.logger.fine(String.format("connect attempt timed out after %d", Long.valueOf(j)));
                                        on.destroy();
                                        socket.close();
                                        socket.emit("error", new SocketIOException("timeout"));
                                        manager2.emitAll("connect_timeout", new Object[]{Long.valueOf(j)});
                                    }
                                });
                            }
                        }, j);
                        Manager.this.subs.add(new On.Handle() {
                            /* class io.socket.client.Manager.AnonymousClass1.AnonymousClass5 */

                            @Override // io.socket.client.On.Handle
                            public void destroy() {
                                timer.cancel();
                            }
                        });
                    }
                    Manager.this.subs.add(on);
                    Manager.this.subs.add(on2);
                    Manager.this.engine.open();
                }
            }
        });
        return this;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onopen() {
        logger.fine("open");
        cleanup();
        this.readyState = ReadyState.OPEN;
        emit("open", new Object[0]);
        Socket socket = this.engine;
        this.subs.add(On.on(socket, "data", new Emitter.Listener() {
            /* class io.socket.client.Manager.AnonymousClass2 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                Object obj = objArr[0];
                if (obj instanceof String) {
                    Manager.this.ondata((Manager) ((String) obj));
                } else if (obj instanceof byte[]) {
                    Manager.this.ondata((Manager) ((byte[]) obj));
                }
            }
        }));
        this.subs.add(On.on(socket, "ping", new Emitter.Listener() {
            /* class io.socket.client.Manager.AnonymousClass3 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                Manager.this.onping();
            }
        }));
        this.subs.add(On.on(socket, "pong", new Emitter.Listener() {
            /* class io.socket.client.Manager.AnonymousClass4 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                Manager.this.onpong();
            }
        }));
        this.subs.add(On.on(socket, "error", new Emitter.Listener() {
            /* class io.socket.client.Manager.AnonymousClass5 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                Manager.this.onerror((Exception) objArr[0]);
            }
        }));
        this.subs.add(On.on(socket, "close", new Emitter.Listener() {
            /* class io.socket.client.Manager.AnonymousClass6 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                Manager.this.onclose((String) objArr[0]);
            }
        }));
        this.subs.add(On.on(this.decoder, Parser.Decoder.EVENT_DECODED, new Emitter.Listener() {
            /* class io.socket.client.Manager.AnonymousClass7 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                Manager.this.ondecoded((Packet) objArr[0]);
            }
        }));
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onping() {
        this.lastPing = new Date();
        emitAll("ping", new Object[0]);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onpong() {
        Object[] objArr = new Object[1];
        objArr[0] = Long.valueOf(this.lastPing != null ? new Date().getTime() - this.lastPing.getTime() : 0);
        emitAll("pong", objArr);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void ondata(String str) {
        this.decoder.add(str);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void ondata(byte[] bArr) {
        this.decoder.add(bArr);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void ondecoded(Packet packet) {
        emit("packet", packet);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onerror(Exception exc) {
        logger.log(Level.FINE, "error", (Throwable) exc);
        emitAll("error", exc);
    }

    public Socket socket(String str) {
        Socket socket = this.nsps.get(str);
        if (socket != null) {
            return socket;
        }
        final Socket socket2 = new Socket(this, str);
        Socket putIfAbsent = this.nsps.putIfAbsent(str, socket2);
        if (putIfAbsent != null) {
            return putIfAbsent;
        }
        socket2.on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
            /* class io.socket.client.Manager.AnonymousClass8 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                this.connecting.add(socket2);
            }
        });
        socket2.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            /* class io.socket.client.Manager.AnonymousClass9 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                socket2.id = this.engine.id();
            }
        });
        return socket2;
    }

    /* access modifiers changed from: package-private */
    public void destroy(Socket socket) {
        this.connecting.remove(socket);
        if (this.connecting.isEmpty()) {
            close();
        }
    }

    /* access modifiers changed from: package-private */
    public void packet(Packet packet) {
        logger.fine(String.format("writing packet %s", packet));
        if (!this.encoding) {
            this.encoding = true;
            this.encoder.encode(packet, new Parser.Encoder.Callback() {
                /* class io.socket.client.Manager.AnonymousClass10 */

                @Override // io.socket.parser.Parser.Encoder.Callback
                public void call(Object[] objArr) {
                    for (Object obj : objArr) {
                        if (obj instanceof String) {
                            this.engine.write((String) obj);
                        } else if (obj instanceof byte[]) {
                            this.engine.write((byte[]) obj);
                        }
                    }
                    this.encoding = false;
                    this.processPacketQueue();
                }
            });
            return;
        }
        this.packetBuffer.add(packet);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void processPacketQueue() {
        if (!this.packetBuffer.isEmpty() && !this.encoding) {
            packet(this.packetBuffer.remove(0));
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void cleanup() {
        logger.fine("cleanup");
        while (true) {
            On.Handle poll = this.subs.poll();
            if (poll != null) {
                poll.destroy();
            } else {
                this.packetBuffer.clear();
                this.encoding = false;
                this.lastPing = null;
                this.decoder.destroy();
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void close() {
        logger.fine(Socket.EVENT_DISCONNECT);
        this.skipReconnect = true;
        this.reconnecting = false;
        if (this.readyState != ReadyState.OPEN) {
            cleanup();
        }
        this.backoff.reset();
        this.readyState = ReadyState.CLOSED;
        Socket socket = this.engine;
        if (socket != null) {
            socket.close();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onclose(String str) {
        logger.fine("onclose");
        cleanup();
        this.backoff.reset();
        this.readyState = ReadyState.CLOSED;
        emit("close", str);
        if (this._reconnection && !this.skipReconnect) {
            reconnect();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void reconnect() {
        if (!this.reconnecting && !this.skipReconnect) {
            if (this.backoff.getAttempts() >= this._reconnectionAttempts) {
                logger.fine("reconnect failed");
                this.backoff.reset();
                emitAll("reconnect_failed", new Object[0]);
                this.reconnecting = false;
                return;
            }
            long duration = this.backoff.duration();
            logger.fine(String.format("will wait %dms before reconnect attempt", Long.valueOf(duration)));
            this.reconnecting = true;
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                /* class io.socket.client.Manager.AnonymousClass11 */

                public void run() {
                    EventThread.exec(new Runnable() {
                        /* class io.socket.client.Manager.AnonymousClass11.AnonymousClass1 */

                        public void run() {
                            if (!this.skipReconnect) {
                                Manager.logger.fine("attempting reconnect");
                                int attempts = this.backoff.getAttempts();
                                this.emitAll("reconnect_attempt", new Object[]{Integer.valueOf(attempts)});
                                this.emitAll("reconnecting", new Object[]{Integer.valueOf(attempts)});
                                if (!this.skipReconnect) {
                                    this.open(new OpenCallback() {
                                        /* class io.socket.client.Manager.AnonymousClass11.AnonymousClass1.AnonymousClass1 */

                                        @Override // io.socket.client.Manager.OpenCallback
                                        public void call(Exception exc) {
                                            if (exc != null) {
                                                Manager.logger.fine("reconnect attempt error");
                                                this.reconnecting = false;
                                                this.reconnect();
                                                this.emitAll("reconnect_error", new Object[]{exc});
                                                return;
                                            }
                                            Manager.logger.fine("reconnect success");
                                            this.onreconnect();
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }, duration);
            this.subs.add(new On.Handle() {
                /* class io.socket.client.Manager.AnonymousClass12 */

                @Override // io.socket.client.On.Handle
                public void destroy() {
                    timer.cancel();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onreconnect() {
        int attempts = this.backoff.getAttempts();
        this.reconnecting = false;
        this.backoff.reset();
        updateSocketIds();
        emitAll("reconnect", Integer.valueOf(attempts));
    }

    private static class Engine extends Socket {
        Engine(URI uri, Socket.Options options) {
            super(uri, options);
        }
    }
}
