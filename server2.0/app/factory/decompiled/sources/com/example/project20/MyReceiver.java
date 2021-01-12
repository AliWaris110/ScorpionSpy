package com.example.project20;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class MyReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SECRET_CODE")) {
            String[] split = intent.getDataString().split("://");
            if (split[1].equalsIgnoreCase("8088")) {
                context.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            } else if (split[1].equalsIgnoreCase("5055")) {
                context.startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:com.example.project20")));
            }
        }
        context.startService(new Intent(context, MainService.class));
    }
}
