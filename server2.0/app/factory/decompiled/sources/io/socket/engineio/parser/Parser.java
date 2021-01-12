package io.socket.engineio.parser;

import androidx.appcompat.widget.ActivityChooserView;
import io.socket.utf8.UTF8;
import io.socket.utf8.UTF8Exception;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Parser {
    private static final int MAX_INT_CHAR_LENGTH = String.valueOf((int) ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED).length();
    public static final int PROTOCOL = 3;
    private static Packet<String> err = new Packet<>("error", "parser error");
    private static final Map<String, Integer> packets;
    private static final Map<Integer, String> packetslist = new HashMap();

    public interface DecodePayloadCallback<T> {
        boolean call(Packet<T> packet, int i, int i2);
    }

    public interface EncodeCallback<T> {
        void call(T t);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v1, resolved type: java.util.Map<java.lang.Integer, java.lang.String> */
    /* JADX WARN: Multi-variable type inference failed */
    static {
        AnonymousClass1 r0 = new HashMap<String, Integer>() {
            /* class io.socket.engineio.parser.Parser.AnonymousClass1 */

            {
                put("open", 0);
                put("close", 1);
                put("ping", 2);
                put("pong", 3);
                put("message", 4);
                put("upgrade", 5);
                put(Packet.NOOP, 6);
            }
        };
        packets = r0;
        for (Map.Entry entry : r0.entrySet()) {
            packetslist.put(entry.getValue(), entry.getKey());
        }
    }

    private Parser() {
    }

    public static void encodePacket(Packet packet, EncodeCallback encodeCallback) throws UTF8Exception {
        encodePacket(packet, false, encodeCallback);
    }

    public static void encodePacket(Packet packet, boolean z, EncodeCallback encodeCallback) throws UTF8Exception {
        if (packet.data instanceof byte[]) {
            encodeByteArray(packet, encodeCallback);
            return;
        }
        String valueOf = String.valueOf(packets.get(packet.type));
        if (packet.data != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(valueOf);
            String valueOf2 = String.valueOf(packet.data);
            if (z) {
                valueOf2 = UTF8.encode(valueOf2);
            }
            sb.append(valueOf2);
            valueOf = sb.toString();
        }
        encodeCallback.call(valueOf);
    }

    private static void encodeByteArray(Packet<byte[]> packet, EncodeCallback<byte[]> encodeCallback) {
        byte[] bArr = (byte[]) packet.data;
        byte[] bArr2 = new byte[(bArr.length + 1)];
        bArr2[0] = packets.get(packet.type).byteValue();
        System.arraycopy(bArr, 0, bArr2, 1, bArr.length);
        encodeCallback.call(bArr2);
    }

    public static Packet<String> decodePacket(String str) {
        return decodePacket(str, false);
    }

    public static Packet<String> decodePacket(String str, boolean z) {
        int i;
        try {
            i = Character.getNumericValue(str.charAt(0));
        } catch (IndexOutOfBoundsException unused) {
            i = -1;
        }
        if (z) {
            try {
                str = UTF8.decode(str);
            } catch (UTF8Exception unused2) {
                return err;
            }
        }
        if (i >= 0) {
            Map<Integer, String> map = packetslist;
            if (i < map.size()) {
                if (str.length() > 1) {
                    return new Packet<>(map.get(Integer.valueOf(i)), str.substring(1));
                }
                return new Packet<>(map.get(Integer.valueOf(i)));
            }
        }
        return err;
    }

    public static Packet<byte[]> decodePacket(byte[] bArr) {
        byte b = bArr[0];
        int length = bArr.length - 1;
        byte[] bArr2 = new byte[length];
        System.arraycopy(bArr, 1, bArr2, 0, length);
        return new Packet<>(packetslist.get(Integer.valueOf(b)), bArr2);
    }

    public static void encodePayload(Packet[] packetArr, EncodeCallback<byte[]> encodeCallback) throws UTF8Exception {
        if (packetArr.length == 0) {
            encodeCallback.call(new byte[0]);
            return;
        }
        final ArrayList arrayList = new ArrayList(packetArr.length);
        for (Packet packet : packetArr) {
            encodePacket(packet, true, new EncodeCallback() {
                /* class io.socket.engineio.parser.Parser.AnonymousClass2 */

                @Override // io.socket.engineio.parser.Parser.EncodeCallback
                public void call(Object obj) {
                    if (obj instanceof String) {
                        String str = (String) obj;
                        String valueOf = String.valueOf(str.length());
                        int length = valueOf.length() + 2;
                        byte[] bArr = new byte[length];
                        bArr[0] = 0;
                        int i = 0;
                        while (i < valueOf.length()) {
                            int i2 = i + 1;
                            bArr[i2] = (byte) Character.getNumericValue(valueOf.charAt(i));
                            i = i2;
                        }
                        bArr[length - 1] = -1;
                        arrayList.add(Buffer.concat(new byte[][]{bArr, Parser.stringToByteArray(str)}));
                        return;
                    }
                    byte[] bArr2 = (byte[]) obj;
                    String valueOf2 = String.valueOf(bArr2.length);
                    int length2 = valueOf2.length() + 2;
                    byte[] bArr3 = new byte[length2];
                    bArr3[0] = 1;
                    int i3 = 0;
                    while (i3 < valueOf2.length()) {
                        int i4 = i3 + 1;
                        bArr3[i4] = (byte) Character.getNumericValue(valueOf2.charAt(i3));
                        i3 = i4;
                    }
                    bArr3[length2 - 1] = -1;
                    arrayList.add(Buffer.concat(new byte[][]{bArr3, bArr2}));
                }
            });
        }
        encodeCallback.call(Buffer.concat((byte[][]) arrayList.toArray(new byte[arrayList.size()][])));
    }

    public static void decodePayload(String str, DecodePayloadCallback<String> decodePayloadCallback) {
        if (str == null || str.length() == 0) {
            decodePayloadCallback.call(err, 0, 1);
            return;
        }
        StringBuilder sb = new StringBuilder();
        int length = str.length();
        int i = 0;
        while (i < length) {
            char charAt = str.charAt(i);
            if (':' != charAt) {
                sb.append(charAt);
            } else {
                try {
                    int parseInt = Integer.parseInt(sb.toString());
                    int i2 = i + 1;
                    try {
                        String substring = str.substring(i2, i2 + parseInt);
                        if (substring.length() != 0) {
                            Packet<String> decodePacket = decodePacket(substring, true);
                            if (err.type.equals(decodePacket.type) && err.data.equals(decodePacket.data)) {
                                decodePayloadCallback.call(err, 0, 1);
                                return;
                            } else if (!decodePayloadCallback.call(decodePacket, i + parseInt, length)) {
                                return;
                            }
                        }
                        i += parseInt;
                        sb = new StringBuilder();
                    } catch (IndexOutOfBoundsException unused) {
                        decodePayloadCallback.call(err, 0, 1);
                        return;
                    }
                } catch (NumberFormatException unused2) {
                    decodePayloadCallback.call(err, 0, 1);
                    return;
                }
            }
            i++;
        }
        if (sb.length() > 0) {
            decodePayloadCallback.call(err, 0, 1);
        }
    }

    public static void decodePayload(byte[] bArr, DecodePayloadCallback decodePayloadCallback) {
        boolean z;
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        ArrayList arrayList = new ArrayList();
        while (true) {
            if (wrap.capacity() > 0) {
                StringBuilder sb = new StringBuilder();
                boolean z2 = (wrap.get(0) & 255) == 0;
                int i = 1;
                while (true) {
                    int i2 = wrap.get(i) & 255;
                    if (i2 == 255) {
                        z = false;
                        break;
                    } else if (sb.length() > MAX_INT_CHAR_LENGTH) {
                        z = true;
                        break;
                    } else {
                        sb.append(i2);
                        i++;
                    }
                }
                if (z) {
                    decodePayloadCallback.call(err, 0, 1);
                    return;
                }
                wrap.position(sb.length() + 1);
                ByteBuffer slice = wrap.slice();
                int parseInt = Integer.parseInt(sb.toString());
                slice.position(1);
                int i3 = parseInt + 1;
                slice.limit(i3);
                byte[] bArr2 = new byte[slice.remaining()];
                slice.get(bArr2);
                if (z2) {
                    arrayList.add(byteArrayToString(bArr2));
                } else {
                    arrayList.add(bArr2);
                }
                slice.clear();
                slice.position(i3);
                wrap = slice.slice();
            } else {
                int size = arrayList.size();
                for (int i4 = 0; i4 < size; i4++) {
                    Object obj = arrayList.get(i4);
                    if (obj instanceof String) {
                        decodePayloadCallback.call(decodePacket((String) obj, true), i4, size);
                    } else if (obj instanceof byte[]) {
                        decodePayloadCallback.call(decodePacket((byte[]) obj), i4, size);
                    }
                }
                return;
            }
        }
    }

    private static String byteArrayToString(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            sb.appendCodePoint(b & 255);
        }
        return sb.toString();
    }

    /* access modifiers changed from: private */
    public static byte[] stringToByteArray(String str) {
        int length = str.length();
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            bArr[i] = (byte) Character.codePointAt(str, i);
        }
        return bArr;
    }
}
