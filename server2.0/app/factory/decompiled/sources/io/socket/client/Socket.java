package io.socket.client;

import io.socket.client.Manager;
import io.socket.client.On;
import io.socket.emitter.Emitter;
import io.socket.hasbinary.HasBinary;
import io.socket.parser.Packet;
import io.socket.thread.EventThread;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Socket extends Emitter {
    public static final String EVENT_CONNECT = "connect";
    public static final String EVENT_CONNECTING = "connecting";
    public static final String EVENT_CONNECT_ERROR = "connect_error";
    public static final String EVENT_CONNECT_TIMEOUT = "connect_timeout";
    public static final String EVENT_DISCONNECT = "disconnect";
    public static final String EVENT_ERROR = "error";
    public static final String EVENT_MESSAGE = "message";
    public static final String EVENT_PING = "ping";
    public static final String EVENT_PONG = "pong";
    public static final String EVENT_RECONNECT = "reconnect";
    public static final String EVENT_RECONNECTING = "reconnecting";
    public static final String EVENT_RECONNECT_ATTEMPT = "reconnect_attempt";
    public static final String EVENT_RECONNECT_ERROR = "reconnect_error";
    public static final String EVENT_RECONNECT_FAILED = "reconnect_failed";
    protected static Map<String, Integer> events = new HashMap<String, Integer>() {
        /* class io.socket.client.Socket.AnonymousClass1 */

        {
            put(Socket.EVENT_CONNECT, 1);
            put("connect_error", 1);
            put("connect_timeout", 1);
            put(Socket.EVENT_CONNECTING, 1);
            put(Socket.EVENT_DISCONNECT, 1);
            put("error", 1);
            put("reconnect", 1);
            put("reconnect_attempt", 1);
            put("reconnect_failed", 1);
            put("reconnect_error", 1);
            put("reconnecting", 1);
            put("ping", 1);
            put("pong", 1);
        }
    };
    private static final Logger logger = Logger.getLogger(Socket.class.getName());
    private Map<Integer, Ack> acks = new HashMap();
    private volatile boolean connected;
    String id;
    private int ids;

    /* renamed from: io  reason: collision with root package name */
    private Manager f0io;
    private String nsp;
    private final Queue<List<Object>> receiveBuffer = new LinkedList();
    private final Queue<Packet<JSONArray>> sendBuffer = new LinkedList();
    private Queue<On.Handle> subs;

    static /* synthetic */ int access$708(Socket socket) {
        int i = socket.ids;
        socket.ids = i + 1;
        return i;
    }

    public Socket(Manager manager, String str) {
        this.f0io = manager;
        this.nsp = str;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void subEvents() {
        if (this.subs == null) {
            this.subs = new LinkedList<On.Handle>(this.f0io) {
                /* class io.socket.client.Socket.AnonymousClass2 */
                final /* synthetic */ Manager val$io;

                {
                    this.val$io = r3;
                    add(On.on(r3, "open", new Emitter.Listener() {
                        /* class io.socket.client.Socket.AnonymousClass2.AnonymousClass1 */

                        @Override // io.socket.emitter.Emitter.Listener
                        public void call(Object... objArr) {
                            Socket.this.onopen();
                        }
                    }));
                    add(On.on(r3, "packet", new Emitter.Listener() {
                        /* class io.socket.client.Socket.AnonymousClass2.AnonymousClass2 */

                        @Override // io.socket.emitter.Emitter.Listener
                        public void call(Object... objArr) {
                            Socket.this.onpacket((Packet) objArr[0]);
                        }
                    }));
                    add(On.on(r3, "close", new Emitter.Listener() {
                        /* class io.socket.client.Socket.AnonymousClass2.AnonymousClass3 */

                        @Override // io.socket.emitter.Emitter.Listener
                        public void call(Object... objArr) {
                            Socket.this.onclose(objArr.length > 0 ? (String) objArr[0] : null);
                        }
                    }));
                }
            };
        }
    }

    public Socket open() {
        EventThread.exec(new Runnable() {
            /* class io.socket.client.Socket.AnonymousClass3 */

            public void run() {
                if (!Socket.this.connected) {
                    Socket.this.subEvents();
                    Socket.this.f0io.open();
                    if (Manager.ReadyState.OPEN == Socket.this.f0io.readyState) {
                        Socket.this.onopen();
                    }
                    Socket.this.emit(Socket.EVENT_CONNECTING, new Object[0]);
                }
            }
        });
        return this;
    }

    public Socket connect() {
        return open();
    }

    public Socket send(final Object... objArr) {
        EventThread.exec(new Runnable() {
            /* class io.socket.client.Socket.AnonymousClass4 */

            public void run() {
                Socket.this.emit("message", objArr);
            }
        });
        return this;
    }

    @Override // io.socket.emitter.Emitter
    public Emitter emit(final String str, final Object... objArr) {
        EventThread.exec(new Runnable() {
            /* class io.socket.client.Socket.AnonymousClass5 */

            public void run() {
                if (Socket.events.containsKey(str)) {
                    Socket.super.emit(str, objArr);
                    return;
                }
                ArrayList<Object> arrayList = new ArrayList(objArr.length + 1);
                arrayList.add(str);
                arrayList.addAll(Arrays.asList(objArr));
                JSONArray jSONArray = new JSONArray();
                for (Object obj : arrayList) {
                    jSONArray.put(obj);
                }
                Packet packet = new Packet(HasBinary.hasBinary(jSONArray) ? 5 : 2, jSONArray);
                if (arrayList.get(arrayList.size() - 1) instanceof Ack) {
                    Socket.logger.fine(String.format("emitting packet with ack id %d", Integer.valueOf(Socket.this.ids)));
                    Socket.this.acks.put(Integer.valueOf(Socket.this.ids), (Ack) arrayList.remove(arrayList.size() - 1));
                    packet.data = (T) Socket.remove(jSONArray, jSONArray.length() - 1);
                    packet.id = Socket.access$708(Socket.this);
                }
                if (Socket.this.connected) {
                    Socket.this.packet(packet);
                } else {
                    Socket.this.sendBuffer.add(packet);
                }
            }
        });
        return this;
    }

    /* access modifiers changed from: private */
    public static JSONArray remove(JSONArray jSONArray, int i) {
        Object obj;
        JSONArray jSONArray2 = new JSONArray();
        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
            if (i2 != i) {
                try {
                    obj = jSONArray.get(i2);
                } catch (JSONException unused) {
                    obj = null;
                }
                jSONArray2.put(obj);
            }
        }
        return jSONArray2;
    }

    public Emitter emit(final String str, final Object[] objArr, final Ack ack) {
        EventThread.exec(new Runnable() {
            /* class io.socket.client.Socket.AnonymousClass6 */

            public void run() {
                AnonymousClass1<Object> r0 = new ArrayList<Object>() {
                    /* class io.socket.client.Socket.AnonymousClass6.AnonymousClass1 */

                    {
                        add(str);
                        if (objArr != null) {
                            addAll(Arrays.asList(objArr));
                        }
                    }
                };
                JSONArray jSONArray = new JSONArray();
                for (Object obj : r0) {
                    jSONArray.put(obj);
                }
                Packet packet = new Packet(HasBinary.hasBinary(jSONArray) ? 5 : 2, jSONArray);
                Socket.logger.fine(String.format("emitting packet with ack id %d", Integer.valueOf(Socket.this.ids)));
                Socket.this.acks.put(Integer.valueOf(Socket.this.ids), ack);
                packet.id = Socket.access$708(Socket.this);
                Socket.this.packet(packet);
            }
        });
        return this;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void packet(Packet packet) {
        packet.nsp = this.nsp;
        this.f0io.packet(packet);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onopen() {
        logger.fine("transport is open - connecting");
        if (!"/".equals(this.nsp)) {
            packet(new Packet(0));
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onclose(String str) {
        logger.fine(String.format("close (%s)", str));
        this.connected = false;
        this.id = null;
        emit(EVENT_DISCONNECT, str);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onpacket(Packet<?> packet) {
        if (this.nsp.equals(packet.nsp)) {
            switch (packet.type) {
                case 0:
                    onconnect();
                    return;
                case 1:
                    ondisconnect();
                    return;
                case 2:
                    onevent(packet);
                    return;
                case 3:
                    onack(packet);
                    return;
                case 4:
                    emit("error", packet.data);
                    return;
                case 5:
                    onevent(packet);
                    return;
                case 6:
                    onack(packet);
                    return;
                default:
                    return;
            }
        }
    }

    private void onevent(Packet<JSONArray> packet) {
        ArrayList arrayList = new ArrayList(Arrays.asList(toArray(packet.data)));
        Logger logger2 = logger;
        logger2.fine(String.format("emitting event %s", arrayList));
        if (packet.id >= 0) {
            logger2.fine("attaching ack callback to event");
            arrayList.add(ack(packet.id));
        }
        if (!this.connected) {
            this.receiveBuffer.add(arrayList);
        } else if (!arrayList.isEmpty()) {
            super.emit(arrayList.remove(0).toString(), arrayList.toArray());
        }
    }

    private Ack ack(final int i) {
        final boolean[] zArr = {false};
        return new Ack() {
            /* class io.socket.client.Socket.AnonymousClass7 */

            @Override // io.socket.client.Ack
            public void call(final Object... objArr) {
                EventThread.exec(new Runnable() {
                    /* class io.socket.client.Socket.AnonymousClass7.AnonymousClass1 */

                    public void run() {
                        if (!zArr[0]) {
                            zArr[0] = true;
                            Logger logger = Socket.logger;
                            Object[] objArr = objArr;
                            if (objArr.length == 0) {
                                objArr = null;
                            }
                            logger.fine(String.format("sending ack %s", objArr));
                            JSONArray jSONArray = new JSONArray();
                            for (Object obj : objArr) {
                                jSONArray.put(obj);
                            }
                            Packet packet = new Packet(HasBinary.hasBinary(jSONArray) ? 6 : 3, jSONArray);
                            packet.id = i;
                            this.packet(packet);
                        }
                    }
                });
            }
        };
    }

    private void onack(Packet<JSONArray> packet) {
        Ack remove = this.acks.remove(Integer.valueOf(packet.id));
        if (remove != null) {
            logger.fine(String.format("calling ack %s with %s", Integer.valueOf(packet.id), packet.data));
            remove.call(toArray(packet.data));
            return;
        }
        logger.fine(String.format("bad ack %s", Integer.valueOf(packet.id)));
    }

    private void onconnect() {
        this.connected = true;
        emit(EVENT_CONNECT, new Object[0]);
        emitBuffered();
    }

    private void emitBuffered() {
        while (true) {
            List<Object> poll = this.receiveBuffer.poll();
            if (poll == null) {
                break;
            }
            super.emit((String) poll.get(0), poll.toArray());
        }
        this.receiveBuffer.clear();
        while (true) {
            Packet<JSONArray> poll2 = this.sendBuffer.poll();
            if (poll2 != null) {
                packet(poll2);
            } else {
                this.sendBuffer.clear();
                return;
            }
        }
    }

    private void ondisconnect() {
        logger.fine(String.format("server disconnect (%s)", this.nsp));
        destroy();
        onclose("io server disconnect");
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void destroy() {
        Queue<On.Handle> queue = this.subs;
        if (queue != null) {
            for (On.Handle handle : queue) {
                handle.destroy();
            }
            this.subs = null;
        }
        this.f0io.destroy(this);
    }

    public Socket close() {
        EventThread.exec(new Runnable() {
            /* class io.socket.client.Socket.AnonymousClass8 */

            public void run() {
                if (Socket.this.connected) {
                    Socket.logger.fine(String.format("performing disconnect (%s)", Socket.this.nsp));
                    Socket.this.packet(new Packet(1));
                }
                Socket.this.destroy();
                if (Socket.this.connected) {
                    Socket.this.onclose("io client disconnect");
                }
            }
        });
        return this;
    }

    public Socket disconnect() {
        return close();
    }

    public Manager io() {
        return this.f0io;
    }

    public boolean connected() {
        return this.connected;
    }

    public String id() {
        return this.id;
    }

    private static Object[] toArray(JSONArray jSONArray) {
        Object obj;
        int length = jSONArray.length();
        Object[] objArr = new Object[length];
        for (int i = 0; i < length; i++) {
            Object obj2 = null;
            try {
                obj = jSONArray.get(i);
            } catch (JSONException e) {
                logger.log(Level.WARNING, "An error occured while retrieving data from JSONArray", (Throwable) e);
                obj = null;
            }
            if (!JSONObject.NULL.equals(obj)) {
                obj2 = obj;
            }
            objArr[i] = obj2;
        }
        return objArr;
    }
}
