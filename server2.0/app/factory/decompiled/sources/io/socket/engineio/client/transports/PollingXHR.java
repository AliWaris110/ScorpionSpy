package io.socket.engineio.client.transports;

import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;
import io.socket.thread.EventThread;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class PollingXHR extends Polling {
    private static final Logger logger = Logger.getLogger(PollingXHR.class.getName());

    public PollingXHR(Transport.Options options) {
        super(options);
    }

    /* access modifiers changed from: protected */
    public Request request() {
        return request(null);
    }

    /* access modifiers changed from: protected */
    public Request request(Request.Options options) {
        if (options == null) {
            options = new Request.Options();
        }
        options.uri = uri();
        options.sslContext = this.sslContext;
        options.hostnameVerifier = this.hostnameVerifier;
        options.proxy = this.proxy;
        Request request = new Request(options);
        request.on("requestHeaders", new Emitter.Listener() {
            /* class io.socket.engineio.client.transports.PollingXHR.AnonymousClass2 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                this.emit("requestHeaders", objArr[0]);
            }
        }).on("responseHeaders", new Emitter.Listener() {
            /* class io.socket.engineio.client.transports.PollingXHR.AnonymousClass1 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(final Object... objArr) {
                EventThread.exec(new Runnable() {
                    /* class io.socket.engineio.client.transports.PollingXHR.AnonymousClass1.AnonymousClass1 */

                    public void run() {
                        this.emit("responseHeaders", objArr[0]);
                    }
                });
            }
        });
        return request;
    }

    /* access modifiers changed from: protected */
    @Override // io.socket.engineio.client.transports.Polling
    public void doWrite(byte[] bArr, final Runnable runnable) {
        Request.Options options = new Request.Options();
        options.method = "POST";
        options.data = bArr;
        Request request = request(options);
        request.on(Request.EVENT_SUCCESS, new Emitter.Listener() {
            /* class io.socket.engineio.client.transports.PollingXHR.AnonymousClass3 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(Object... objArr) {
                EventThread.exec(new Runnable() {
                    /* class io.socket.engineio.client.transports.PollingXHR.AnonymousClass3.AnonymousClass1 */

                    public void run() {
                        runnable.run();
                    }
                });
            }
        });
        request.on("error", new Emitter.Listener() {
            /* class io.socket.engineio.client.transports.PollingXHR.AnonymousClass4 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(final Object... objArr) {
                EventThread.exec(new Runnable() {
                    /* class io.socket.engineio.client.transports.PollingXHR.AnonymousClass4.AnonymousClass1 */

                    public void run() {
                        Object[] objArr = objArr;
                        this.onError("xhr post error", (objArr.length <= 0 || !(objArr[0] instanceof Exception)) ? null : (Exception) objArr[0]);
                    }
                });
            }
        });
        request.create();
    }

    /* access modifiers changed from: protected */
    @Override // io.socket.engineio.client.transports.Polling
    public void doPoll() {
        logger.fine("xhr poll");
        Request request = request();
        request.on("data", new Emitter.Listener() {
            /* class io.socket.engineio.client.transports.PollingXHR.AnonymousClass5 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(final Object... objArr) {
                EventThread.exec(new Runnable() {
                    /* class io.socket.engineio.client.transports.PollingXHR.AnonymousClass5.AnonymousClass1 */

                    public void run() {
                        Object[] objArr = objArr;
                        String str = objArr.length > 0 ? objArr[0] : null;
                        if (str instanceof String) {
                            this.onData(str);
                        } else if (str instanceof byte[]) {
                            this.onData((byte[]) str);
                        }
                    }
                });
            }
        });
        request.on("error", new Emitter.Listener() {
            /* class io.socket.engineio.client.transports.PollingXHR.AnonymousClass6 */

            @Override // io.socket.emitter.Emitter.Listener
            public void call(final Object... objArr) {
                EventThread.exec(new Runnable() {
                    /* class io.socket.engineio.client.transports.PollingXHR.AnonymousClass6.AnonymousClass1 */

                    public void run() {
                        Object[] objArr = objArr;
                        this.onError("xhr poll error", (objArr.length <= 0 || !(objArr[0] instanceof Exception)) ? null : (Exception) objArr[0]);
                    }
                });
            }
        });
        request.create();
    }

    public static class Request extends Emitter {
        public static final String EVENT_DATA = "data";
        public static final String EVENT_ERROR = "error";
        public static final String EVENT_REQUEST_HEADERS = "requestHeaders";
        public static final String EVENT_RESPONSE_HEADERS = "responseHeaders";
        public static final String EVENT_SUCCESS = "success";
        private byte[] data;
        private HostnameVerifier hostnameVerifier;
        private String method;
        private Proxy proxy;
        private SSLContext sslContext;
        private String uri;
        private HttpURLConnection xhr;

        public static class Options {
            public byte[] data;
            public HostnameVerifier hostnameVerifier;
            public String method;
            public Proxy proxy;
            public SSLContext sslContext;
            public String uri;
        }

        public Request(Options options) {
            this.method = options.method != null ? options.method : "GET";
            this.uri = options.uri;
            this.data = options.data;
            this.sslContext = options.sslContext;
            this.hostnameVerifier = options.hostnameVerifier;
            this.proxy = options.proxy;
        }

        public void create() {
            HttpURLConnection httpURLConnection;
            try {
                PollingXHR.logger.fine(String.format("xhr open %s: %s", this.method, this.uri));
                URL url = new URL(this.uri);
                Proxy proxy2 = this.proxy;
                if (proxy2 != null) {
                    httpURLConnection = (HttpURLConnection) url.openConnection(proxy2);
                } else {
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                }
                this.xhr = httpURLConnection;
                httpURLConnection.setRequestMethod(this.method);
                this.xhr.setConnectTimeout(10000);
                HttpURLConnection httpURLConnection2 = this.xhr;
                if (httpURLConnection2 instanceof HttpsURLConnection) {
                    SSLContext sSLContext = this.sslContext;
                    if (sSLContext != null) {
                        ((HttpsURLConnection) httpURLConnection2).setSSLSocketFactory(sSLContext.getSocketFactory());
                    }
                    HostnameVerifier hostnameVerifier2 = this.hostnameVerifier;
                    if (hostnameVerifier2 != null) {
                        ((HttpsURLConnection) this.xhr).setHostnameVerifier(hostnameVerifier2);
                    }
                }
                TreeMap treeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
                if ("POST".equals(this.method)) {
                    this.xhr.setDoOutput(true);
                    treeMap.put("Content-type", new LinkedList(Arrays.asList("application/octet-stream")));
                }
                onRequestHeaders(treeMap);
                for (Map.Entry<String, List<String>> entry : treeMap.entrySet()) {
                    for (String str : entry.getValue()) {
                        this.xhr.addRequestProperty(entry.getKey(), str);
                    }
                }
                PollingXHR.logger.fine(String.format("sending xhr with url %s | data %s", this.uri, this.data));
                new Thread(new Runnable() {
                    /* class io.socket.engineio.client.transports.PollingXHR.Request.AnonymousClass1 */

                    /* JADX WARNING: Removed duplicated region for block: B:32:0x0095 A[SYNTHETIC, Splitter:B:32:0x0095] */
                    /* JADX WARNING: Removed duplicated region for block: B:38:? A[RETURN, SYNTHETIC] */
                    /* JADX WARNING: Removed duplicated region for block: B:40:? A[RETURN, SYNTHETIC] */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void run() {
                        /*
                        // Method dump skipped, instructions count: 155
                        */
                        throw new UnsupportedOperationException("Method not decompiled: io.socket.engineio.client.transports.PollingXHR.Request.AnonymousClass1.run():void");
                    }
                }).start();
            } catch (IOException e) {
                onError(e);
            }
        }

        private void onSuccess() {
            emit(EVENT_SUCCESS, new Object[0]);
        }

        private void onData(String str) {
            emit("data", str);
            onSuccess();
        }

        private void onData(byte[] bArr) {
            emit("data", bArr);
            onSuccess();
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void onError(Exception exc) {
            emit("error", exc);
        }

        private void onRequestHeaders(Map<String, List<String>> map) {
            emit("requestHeaders", map);
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void onResponseHeaders(Map<String, List<String>> map) {
            emit("responseHeaders", map);
        }

        private void cleanup() {
            HttpURLConnection httpURLConnection = this.xhr;
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
                this.xhr = null;
            }
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x0090, code lost:
            if (r2 == null) goto L_0x00ab;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:42:0x00a6, code lost:
            if (r2 == null) goto L_0x00ab;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
            r2.close();
         */
        /* JADX WARNING: Removed duplicated region for block: B:39:0x00a1 A[SYNTHETIC, Splitter:B:39:0x00a1] */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x00b2 A[SYNTHETIC, Splitter:B:50:0x00b2] */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x00b9 A[SYNTHETIC, Splitter:B:54:0x00b9] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void onLoad() {
            /*
            // Method dump skipped, instructions count: 194
            */
            throw new UnsupportedOperationException("Method not decompiled: io.socket.engineio.client.transports.PollingXHR.Request.onLoad():void");
        }
    }
}
