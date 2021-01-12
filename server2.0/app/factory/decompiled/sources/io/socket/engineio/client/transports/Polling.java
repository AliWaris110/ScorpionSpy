package io.socket.engineio.client.transports;

import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;
import io.socket.engineio.parser.Packet;
import io.socket.engineio.parser.Parser;
import io.socket.parseqs.ParseQS;
import io.socket.thread.EventThread;
import io.socket.utf8.UTF8Exception;
import io.socket.yeast.Yeast;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class Polling extends Transport {
    public static final String EVENT_POLL = "poll";
    public static final String EVENT_POLL_COMPLETE = "pollComplete";
    public static final String NAME = "polling";
    private static final Logger logger = Logger.getLogger(Polling.class.getName());
    private boolean polling;

    /* access modifiers changed from: protected */
    public abstract void doPoll();

    /* access modifiers changed from: protected */
    public abstract void doWrite(byte[] bArr, Runnable runnable);

    public Polling(Transport.Options options) {
        super(options);
        this.name = NAME;
    }

    /* access modifiers changed from: protected */
    @Override // io.socket.engineio.client.Transport
    public void doOpen() {
        poll();
    }

    public void pause(final Runnable runnable) {
        EventThread.exec(new Runnable() {
            /* class io.socket.engineio.client.transports.Polling.AnonymousClass1 */

            public void run() {
                final Polling polling = Polling.this;
                polling.readyState = Transport.ReadyState.PAUSED;
                final AnonymousClass1 r1 = new Runnable() {
                    /* class io.socket.engineio.client.transports.Polling.AnonymousClass1.AnonymousClass1 */

                    public void run() {
                        Polling.logger.fine("paused");
                        polling.readyState = Transport.ReadyState.PAUSED;
                        runnable.run();
                    }
                };
                if (Polling.this.polling || !Polling.this.writable) {
                    final int[] iArr = {0};
                    if (Polling.this.polling) {
                        Polling.logger.fine("we are currently polling - waiting to pause");
                        iArr[0] = iArr[0] + 1;
                        Polling.this.once(Polling.EVENT_POLL_COMPLETE, new Emitter.Listener() {
                            /* class io.socket.engineio.client.transports.Polling.AnonymousClass1.AnonymousClass2 */

                            @Override // io.socket.emitter.Emitter.Listener
                            public void call(Object... objArr) {
                                Polling.logger.fine("pre-pause polling complete");
                                int[] iArr = iArr;
                                int i = iArr[0] - 1;
                                iArr[0] = i;
                                if (i == 0) {
                                    r1.run();
                                }
                            }
                        });
                    }
                    if (!Polling.this.writable) {
                        Polling.logger.fine("we are currently writing - waiting to pause");
                        iArr[0] = iArr[0] + 1;
                        Polling.this.once("drain", new Emitter.Listener() {
                            /* class io.socket.engineio.client.transports.Polling.AnonymousClass1.AnonymousClass3 */

                            @Override // io.socket.emitter.Emitter.Listener
                            public void call(Object... objArr) {
                                Polling.logger.fine("pre-pause writing complete");
                                int[] iArr = iArr;
                                int i = iArr[0] - 1;
                                iArr[0] = i;
                                if (i == 0) {
                                    r1.run();
                                }
                            }
                        });
                        return;
                    }
                    return;
                }
                r1.run();
            }
        });
    }

    private void poll() {
        logger.fine(NAME);
        this.polling = true;
        doPoll();
        emit(EVENT_POLL, new Object[0]);
    }

    /* access modifiers changed from: protected */
    @Override // io.socket.engineio.client.Transport
    public void onData(String str) {
        _onData(str);
    }

    /* access modifiers changed from: protected */
    @Override // io.socket.engineio.client.Transport
    public void onData(byte[] bArr) {
        _onData(bArr);
    }

    private void _onData(Object obj) {
        Logger logger2 = logger;
        logger2.fine(String.format("polling got data %s", obj));
        AnonymousClass2 r2 = new Parser.DecodePayloadCallback() {
            /* class io.socket.engineio.client.transports.Polling.AnonymousClass2 */

            @Override // io.socket.engineio.parser.Parser.DecodePayloadCallback
            public boolean call(Packet packet, int i, int i2) {
                if (this.readyState == Transport.ReadyState.OPENING) {
                    this.onOpen();
                }
                if ("close".equals(packet.type)) {
                    this.onClose();
                    return false;
                }
                this.onPacket(packet);
                return true;
            }
        };
        if (obj instanceof String) {
            Parser.decodePayload((String) obj, r2);
        } else if (obj instanceof byte[]) {
            Parser.decodePayload((byte[]) obj, r2);
        }
        if (this.readyState != Transport.ReadyState.CLOSED) {
            this.polling = false;
            emit(EVENT_POLL_COMPLETE, new Object[0]);
            if (this.readyState == Transport.ReadyState.OPEN) {
                poll();
                return;
            }
            logger2.fine(String.format("ignoring poll - transport state '%s'", this.readyState));
        }
    }

    /* access modifiers changed from: protected */
    @Override // io.socket.engineio.client.Transport
    public void doClose() {
        AnonymousClass3 r0 = new Emitter.Listener() {
            /* class io.socket.engineio.client.transports.Polling.AnonymousClass3 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                Polling.logger.fine("writing close packet");
                try {
                    this.write(new Packet[]{new Packet("close")});
                } catch (UTF8Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        if (this.readyState == Transport.ReadyState.OPEN) {
            logger.fine("transport open - closing");
            r0.call(new Object[0]);
            return;
        }
        logger.fine("transport not open - deferring close");
        once("open", r0);
    }

    /* access modifiers changed from: protected */
    @Override // io.socket.engineio.client.Transport
    public void write(Packet[] packetArr) throws UTF8Exception {
        this.writable = false;
        final AnonymousClass4 r0 = new Runnable() {
            /* class io.socket.engineio.client.transports.Polling.AnonymousClass4 */

            public void run() {
                this.writable = true;
                this.emit("drain", new Object[0]);
            }
        };
        Parser.encodePayload(packetArr, new Parser.EncodeCallback<byte[]>() {
            /* class io.socket.engineio.client.transports.Polling.AnonymousClass5 */

            public void call(byte[] bArr) {
                this.doWrite(bArr, r0);
            }
        });
    }

    /* access modifiers changed from: protected */
    public String uri() {
        String str;
        String str2;
        Map map = this.query;
        if (map == null) {
            map = new HashMap();
        }
        String str3 = this.secure ? "https" : "http";
        if (this.timestampRequests) {
            map.put(this.timestampParam, Yeast.yeast());
        }
        String encode = ParseQS.encode(map);
        if (this.port <= 0 || ((!"https".equals(str3) || this.port == 443) && (!"http".equals(str3) || this.port == 80))) {
            str = "";
        } else {
            str = ":" + this.port;
        }
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
