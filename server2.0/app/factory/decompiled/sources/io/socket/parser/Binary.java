package io.socket.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Binary {
    private static final String KEY_NUM = "num";
    private static final String KEY_PLACEHOLDER = "_placeholder";
    private static final Logger logger = Logger.getLogger(Binary.class.getName());

    public static class DeconstructedPacket {
        public byte[][] buffers;
        public Packet packet;
    }

    public static DeconstructedPacket deconstructPacket(Packet packet) {
        ArrayList arrayList = new ArrayList();
        packet.data = (T) _deconstructPacket(packet.data, arrayList);
        packet.attachments = arrayList.size();
        DeconstructedPacket deconstructedPacket = new DeconstructedPacket();
        deconstructedPacket.packet = packet;
        deconstructedPacket.buffers = (byte[][]) arrayList.toArray(new byte[arrayList.size()][]);
        return deconstructedPacket;
    }

    private static Object _deconstructPacket(Object obj, List<byte[]> list) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof byte[]) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put(KEY_PLACEHOLDER, true);
                jSONObject.put(KEY_NUM, list.size());
                list.add((byte[]) obj);
                return jSONObject;
            } catch (JSONException e) {
                logger.log(Level.WARNING, "An error occured while putting data to JSONObject", (Throwable) e);
                return null;
            }
        } else if (obj instanceof JSONArray) {
            JSONArray jSONArray = new JSONArray();
            JSONArray jSONArray2 = (JSONArray) obj;
            int length = jSONArray2.length();
            for (int i = 0; i < length; i++) {
                try {
                    jSONArray.put(i, _deconstructPacket(jSONArray2.get(i), list));
                } catch (JSONException e2) {
                    logger.log(Level.WARNING, "An error occured while putting packet data to JSONObject", (Throwable) e2);
                    return null;
                }
            }
            return jSONArray;
        } else if (!(obj instanceof JSONObject)) {
            return obj;
        } else {
            JSONObject jSONObject2 = new JSONObject();
            JSONObject jSONObject3 = (JSONObject) obj;
            Iterator<String> keys = jSONObject3.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                try {
                    jSONObject2.put(next, _deconstructPacket(jSONObject3.get(next), list));
                } catch (JSONException e3) {
                    logger.log(Level.WARNING, "An error occured while putting data to JSONObject", (Throwable) e3);
                    return null;
                }
            }
            return jSONObject2;
        }
    }

    public static Packet reconstructPacket(Packet packet, byte[][] bArr) {
        packet.data = (T) _reconstructPacket(packet.data, bArr);
        packet.attachments = -1;
        return packet;
    }

    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:31:0x006c */
    /* JADX DEBUG: Multi-variable search result rejected for r4v0, resolved type: java.lang.Object */
    /* JADX DEBUG: Multi-variable search result rejected for r4v1, resolved type: java.lang.Object */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v2, types: [org.json.JSONObject] */
    private static Object _reconstructPacket(Object obj, byte[][] bArr) {
        if (obj instanceof JSONArray) {
            JSONArray jSONArray = (JSONArray) obj;
            int length = jSONArray.length();
            for (int i = 0; i < length; i++) {
                try {
                    jSONArray.put(i, _reconstructPacket(jSONArray.get(i), bArr));
                } catch (JSONException e) {
                    logger.log(Level.WARNING, "An error occured while putting packet data to JSONObject", (Throwable) e);
                    return null;
                }
            }
            return jSONArray;
        }
        if (obj instanceof JSONObject) {
            obj = (JSONObject) obj;
            if (obj.optBoolean(KEY_PLACEHOLDER)) {
                int optInt = obj.optInt(KEY_NUM, -1);
                if (optInt < 0 || optInt >= bArr.length) {
                    return null;
                }
                return bArr[optInt];
            }
            Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                try {
                    obj.put(next, _reconstructPacket(obj.get(next), bArr));
                } catch (JSONException e2) {
                    logger.log(Level.WARNING, "An error occured while putting data to JSONObject", (Throwable) e2);
                    return null;
                }
            }
        }
        return obj;
    }
}
