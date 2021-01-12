package com.example.project20;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class WifiScanner {
    public static JSONObject scan(Context context) {
        List<ScanResult> scanResults;
        try {
            JSONObject jSONObject = new JSONObject();
            JSONArray jSONArray = new JSONArray();
            WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
            if (wifiManager != null && wifiManager.isWifiEnabled() && (scanResults = wifiManager.getScanResults()) != null && scanResults.size() > 0) {
                int i = 0;
                while (i < scanResults.size() && i < 10) {
                    ScanResult scanResult = scanResults.get(i);
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put("BSSID", scanResult.BSSID);
                    jSONObject2.put("SSID", scanResult.SSID);
                    jSONArray.put(jSONObject2);
                    i++;
                }
                jSONObject.put("networks", jSONArray);
            }
            return jSONObject;
        } catch (Throwable th) {
            Log.e("MtaSDK", "isWifiNet error", th);
            return null;
        }
    }
}
