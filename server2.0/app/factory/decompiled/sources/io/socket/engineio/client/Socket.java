package io.socket.engineio.client;

import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.PollingXHR;
import io.socket.engineio.client.transports.WebSocket;
import io.socket.engineio.parser.Packet;
import io.socket.parseqs.ParseQS;
import io.socket.thread.EventThread;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import org.json.JSONException;

public class Socket extends Emitter {
    public static final String EVENT_CLOSE = "close";
    public static final String EVENT_DATA = "data";
    public static final String EVENT_DRAIN = "drain";
    public static final String EVENT_ERROR = "error";
    public static final String EVENT_FLUSH = "flush";
    public static final String EVENT_HANDSHAKE = "handshake";
    public static final String EVENT_HEARTBEAT = "heartbeat";
    public static final String EVENT_MESSAGE = "message";
    public static final String EVENT_OPEN = "open";
    public static final String EVENT_PACKET = "packet";
    public static final String EVENT_PACKET_CREATE = "packetCreate";
    public static final String EVENT_PING = "ping";
    public static final String EVENT_PONG = "pong";
    public static final String EVENT_TRANSPORT = "transport";
    public static final String EVENT_UPGRADE = "upgrade";
    public static final String EVENT_UPGRADE_ERROR = "upgradeError";
    public static final String EVENT_UPGRADING = "upgrading";
    private static final String PROBE_ERROR = "probe error";
    public static final int PROTOCOL = 3;
    private static HostnameVerifier defaultHostnameVerifier;
    private static SSLContext defaultSSLContext;
    private static final Logger logger = Logger.getLogger(Socket.class.getName());
    private static boolean priorWebsocketSuccess = false;
    private ScheduledExecutorService heartbeatScheduler;
    String hostname;
    private HostnameVerifier hostnameVerifier;
    private String id;
    private final Emitter.Listener onHeartbeatAsListener;
    private String path;
    private long pingInterval;
    private Future pingIntervalTimer;
    private long pingTimeout;
    private Future pingTimeoutTimer;
    private int policyPort;
    int port;
    private int prevBufferLen;
    public Proxy proxy;
    public String proxyLogin;
    public String proxyPassword;
    private Map<String, String> query;
    private ReadyState readyState;
    private boolean rememberUpgrade;
    private boolean secure;
    private SSLContext sslContext;
    private String timestampParam;
    private boolean timestampRequests;
    Transport transport;
    private List<String> transports;
    private boolean upgrade;
    private List<String> upgrades;
    private boolean upgrading;
    LinkedList<Packet> writeBuffer;

    /* access modifiers changed from: private */
    public enum ReadyState {
        OPENING,
        OPEN,
        CLOSING,
        CLOSED;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public Socket() {
        this(new Options());
    }

    public Socket(String str) throws URISyntaxException {
        this(str, (Options) null);
    }

