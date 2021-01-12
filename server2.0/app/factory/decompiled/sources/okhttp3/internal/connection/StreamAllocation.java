package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import okhttp3.Address;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http1.Http1Codec;
import okhttp3.internal.http2.ConnectionShutdownException;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.Http2Codec;
import okhttp3.internal.http2.StreamResetException;

public final class StreamAllocation {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public final Address address;
    private final Object callStackTrace;
    private boolean canceled;
    private HttpCodec codec;
    private RealConnection connection;
    private final ConnectionPool connectionPool;
    private int refusedStreamCount;
    private boolean released;
    private Route route;
    private final RouteSelector routeSelector;

    public StreamAllocation(ConnectionPool connectionPool2, Address address2, Object obj) {
        this.connectionPool = connectionPool2;
        this.address = address2;
        this.routeSelector = new RouteSelector(address2, routeDatabase());
        this.callStackTrace = obj;
    }

    public HttpCodec newStream(OkHttpClient okHttpClient, boolean z) {
        HttpCodec httpCodec;
        int connectTimeoutMillis = okHttpClient.connectTimeoutMillis();
        int readTimeoutMillis = okHttpClient.readTimeoutMillis();
        int writeTimeoutMillis = okHttpClient.writeTimeoutMillis();
        try {
            RealConnection findHealthyConnection = findHealthyConnection(connectTimeoutMillis, readTimeoutMillis, writeTimeoutMillis, okHttpClient.retryOnConnectionFailure(), z);
            if (findHealthyConnection.http2Connection != null) {
                httpCodec = new Http2Codec(okHttpClient, this, findHealthyConnection.http2Connection);
            } else {
                findHealthyConnection.socket().setSoTimeout(readTimeoutMillis);
                findHealthyConnection.source.timeout().timeout((long) readTimeoutMillis, TimeUnit.MILLISECONDS);
                findHealthyConnection.sink.timeout().timeout((long) writeTimeoutMillis, TimeUnit.MILLISECONDS);
                httpCodec = new Http1Codec(okHttpClient, this, findHealthyConnection.source, findHealthyConnection.sink);
            }
            synchronized (this.connectionPool) {
                this.codec = httpCodec;
            }
            return httpCodec;
        } catch (IOException e) {
            throw new RouteException(e);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0018, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0012, code lost:
        if (r0.isHealthy(r8) != false) goto L_0x0018;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private okhttp3.internal.connection.RealConnection findHealthyConnection(int r4, int r5, int r6, boolean r7, boolean r8) throws java.io.IOException {
        /*
            r3 = this;
        L_0x0000:
            okhttp3.internal.connection.RealConnection r0 = r3.findConnection(r4, r5, r6, r7)
            okhttp3.ConnectionPool r1 = r3.connectionPool
            monitor-enter(r1)
            int r2 = r0.successCount     // Catch:{ all -> 0x0019 }
            if (r2 != 0) goto L_0x000d
            monitor-exit(r1)     // Catch:{ all -> 0x0019 }
            return r0
        L_0x000d:
            monitor-exit(r1)     // Catch:{ all -> 0x0019 }
            boolean r1 = r0.isHealthy(r8)
            if (r1 != 0) goto L_0x0018
            r3.noNewStreams()
            goto L_0x0000
        L_0x0018:
            return r0
        L_0x0019:
            r4 = move-exception
            monitor-exit(r1)
            goto L_0x001d
        L_0x001c:
            throw r4
        L_0x001d:
            goto L_0x001c
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.StreamAllocation.findHealthyConnection(int, int, int, boolean, boolean):okhttp3.internal.connection.RealConnection");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x002c, code lost:
        if (r1 != null) goto L_0x0041;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x002e, code lost:
        r1 = r8.routeSelector.next();
        r0 = r8.connectionPool;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0036, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r8.route = r1;
        r8.refusedStreamCount = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x003c, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0041, code lost:
        r0 = new okhttp3.internal.connection.RealConnection(r1);
        r1 = r8.connectionPool;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0048, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        acquire(r0);
        okhttp3.internal.Internal.instance.put(r8.connectionPool, r0);
        r8.connection = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0057, code lost:
        if (r8.canceled != false) goto L_0x0074;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0059, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x005a, code lost:
        r0.connect(r9, r10, r11, r8.address.connectionSpecs(), r12);
        routeDatabase().connected(r0.route());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0073, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x007b, code lost:
        throw new java.io.IOException("Canceled");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private okhttp3.internal.connection.RealConnection findConnection(int r9, int r10, int r11, boolean r12) throws java.io.IOException {
        /*
        // Method dump skipped, instructions count: 154
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.StreamAllocation.findConnection(int, int, int, boolean):okhttp3.internal.connection.RealConnection");
    }

    public void streamFinished(boolean z, HttpCodec httpCodec) {
        synchronized (this.connectionPool) {
            if (httpCodec != null) {
                if (httpCodec == this.codec) {
                    if (!z) {
                        this.connection.successCount++;
                    }
                }
            }
            throw new IllegalStateException("expected " + this.codec + " but was " + httpCodec);
        }
        deallocate(z, false, true);
    }

    public HttpCodec codec() {
        HttpCodec httpCodec;
        synchronized (this.connectionPool) {
            httpCodec = this.codec;
        }
        return httpCodec;
    }

    private RouteDatabase routeDatabase() {
        return Internal.instance.routeDatabase(this.connectionPool);
    }

    public synchronized RealConnection connection() {
        return this.connection;
    }

    public void release() {
        deallocate(false, true, false);
    }

    public void noNewStreams() {
        deallocate(true, false, false);
    }

    private void deallocate(boolean z, boolean z2, boolean z3) {
        RealConnection realConnection;
        RealConnection realConnection2;
        synchronized (this.connectionPool) {
            realConnection = null;
            if (z3) {
                try {
                    this.codec = null;
                } catch (Throwable th) {
                    throw th;
                }
            }
            if (z2) {
                this.released = true;
            }
            RealConnection realConnection3 = this.connection;
            if (realConnection3 != null) {
                if (z) {
                    realConnection3.noNewStreams = true;
                }
                if (this.codec == null && (this.released || this.connection.noNewStreams)) {
                    release(this.connection);
                    if (this.connection.allocations.isEmpty()) {
                        this.connection.idleAtNanos = System.nanoTime();
                        if (Internal.instance.connectionBecameIdle(this.connectionPool, this.connection)) {
                            realConnection2 = this.connection;
                            this.connection = null;
                            realConnection = realConnection2;
                        }
                    }
                    realConnection2 = null;
                    this.connection = null;
                    realConnection = realConnection2;
                }
            }
        }
        if (realConnection != null) {
            Util.closeQuietly(realConnection.socket());
        }
    }

    public void cancel() {
        HttpCodec httpCodec;
        RealConnection realConnection;
        synchronized (this.connectionPool) {
            this.canceled = true;
            httpCodec = this.codec;
            realConnection = this.connection;
        }
        if (httpCodec != null) {
            httpCodec.cancel();
        } else if (realConnection != null) {
            realConnection.cancel();
        }
    }

    public void streamFailed(IOException iOException) {
        boolean z;
        synchronized (this.connectionPool) {
            if (iOException instanceof StreamResetException) {
                StreamResetException streamResetException = (StreamResetException) iOException;
                if (streamResetException.errorCode == ErrorCode.REFUSED_STREAM) {
                    this.refusedStreamCount++;
                }
                if (streamResetException.errorCode != ErrorCode.REFUSED_STREAM || this.refusedStreamCount > 1) {
                    this.route = null;
                }
                z = false;
            } else {
                RealConnection realConnection = this.connection;
                if ((realConnection != null && !realConnection.isMultiplexed()) || (iOException instanceof ConnectionShutdownException)) {
                    if (this.connection.successCount == 0) {
                        Route route2 = this.route;
                        if (!(route2 == null || iOException == null)) {
                            this.routeSelector.connectFailed(route2, iOException);
                        }
                        this.route = null;
                    }
                }
                z = false;
            }
            z = true;
        }
        deallocate(z, false, true);
    }

    public void acquire(RealConnection realConnection) {
        realConnection.allocations.add(new StreamAllocationReference(this, this.callStackTrace));
    }

    private void release(RealConnection realConnection) {
        int size = realConnection.allocations.size();
        for (int i = 0; i < size; i++) {
            if (realConnection.allocations.get(i).get() == this) {
                realConnection.allocations.remove(i);
                return;
            }
        }
        throw new IllegalStateException();
    }

    public boolean hasMoreRoutes() {
        return this.route != null || this.routeSelector.hasNext();
    }

    public String toString() {
        return this.address.toString();
    }

    public static final class StreamAllocationReference extends WeakReference<StreamAllocation> {
        public final Object callStackTrace;

        StreamAllocationReference(StreamAllocation streamAllocation, Object obj) {
            super(streamAllocation);
            this.callStackTrace = obj;
        }
    }
}
