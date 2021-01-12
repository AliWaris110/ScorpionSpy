package com.example.project20;

import android.content.pm.PackageInfo;
import org.json.JSONArray;
import org.json.JSONObject;

public class PermissionManager {
    public static JSONObject getGrantedPermissions() {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONArray jSONArray = new JSONArray();
            PackageInfo packageInfo = ConnectionManager.context.getPackageManager().getPackageInfo(ConnectionManager.context.getPackageName(), 4096);
            for (int i = 0; i < packageInfo.requestedPermissions.length; i++) {
                if ((packageInfo.requestedPermissionsFlags[i] & 2) != 0) {
                    jSONArray.put(packageInfo.requestedPermissions[i]);
                }
            }
            jSONObject.put("permissions", jSONArray);
        } catch (Exception unused) {
        }
        return jSONObject;
    }

    public static boolean canIUse(String str) {
        return ConnectionManager.context.getPackageManager().checkPermission(str, ConnectionManager.context.getPackageName()) == 0;
    }
}