    public Socket(URI uri) {
        this(uri, (Options) null);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public Socket(String str, Options options) throws URISyntaxException {
        this(str == null ? null : new URI(str), options);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public Socket(URI uri, Options options) {
        this(uri != null ? Options.fromURI(uri, options) : options);
    }

    public Socket(Options options) {
        this.writeBuffer = new LinkedList<>();
        this.onHeartbeatAsListener = new Emitter.Listener() {
            /* class io.socket.engineio.client.Socket.AnonymousClass1 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                Socket.this.onHeartbeat(objArr.length > 0 ? ((Long) objArr[0]).longValue() : 0);
            }
        };
        if (options.host != null) {
            String str = options.host;
            if (str.split(":").length > 2) {
                int indexOf = str.indexOf(91);
                str = indexOf != -1 ? str.substring(indexOf + 1) : str;
                int lastIndexOf = str.lastIndexOf(93);
                if (lastIndexOf != -1) {
                    str = str.substring(0, lastIndexOf);
                }
            }
            options.hostname = str;
        }
        this.secure = options.secure;
        if (options.port == -1) {
            options.port = this.secure ? 443 : 80;
        }
        this.sslContext = options.sslContext != null ? options.sslContext : defaultSSLContext;
        this.hostname = options.hostname != null ? options.hostname : "localhost";
        this.port = options.port;
        this.query = options.query != null ? ParseQS.decode(options.query) : new HashMap<>();
        this.upgrade = options.upgrade;
        StringBuilder sb = new StringBuilder();
        sb.append((options.path != null ? options.path : "/engine.io").replaceAll("/$", ""));
        sb.append("/");
        this.path = sb.toString();
        this.timestampParam = options.timestampParam != null ? options.timestampParam : "t";
        this.timestampRequests = options.timestampRequests;
        this.transports = new ArrayList(Arrays.asList(options.transports != null ? options.transports : new String[]{Polling.NAME, WebSocket.NAME}));
        this.policyPort = options.policyPort != 0 ? options.policyPort : 843;
        this.rememberUpgrade = options.rememberUpgrade;
        this.hostnameVerifier = options.hostnameVerifier != null ? options.hostnameVerifier : defaultHostnameVerifier;
        this.proxy = options.proxy;
        this.proxyLogin = options.proxyLogin;
        this.proxyPassword = options.proxyPassword;
    }

    public static void setDefaultSSLContext(SSLContext sSLContext) {
        defaultSSLContext = sSLContext;
    }

    public static void setDefaultHostnameVerifier(HostnameVerifier hostnameVerifier2) {
        defaultHostnameVerifier = hostnameVerifier2;
    }

    public Socket open() {
        EventThread.exec(new Runnable() {
            /* class io.socket.engineio.client.Socket.AnonymousClass2 */

            public void run() {
                boolean z = Socket.this.rememberUpgrade;
                String str = WebSocket.NAME;
                if (!z || !Socket.priorWebsocketSuccess || !Socket.this.transports.contains(str)) {
                    if (Socket.this.transports.size() == 0) {
                        final Socket socket = Socket.this;
                        EventThread.nextTick(new Runnable() {
                            /* class io.socket.engineio.client.Socket.AnonymousClass2.AnonymousClass1 */

                            public void run() {
                                socket.emit("error", new EngineIOException("No transports available"));
                            }
                        });
                        return;
                    }
                    str = (String) Socket.this.transports.get(0);
                }
                Socket.this.readyState = ReadyState.OPENING;
                Transport createTransport = Socket.this.createTransport(str);
                Socket.this.setTransport(createTransport);
                createTransport.open();
            }
        });
        return this;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private Transport createTransport(String str) {
        Transport transport2;
        logger.fine(String.format("creating transport '%s'", str));
        HashMap hashMap = new HashMap(this.query);
        hashMap.put("EIO", String.valueOf(3));
        hashMap.put("transport", str);
        String str2 = this.id;
        if (str2 != null) {
            hashMap.put("sid", str2);
        }
        Transport.Options options = new Transport.Options();
        options.sslContext = this.sslContext;
        options.hostname = this.hostname;
        options.port = this.port;
        options.secure = this.secure;
        options.path = this.path;
        options.query = hashMap;
        options.timestampRequests = this.timestampRequests;
        options.timestampParam = this.timestampParam;
        options.policyPort = this.policyPort;
        options.socket = this;
        options.hostnameVerifier = this.hostnameVerifier;
        options.proxy = this.proxy;
        options.proxyLogin = this.proxyLogin;
        options.proxyPassword = this.proxyPassword;
        if (WebSocket.NAME.equals(str)) {
            transport2 = new WebSocket(options);
        } else if (Polling.NAME.equals(str)) {
            transport2 = new PollingXHR(options);
        } else {
            throw new RuntimeException();
        }
        emit("transport", transport2);
        return transport2;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setTransport(Transport transport2) {
        Logger logger2 = logger;
        logger2.fine(String.format("setting transport %s", transport2.name));
        Transport transport3 = this.transport;
        if (transport3 != null) {
            logger2.fine(String.format("clearing existing transport %s", transport3.name));
            this.transport.off();
        }
        this.transport = transport2;
        transport2.on("drain", new Emitter.Listener() {
            /* class io.socket.engineio.client.Socket.AnonymousClass6 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                this.onDrain();
            }
        }).on("packet", new Emitter.Listener() {
            /* class io.socket.engineio.client.Socket.AnonymousClass5 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                this.onPacket(objArr.length > 0 ? (Packet) objArr[0] : null);
            }
        }).on("error", new Emitter.Listener() {
            /* class io.socket.engineio.client.Socket.AnonymousClass4 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                this.onError(objArr.length > 0 ? (Exception) objArr[0] : null);
            }
        }).on("close", new Emitter.Listener() {
            /* class io.socket.engineio.client.Socket.AnonymousClass3 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                this.onClose("transport close");
            }
        });
    }

    private void probe(final String str) {
        logger.fine(String.format("probing transport '%s'", str));
        final Transport[] transportArr = {createTransport(str)};
        final boolean[] zArr = {false};
        priorWebsocketSuccess = false;
        final AnonymousClass7 r13 = new Emitter.Listener() {
            /* class io.socket.engineio.client.Socket.AnonymousClass7 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                if (!zArr[0]) {
                    Socket.logger.fine(String.format("probe transport '%s' opened", str));
                    Packet packet = new Packet("ping", "probe");
                    transportArr[0].send(new Packet[]{packet});
                    transportArr[0].once("packet", new Emitter.Listener() {
                        /* class io.socket.engineio.client.Socket.AnonymousClass7.AnonymousClass1 */

                        @Override // io.socket.emitter.Emitter.Listener
                        public void call(Object... objArr) {
                            if (!zArr[0]) {
                                Packet packet = (Packet) objArr[0];
                                if (!"pong".equals(packet.type) || !"probe".equals(packet.data)) {
                                    Socket.logger.fine(String.format("probe transport '%s' failed", str));
                                    EngineIOException engineIOException = new EngineIOException(Socket.PROBE_ERROR);
                                    engineIOException.transport = transportArr[0].name;
                                    this.emit(Socket.EVENT_UPGRADE_ERROR, engineIOException);
                                    return;
                                }
                                Socket.logger.fine(String.format("probe transport '%s' pong", str));
                                this.upgrading = true;
                                this.emit(Socket.EVENT_UPGRADING, transportArr[0]);
                                if (transportArr[0] != null) {
                                    boolean unused = Socket.priorWebsocketSuccess = WebSocket.NAME.equals(transportArr[0].name);
                                    Socket.logger.fine(String.format("pausing current transport '%s'", this.transport.name));
                                    ((Polling) this.transport).pause(new Runnable() {
                                        /* class io.socket.engineio.client.Socket.AnonymousClass7.AnonymousClass1.AnonymousClass1 */

                                        public void run() {
                                            if (!zArr[0] && ReadyState.CLOSED != this.readyState) {
                                                Socket.logger.fine("changing transport and sending upgrade packet");
                                                r12[0].run();
                                                this.setTransport(transportArr[0]);
                                                Packet packet = new Packet("upgrade");
                                                transportArr[0].send(new Packet[]{packet});
                                                this.emit("upgrade", transportArr[0]);
                                                transportArr[0] = null;
                                                this.upgrading = false;
                                                this.flush();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        };
        final AnonymousClass8 r6 = new Emitter.Listener() {
            /* class io.socket.engineio.client.Socket.AnonymousClass8 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                boolean[] zArr = zArr;
                if (!zArr[0]) {
                    zArr[0] = true;
                    r12[0].run();
                    transportArr[0].close();
                    transportArr[0] = null;
                }
            }
        };
        final AnonymousClass9 r14 = new Emitter.Listener() {
            /* class io.socket.engineio.client.Socket.AnonymousClass9 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                EngineIOException engineIOException;
                Object obj = objArr[0];
                if (obj instanceof Exception) {
                    engineIOException = new EngineIOException(Socket.PROBE_ERROR, (Exception) obj);
                } else if (obj instanceof String) {
                    engineIOException = new EngineIOException("probe error: " + ((String) obj));
                } else {
                    engineIOException = new EngineIOException(Socket.PROBE_ERROR);
                }
                engineIOException.transport = transportArr[0].name;
                r6.call(new Object[0]);
                Socket.logger.fine(String.format("probe transport \"%s\" failed because of error: %s", str, obj));
                this.emit(Socket.EVENT_UPGRADE_ERROR, engineIOException);
            }
        };
        final AnonymousClass10 r15 = new Emitter.Listener() {
            /* class io.socket.engineio.client.Socket.AnonymousClass10 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                r14.call("transport closed");
            }
        };
        final AnonymousClass11 r8 = new Emitter.Listener() {
            /* class io.socket.engineio.client.Socket.AnonymousClass11 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                r14.call("socket closed");
            }
        };
        final AnonymousClass12 r7 = new Emitter.Listener() {
            /* class io.socket.engineio.client.Socket.AnonymousClass12 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                Transport transport = (Transport) objArr[0];
                if (transportArr[0] != null && !transport.name.equals(transportArr[0].name)) {
                    Socket.logger.fine(String.format("'%s' works - aborting '%s'", transport.name, transportArr[0].name));
                    r6.call(new Object[0]);
                }
            }
        };
        final Runnable[] runnableArr = {new Runnable() {
            /* class io.socket.engineio.client.Socket.AnonymousClass13 */

            public void run() {
                transportArr[0].off("open", r13);
                transportArr[0].off("error", r14);
                transportArr[0].off("close", r15);
                this.off("close", r8);
                this.off(Socket.EVENT_UPGRADING, r7);
            }
        }};
        transportArr[0].once("open", r13);
        transportArr[0].once("error", r14);
        transportArr[0].once("close", r15);
        once("close", r8);
        once(EVENT_UPGRADING, r7);
        transportArr[0].open();
    }

    private void onOpen() {
        Logger logger2 = logger;
        logger2.fine("socket open");
        this.readyState = ReadyState.OPEN;
        priorWebsocketSuccess = WebSocket.NAME.equals(this.transport.name);
        emit("open", new Object[0]);
        flush();
        if (this.readyState == ReadyState.OPEN && this.upgrade && (this.transport instanceof Polling)) {
            logger2.fine("starting upgrade probes");
            for (String str : this.upgrades) {
                probe(str);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onPacket(Packet packet) {
        if (this.readyState == ReadyState.OPENING || this.readyState == ReadyState.OPEN) {
            logger.fine(String.format("socket received: type '%s', data '%s'", packet.type, packet.data));
            emit("packet", packet);
            emit(EVENT_HEARTBEAT, new Object[0]);
            if ("open".equals(packet.type)) {
                try {
                    onHandshake(new HandshakeData((String) packet.data));
                } catch (JSONException e) {
                    emit("error", new EngineIOException(e));
                }
            } else if ("pong".equals(packet.type)) {
                setPing();
                emit("pong", new Object[0]);
            } else if ("error".equals(packet.type)) {
                EngineIOException engineIOException = new EngineIOException("server error");
                engineIOException.code = packet.data;
                onError(engineIOException);
            } else if ("message".equals(packet.type)) {
                emit("data", packet.data);
                emit("message", packet.data);
            }
        } else {
            logger.fine(String.format("packet received with socket readyState '%s'", this.readyState));
        }
    }

    private void onHandshake(HandshakeData handshakeData) {
        emit(EVENT_HANDSHAKE, handshakeData);
        this.id = handshakeData.sid;
        this.transport.query.put("sid", handshakeData.sid);
        this.upgrades = filterUpgrades(Arrays.asList(handshakeData.upgrades));
        this.pingInterval = handshakeData.pingInterval;
        this.pingTimeout = handshakeData.pingTimeout;
        onOpen();
        if (ReadyState.CLOSED != this.readyState) {
            setPing();
            off(EVENT_HEARTBEAT, this.onHeartbeatAsListener);
            on(EVENT_HEARTBEAT, this.onHeartbeatAsListener);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onHeartbeat(long j) {
        Future future = this.pingTimeoutTimer;
        if (future != null) {
            future.cancel(false);
        }
        if (j <= 0) {
            j = this.pingInterval + this.pingTimeout;
        }
        this.pingTimeoutTimer = getHeartbeatScheduler().schedule(new Runnable() {
            /* class io.socket.engineio.client.Socket.AnonymousClass14 */

            public void run() {
                EventThread.exec(new Runnable() {
                    /* class io.socket.engineio.client.Socket.AnonymousClass14.AnonymousClass1 */

                    public void run() {
                        if (this.readyState != ReadyState.CLOSED) {
                            this.onClose("ping timeout");
                        }
                    }
                });
            }
        }, j, TimeUnit.MILLISECONDS);
    }

    private void setPing() {
        Future future = this.pingIntervalTimer;
        if (future != null) {
            future.cancel(false);
        }
        this.pingIntervalTimer = getHeartbeatScheduler().schedule(new Runnable() {
            /* class io.socket.engineio.client.Socket.AnonymousClass15 */

            public void run() {
                EventThread.exec(new Runnable() {
                    /* class io.socket.engineio.client.Socket.AnonymousClass15.AnonymousClass1 */

                    public void run() {
                        Socket.logger.fine(String.format("writing ping packet - expecting pong within %sms", Long.valueOf(this.pingTimeout)));
                        this.ping();
                        this.onHeartbeat(this.pingTimeout);
                    }
                });
            }
        }, this.pingInterval, TimeUnit.MILLISECONDS);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void ping() {
        EventThread.exec(new Runnable() {
            /* class io.socket.engineio.client.Socket.AnonymousClass16 */

            public void run() {
                Socket.this.sendPacket((Socket) "ping", (String) new Runnable() {
                    /* class io.socket.engineio.client.Socket.AnonymousClass16.AnonymousClass1 */

                    public void run() {
                        Socket.this.emit("ping", new Object[0]);
                    }
                });
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onDrain() {
        for (int i = 0; i < this.prevBufferLen; i++) {
            this.writeBuffer.poll();
        }
        this.prevBufferLen = 0;
        if (this.writeBuffer.size() == 0) {
            emit("drain", new Object[0]);
        } else {
            flush();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void flush() {
        if (this.readyState != ReadyState.CLOSED && this.transport.writable && !this.upgrading && this.writeBuffer.size() != 0) {
            logger.fine(String.format("flushing %d packets in socket", Integer.valueOf(this.writeBuffer.size())));
            this.prevBufferLen = this.writeBuffer.size();
            Transport transport2 = this.transport;
            LinkedList<Packet> linkedList = this.writeBuffer;
            transport2.send((Packet[]) linkedList.toArray(new Packet[linkedList.size()]));
            emit(EVENT_FLUSH, new Object[0]);
        }
    }

    public void write(String str) {
        write(str, (Runnable) null);
    }

    public void write(String str, Runnable runnable) {
        send(str, runnable);
    }

    public void write(byte[] bArr) {
        write(bArr, (Runnable) null);
    }

    public void write(byte[] bArr, Runnable runnable) {
        send(bArr, runnable);
    }

    public void send(String str) {
        send(str, (Runnable) null);
    }

    public void send(byte[] bArr) {
        send(bArr, (Runnable) null);
    }

    public void send(final String str, final Runnable runnable) {
        EventThread.exec(new Runnable() {
            /* class io.socket.engineio.client.Socket.AnonymousClass17 */

            public void run() {
                Socket.this.sendPacket((Socket) "message", str, (String) runnable);
            }
        });
    }

    public void send(final byte[] bArr, final Runnable runnable) {
        EventThread.exec(new Runnable() {
            /* class io.socket.engineio.client.Socket.AnonymousClass18 */

            public void run() {
                Socket.this.sendPacket((Socket) "message", (String) bArr, (byte[]) runnable);
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void sendPacket(String str, Runnable runnable) {
        sendPacket(new Packet(str), runnable);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void sendPacket(String str, String str2, Runnable runnable) {
        sendPacket(new Packet(str, str2), runnable);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void sendPacket(String str, byte[] bArr, Runnable runnable) {
        sendPacket(new Packet(str, bArr), runnable);
    }

    private void sendPacket(Packet packet, final Runnable runnable) {
        if (ReadyState.CLOSING != this.readyState && ReadyState.CLOSED != this.readyState) {
            emit(EVENT_PACKET_CREATE, packet);
            this.writeBuffer.offer(packet);
            if (runnable != null) {
                once(EVENT_FLUSH, new Emitter.Listener() {
                    /* class io.socket.engineio.client.Socket.AnonymousClass19 */

                    @Override // io.socket.emitter.Emitter.Listener
                    public void call(Object... objArr) {
                        runnable.run();
                    }
                });
            }
            flush();
        }
    }

    public Socket close() {
        EventThread.exec(new Runnable() {
            /* class io.socket.engineio.client.Socket.AnonymousClass20 */

            public void run() {
                if (Socket.this.readyState == ReadyState.OPENING || Socket.this.readyState == ReadyState.OPEN) {
                    Socket.this.readyState = ReadyState.CLOSING;
                    final Socket socket = Socket.this;
                    final AnonymousClass1 r1 = new Runnable() {
                        /* class io.socket.engineio.client.Socket.AnonymousClass20.AnonymousClass1 */

                        public void run() {
                            socket.onClose("forced close");
                            Socket.logger.fine("socket closing - telling transport to close");
                            socket.transport.close();
                        }
                    };
                    final Emitter.Listener[] listenerArr = {new Emitter.Listener() {
                        /* class io.socket.engineio.client.Socket.AnonymousClass20.AnonymousClass2 */

                        @Override // io.socket.emitter.Emitter.Listener
                        public void call(Object... objArr) {
                            socket.off("upgrade", listenerArr[0]);
                            socket.off(Socket.EVENT_UPGRADE_ERROR, listenerArr[0]);
                            r1.run();
                        }
                    }};
                    final AnonymousClass3 r3 = new Runnable() {
                        /* class io.socket.engineio.client.Socket.AnonymousClass20.AnonymousClass3 */

                        public void run() {
                            socket.once("upgrade", listenerArr[0]);
                            socket.once(Socket.EVENT_UPGRADE_ERROR, listenerArr[0]);
                        }
                    };
                    if (Socket.this.writeBuffer.size() > 0) {
                        Socket.this.once("drain", new Emitter.Listener() {
                            /* class io.socket.engineio.client.Socket.AnonymousClass20.AnonymousClass4 */

                            @Override // io.socket.emitter.Emitter.Listener
                            public void call(Object... objArr) {
                                if (Socket.this.upgrading) {
                                    r3.run();
                                } else {
                                    r1.run();
                                }
                            }
                        });
                    } else if (Socket.this.upgrading) {
                        r3.run();
                    } else {
                        r1.run();
                    }
                }
            }
        });
        return this;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onError(Exception exc) {
        logger.fine(String.format("socket error %s", exc));
        priorWebsocketSuccess = false;
        emit("error", exc);
        onClose("transport error", exc);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onClose(String str) {
        onClose(str, null);
    }

    private void onClose(String str, Exception exc) {
        if (ReadyState.OPENING == this.readyState || ReadyState.OPEN == this.readyState || ReadyState.CLOSING == this.readyState) {
            logger.fine(String.format("socket close with reason: %s", str));
            Future future = this.pingIntervalTimer;
            if (future != null) {
                future.cancel(false);
            }
            Future future2 = this.pingTimeoutTimer;
            if (future2 != null) {
                future2.cancel(false);
            }
            ScheduledExecutorService scheduledExecutorService = this.heartbeatScheduler;
            if (scheduledExecutorService != null) {
                scheduledExecutorService.shutdown();
            }
            this.transport.off("close");
            this.transport.close();
            this.transport.off();
            this.readyState = ReadyState.CLOSED;
            this.id = null;
            emit("close", str, exc);
            this.writeBuffer.clear();
            this.prevBufferLen = 0;
        }
    }

    /* access modifiers changed from: package-private */
    public List<String> filterUpgrades(List<String> list) {
        ArrayList arrayList = new ArrayList();
        for (String str : list) {
            if (this.transports.contains(str)) {
                arrayList.add(str);
            }
        }
        return arrayList;
    }

    public String id() {
        return this.id;
    }

    private ScheduledExecutorService getHeartbeatScheduler() {
        ScheduledExecutorService scheduledExecutorService = this.heartbeatScheduler;
        if (scheduledExecutorService == null || scheduledExecutorService.isShutdown()) {
            this.heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
        }
        return this.heartbeatScheduler;
    }

    public static class Options extends Transport.Options {
        public String host;
        public String query;
        public boolean rememberUpgrade;
        public String[] transports;
        public boolean upgrade = true;

        /* access modifiers changed from: private */
        public static Options fromURI(URI uri, Options options) {
            if (options == null) {
                options = new Options();
            }
            options.host = uri.getHost();
            options.secure = "https".equals(uri.getScheme()) || "wss".equals(uri.getScheme());
            options.port = uri.getPort();
            String rawQuery = uri.getRawQuery();
            if (rawQuery != null) {
                options.query = rawQuery;
            }
            return options;
        }
    }
}
