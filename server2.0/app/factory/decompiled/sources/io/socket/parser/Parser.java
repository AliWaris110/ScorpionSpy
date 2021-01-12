package io.socket.parser;

import io.socket.emitter.Emitter;
import io.socket.parser.Binary;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Parser {
    public static final int ACK = 3;
    public static final int BINARY_ACK = 6;
    public static final int BINARY_EVENT = 5;
    public static final int CONNECT = 0;
    public static final int DISCONNECT = 1;
    public static final int ERROR = 4;
    public static final int EVENT = 2;
    private static final Logger logger = Logger.getLogger(Parser.class.getName());
    public static int protocol = 4;
    public static String[] types = {"CONNECT", "DISCONNECT", "EVENT", "ACK", "ERROR", "BINARY_EVENT", "BINARY_ACK"};

    private Parser() {
    }

    /* access modifiers changed from: private */
    public static Packet<String> error() {
        return new Packet<>(4, "parser error");
    }

    public static class Encoder {

        public interface Callback {
            void call(Object[] objArr);
        }

        public void encode(Packet packet, Callback callback) {
            Parser.logger.fine(String.format("encoding packet %s", packet));
            if (5 == packet.type || 6 == packet.type) {
                encodeAsBinary(packet, callback);
                return;
            }
            callback.call(new String[]{encodeAsString(packet)});
        }

        private String encodeAsString(Packet packet) {
            boolean z;
            StringBuilder sb = new StringBuilder();
            sb.append(packet.type);
            if (5 == packet.type || 6 == packet.type) {
                sb.append(packet.attachments);
                sb.append("-");
            }
            if (packet.nsp == null || packet.nsp.length() == 0 || "/".equals(packet.nsp)) {
                z = false;
            } else {
                sb.append(packet.nsp);
                z = true;
            }
            if (packet.id >= 0) {
                if (z) {
                    sb.append(",");
                    z = false;
                }
                sb.append(packet.id);
            }
            if (packet.data != null) {
                if (z) {
                    sb.append(",");
                }
                sb.append((Object) packet.data);
            }
            Parser.logger.fine(String.format("encoded %s as %s", packet, sb));
            return sb.toString();
        }

        private void encodeAsBinary(Packet packet, Callback callback) {
            Binary.DeconstructedPacket deconstructPacket = Binary.deconstructPacket(packet);
            String encodeAsString = encodeAsString(deconstructPacket.packet);
            ArrayList arrayList = new ArrayList(Arrays.asList(deconstructPacket.buffers));
            arrayList.add(0, encodeAsString);
            callback.call(arrayList.toArray());
        }
    }

    public static class Decoder extends Emitter {
        public static String EVENT_DECODED = "decoded";
        BinaryReconstructor reconstructor = null;

        public void add(String str) {
            Packet decodeString = decodeString(str);
            if (5 == decodeString.type || 6 == decodeString.type) {
                BinaryReconstructor binaryReconstructor = new BinaryReconstructor(decodeString);
                this.reconstructor = binaryReconstructor;
                if (binaryReconstructor.reconPack.attachments == 0) {
                    emit(EVENT_DECODED, decodeString);
                    return;
                }
                return;
            }
            emit(EVENT_DECODED, decodeString);
        }

        public void add(byte[] bArr) {
            BinaryReconstructor binaryReconstructor = this.reconstructor;
            if (binaryReconstructor != null) {
                Packet takeBinaryData = binaryReconstructor.takeBinaryData(bArr);
                if (takeBinaryData != null) {
                    this.reconstructor = null;
                    emit(EVENT_DECODED, takeBinaryData);
                    return;
                }
                return;
            }
            throw new RuntimeException("got binary data when not reconstructing a packet");
        }

        private static Packet decodeString(String str) {
            int i;
            Packet packet = new Packet();
            int length = str.length();
            packet.type = Character.getNumericValue(str.charAt(0));
            if (packet.type < 0 || packet.type > Parser.types.length - 1) {
                return Parser.error();
            }
            if (5 != packet.type && 6 != packet.type) {
                i = 0;
            } else if (!str.contains("-") || length <= 1) {
                return Parser.error();
            } else {
                StringBuilder sb = new StringBuilder();
                i = 0;
                while (true) {
                    i++;
                    if (str.charAt(i) == '-') {
                        break;
                    }
                    sb.append(str.charAt(i));
                }
                packet.attachments = Integer.parseInt(sb.toString());
            }
            int i2 = i + 1;
            if (length <= i2 || '/' != str.charAt(i2)) {
                packet.nsp = "/";
            } else {
                StringBuilder sb2 = new StringBuilder();
                do {
                    i++;
                    char charAt = str.charAt(i);
                    if (',' == charAt) {
                        break;
                    }
                    sb2.append(charAt);
                } while (i + 1 != length);
                packet.nsp = sb2.toString();
            }
            int i3 = i + 1;
            if (length > i3 && Character.getNumericValue(Character.valueOf(str.charAt(i3)).charValue()) > -1) {
                StringBuilder sb3 = new StringBuilder();
                while (true) {
                    i++;
                    char charAt2 = str.charAt(i);
                    if (Character.getNumericValue(charAt2) < 0) {
                        i--;
                        break;
                    }
                    sb3.append(charAt2);
                    

                    /* access modifiers changed from: package-private */
                    public static class BinaryReconstructor {
                        List<byte[]> buffers = new ArrayList();
                        public Packet reconPack;

                        BinaryReconstructor(Packet packet) {
                            this.reconPack = packet;
                        }

                        public Packet takeBinaryData(byte[] bArr) {
                            this.buffers.add(bArr);
                            if (this.buffers.size() != this.reconPack.attachments) {
                                return null;
                            }
                            Packet packet = this.reconPack;
                            List<byte[]> list = this.buffers;
                            Packet reconstructPacket = Binary.reconstructPacket(packet, (byte[][]) list.toArray(new byte[list.size()][]));
                            finishReconstruction();
                            return reconstructPacket;
                        }

                        public void finishReconstruction() {
                            this.reconPack = null;
                            this.buffers = new ArrayList();
                        }
                    }
                }
