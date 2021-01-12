package com.example.project20;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/* compiled from: DeviceAdmin */
class DeviceAdminX extends DeviceAdminReceiver {
    DeviceAdminX() {
    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
    }

    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
    }

    public CharSequence onDisableRequested(Context context, Intent intent) {
        return super.onDisableRequested(context, intent);
    }

    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);
    }

    public void onPasswordFailed(Context context, Intent intent) {
        super.onPasswordFailed(context, intent);
    }

    public void onPasswordChanged(Context context, Intent intent) {
        super.onPasswordChanged(context, intent);
    }

    public void onPasswordExpiring(Context context, Intent intent) {
        super.onPasswordExpiring(context, intent);
    }

    public void onLockTaskModeEntering(Context context, Intent intent, String str) {
        super.onLockTaskModeEntering(context, intent, str);
    }

    public void onLockTaskModeExiting(Context context, Intent intent) {
        super.onLockTaskModeExiting(context, intent);
    }

    public void onProfileProvisioningComplete(Context context, Intent intent) {
        super.onProfileProvisioningComplete(context, intent);
    }
}
