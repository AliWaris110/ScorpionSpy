package io.socket.engineio.parser;

import java.nio.ByteBuffer;

/* access modifiers changed from: package-private */
/* compiled from: Parser */
public class Buffer {
    private Buffer() {
    }

    public static byte[] concat(byte[][] bArr) {
        int i = 0;
        for (byte[] bArr2 : bArr) {
            i += bArr2.length;
        }
        return concat(bArr, i);
    }

    public static byte[] concat(byte[][] bArr, int i) {
        if (bArr.length == 0) {
            return new byte[0];
        }
        if (bArr.length == 1) {
            return bArr[0];
        }
        ByteBuffer allocate = ByteBuffer.allocate(i);
        for (byte[] bArr2 : bArr) {
            allocate.put(bArr2);
        }
        return allocate.array();
    }
}
