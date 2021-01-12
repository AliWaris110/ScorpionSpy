package com.example.project20;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ServiceReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(new Intent(context, MainService.class));
        } else {
            context.startService(new Intent(context, MainService.class));
        }
    }
}
