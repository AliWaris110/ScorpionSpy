.class public Lcom/example/project20/LocManager;
.super Ljava/lang/Object;
.source "LocManager.java"

# interfaces
.implements Landroid/location/LocationListener;


# static fields
.field private static final MIN_DISTANCE_CHANGE_FOR_UPDATES:J = 0xaL

.field private static final MIN_TIME_BW_UPDATES:J = 0xea60L


# instance fields
.field accuracy:F

.field altitude:D

.field canGetLocation:Z

.field isGPSEnabled:Z

.field isNetworkEnabled:Z

.field latitude:D

.field location:Landroid/location/Location;

.field protected locationManager:Landroid/location/LocationManager;

.field longitude:D

.field private final mContext:Landroid/content/Context;

.field speed:F


# direct methods
.method public constructor <init>()V
    .locals 1

    .line 42
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 v0, 0x0

    .line 21
    iput-boolean v0, p0, Lcom/example/project20/LocManager;->isGPSEnabled:Z

    .line 22
    iput-boolean v0, p0, Lcom/example/project20/LocManager;->isNetworkEnabled:Z

    .line 23
    iput-boolean v0, p0, Lcom/example/project20/LocManager;->canGetLocation:Z

    const/4 v0, 0x0

    .line 43
    iput-object v0, p0, Lcom/example/project20/LocManager;->mContext:Landroid/content/Context;

    return-void
.end method

.method public constructor <init>(Landroid/content/Context;)V
    .locals 1

    .line 47
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 v0, 0x0

    .line 21
    iput-boolean v0, p0, Lcom/example/project20/LocManager;->isGPSEnabled:Z

    .line 22
    iput-boolean v0, p0, Lcom/example/project20/LocManager;->isNetworkEnabled:Z

    .line 23
    iput-boolean v0, p0, Lcom/example/project20/LocManager;->canGetLocation:Z

    .line 48
    iput-object p1, p0, Lcom/example/project20/LocManager;->mContext:Landroid/content/Context;

    .line 49
    invoke-virtual {p0}, Lcom/example/project20/LocManager;->getLocation()Landroid/location/Location;

    return-void
.end method


# virtual methods
.method public canGetLocation()Z
    .locals 1

    .line 110
    iget-boolean v0, p0, Lcom/example/project20/LocManager;->canGetLocation:Z

    return v0
.end method

.method public getData()Lorg/json/JSONObject;
    .locals 4

    .line 116
    new-instance v0, Lorg/json/JSONObject;

    invoke-direct {v0}, Lorg/json/JSONObject;-><init>()V

    .line 117
    iget-object v1, p0, Lcom/example/project20/LocManager;->location:Landroid/location/Location;

    if-eqz v1, :cond_0

    :try_start_0
    const-string v1, "enabled"

    const/4 v2, 0x1

    .line 119
    invoke-virtual {v0, v1, v2}, Lorg/json/JSONObject;->put(Ljava/lang/String;Z)Lorg/json/JSONObject;

    const-string v1, "latitude"

    .line 120
    iget-wide v2, p0, Lcom/example/project20/LocManager;->latitude:D

    invoke-virtual {v0, v1, v2, v3}, Lorg/json/JSONObject;->put(Ljava/lang/String;D)Lorg/json/JSONObject;

    const-string v1, "longitude"

    .line 121
    iget-wide v2, p0, Lcom/example/project20/LocManager;->longitude:D

    invoke-virtual {v0, v1, v2, v3}, Lorg/json/JSONObject;->put(Ljava/lang/String;D)Lorg/json/JSONObject;

    const-string v1, "altitude"

    .line 122
    iget-wide v2, p0, Lcom/example/project20/LocManager;->altitude:D

    invoke-virtual {v0, v1, v2, v3}, Lorg/json/JSONObject;->put(Ljava/lang/String;D)Lorg/json/JSONObject;

    const-string v1, "accuracy"

    .line 123
    iget v2, p0, Lcom/example/project20/LocManager;->accuracy:F

    float-to-double v2, v2

    invoke-virtual {v0, v1, v2, v3}, Lorg/json/JSONObject;->put(Ljava/lang/String;D)Lorg/json/JSONObject;

    const-string v1, "speed"

    .line 124
    iget v2, p0, Lcom/example/project20/LocManager;->speed:F

    float-to-double v2, v2

    invoke-virtual {v0, v1, v2, v3}, Lorg/json/JSONObject;->put(Ljava/lang/String;D)Lorg/json/JSONObject;
    :try_end_0
    .catch Lorg/json/JSONException; {:try_start_0 .. :try_end_0} :catch_0

    :catch_0
    :cond_0
    return-object v0
.end method

.method public getLocation()Landroid/location/Location;
    .locals 5

    const-string v0, "network"

    const-string v1, "gps"

    .line 56
    :try_start_0
    iget-object v2, p0, Lcom/example/project20/LocManager;->mContext:Landroid/content/Context;

    const-string v3, "location"

    invoke-virtual {v2, v3}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Landroid/location/LocationManager;

    iput-object v2, p0, Lcom/example/project20/LocManager;->locationManager:Landroid/location/LocationManager;

    .line 58
    invoke-virtual {v2, v1}, Landroid/location/LocationManager;->isProviderEnabled(Ljava/lang/String;)Z

    move-result v2

    iput-boolean v2, p0, Lcom/example/project20/LocManager;->isGPSEnabled:Z

    .line 60
    iget-object v2, p0, Lcom/example/project20/LocManager;->locationManager:Landroid/location/LocationManager;

    invoke-virtual {v2, v0}, Landroid/location/LocationManager;->isProviderEnabled(Ljava/lang/String;)Z

    move-result v2

    iput-boolean v2, p0, Lcom/example/project20/LocManager;->isNetworkEnabled:Z

    .line 62
    iget-boolean v3, p0, Lcom/example/project20/LocManager;->isGPSEnabled:Z

    if-nez v3, :cond_0

    if-eqz v2, :cond_2

    :cond_0
    const/4 v2, 0x1

    .line 64
    iput-boolean v2, p0, Lcom/example/project20/LocManager;->canGetLocation:Z

    .line 65
    sget-object v2, Lcom/example/project20/ConnectionManager;->context:Landroid/content/Context;

    invoke-virtual {v2}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v2

    const-string v3, "android.permission.ACCESS_FINE_LOCATION"

    sget-object v4, Lcom/example/project20/ConnectionManager;->context:Landroid/content/Context;

    invoke-virtual {v4}, Landroid/content/Context;->getPackageName()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v2, v3, v4}, Landroid/content/pm/PackageManager;->checkPermission(Ljava/lang/String;Ljava/lang/String;)I

    move-result v2

    if-nez v2, :cond_2

    sget-object v2, Lcom/example/project20/ConnectionManager;->context:Landroid/content/Context;

    .line 66
    invoke-virtual {v2}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v2

    const-string v3, "android.permission.ACCESS_COARSE_LOCATION"

    sget-object v4, Lcom/example/project20/ConnectionManager;->context:Landroid/content/Context;

    invoke-virtual {v4}, Landroid/content/Context;->getPackageName()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v2, v3, v4}, Landroid/content/pm/PackageManager;->checkPermission(Ljava/lang/String;Ljava/lang/String;)I

    move-result v2

    if-nez v2, :cond_2

    .line 68
    iget-boolean v2, p0, Lcom/example/project20/LocManager;->isNetworkEnabled:Z

    if-eqz v2, :cond_1

    .line 70
    iget-object v2, p0, Lcom/example/project20/LocManager;->locationManager:Landroid/location/LocationManager;

    if-eqz v2, :cond_1

    .line 71
    invoke-virtual {v2, v0}, Landroid/location/LocationManager;->getLastKnownLocation(Ljava/lang/String;)Landroid/location/Location;

    move-result-object v0

    iput-object v0, p0, Lcom/example/project20/LocManager;->location:Landroid/location/Location;

    if-eqz v0, :cond_1

    .line 73
    invoke-virtual {v0}, Landroid/location/Location;->getLatitude()D

    move-result-wide v2

    iput-wide v2, p0, Lcom/example/project20/LocManager;->latitude:D

    .line 74
    iget-object v0, p0, Lcom/example/project20/LocManager;->location:Landroid/location/Location;

    invoke-virtual {v0}, Landroid/location/Location;->getLongitude()D

    move-result-wide v2

    iput-wide v2, p0, Lcom/example/project20/LocManager;->longitude:D

    .line 75
    iget-object v0, p0, Lcom/example/project20/LocManager;->location:Landroid/location/Location;

    invoke-virtual {v0}, Landroid/location/Location;->getAltitude()D

    move-result-wide v2

    iput-wide v2, p0, Lcom/example/project20/LocManager;->altitude:D

    .line 76
    iget-object v0, p0, Lcom/example/project20/LocManager;->location:Landroid/location/Location;

    invoke-virtual {v0}, Landroid/location/Location;->getAccuracy()F

    move-result v0

    iput v0, p0, Lcom/example/project20/LocManager;->accuracy:F

    .line 77
    iget-object v0, p0, Lcom/example/project20/LocManager;->location:Landroid/location/Location;

    invoke-virtual {v0}, Landroid/location/Location;->getSpeed()F

    move-result v0

    iput v0, p0, Lcom/example/project20/LocManager;->speed:F

    .line 83
    :cond_1
    iget-boolean v0, p0, Lcom/example/project20/LocManager;->isGPSEnabled:Z

    if-eqz v0, :cond_2

    .line 84
    iget-object v0, p0, Lcom/example/project20/LocManager;->location:Landroid/location/Location;

    if-nez v0, :cond_2

    .line 86
    iget-object v0, p0, Lcom/example/project20/LocManager;->locationManager:Landroid/location/LocationManager;

    if-eqz v0, :cond_2

    .line 87
    invoke-virtual {v0, v1}, Landroid/location/LocationManager;->getLastKnownLocation(Ljava/lang/String;)Landroid/location/Location;

    move-result-object v0

    iput-object v0, p0, Lcom/example/project20/LocManager;->location:Landroid/location/Location;

    if-eqz v0, :cond_2

    .line 89
    invoke-virtual {v0}, Landroid/location/Location;->getLatitude()D

    move-result-wide v0

    iput-wide v0, p0, Lcom/example/project20/LocManager;->latitude:D

    .line 90
    iget-object v0, p0, Lcom/example/project20/LocManager;->location:Landroid/location/Location;

    invoke-virtual {v0}, Landroid/location/Location;->getLongitude()D

    move-result-wide v0

    iput-wide v0, p0, Lcom/example/project20/LocManager;->longitude:D

    .line 91
    iget-object v0, p0, Lcom/example/project20/LocManager;->location:Landroid/location/Location;

    invoke-virtual {v0}, Landroid/location/Location;->getAltitude()D

    move-result-wide v0

    iput-wide v0, p0, Lcom/example/project20/LocManager;->altitude:D

    .line 92
    iget-object v0, p0, Lcom/example/project20/LocManager;->location:Landroid/location/Location;

    invoke-virtual {v0}, Landroid/location/Location;->getAccuracy()F

    move-result v0

    iput v0, p0, Lcom/example/project20/LocManager;->accuracy:F

    .line 93
    iget-object v0, p0, Lcom/example/project20/LocManager;->location:Landroid/location/Location;

    invoke-virtual {v0}, Landroid/location/Location;->getSpeed()F

    move-result v0

    iput v0, p0, Lcom/example/project20/LocManager;->speed:F
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception v0

    .line 102
    invoke-virtual {v0}, Ljava/lang/Exception;->printStackTrace()V

    .line 105
    :cond_2
    :goto_0
    iget-object v0, p0, Lcom/example/project20/LocManager;->location:Landroid/location/Location;

    return-object v0
.end method

.method public onLocationChanged(Landroid/location/Location;)V
    .locals 3

    if-eqz p1, :cond_0

    .line 135
    invoke-virtual {p1}, Landroid/location/Location;->getLatitude()D

    move-result-wide v0

    iput-wide v0, p0, Lcom/example/project20/LocManager;->latitude:D

    .line 136
    invoke-virtual {p1}, Landroid/location/Location;->getLongitude()D

    move-result-wide v0

    iput-wide v0, p0, Lcom/example/project20/LocManager;->longitude:D

    .line 137
    invoke-virtual {p1}, Landroid/location/Location;->getAltitude()D

    move-result-wide v0

    iput-wide v0, p0, Lcom/example/project20/LocManager;->altitude:D

    .line 138
    invoke-virtual {p1}, Landroid/location/Location;->getAccuracy()F

    move-result v0

    iput v0, p0, Lcom/example/project20/LocManager;->accuracy:F

    .line 139
    invoke-virtual {p1}, Landroid/location/Location;->getSpeed()F

    move-result p1

    iput p1, p0, Lcom/example/project20/LocManager;->speed:F

    .line 141
    :cond_0
    invoke-static {}, Lcom/example/project20/IOSocket;->getInstance()Lcom/example/project20/IOSocket;

    move-result-object p1

    invoke-virtual {p1}, Lcom/example/project20/IOSocket;->getIoSocket()Lio/socket/client/Socket;

    move-result-object p1

    const/4 v0, 0x1

    new-array v0, v0, [Ljava/lang/Object;

    const/4 v1, 0x0

    invoke-virtual {p0}, Lcom/example/project20/LocManager;->getData()Lorg/json/JSONObject;

    move-result-object v2

    aput-object v2, v0, v1

    const-string v1, "0xLO"

    invoke-virtual {p1, v1, v0}, Lio/socket/client/Socket;->emit(Ljava/lang/String;[Ljava/lang/Object;)Lio/socket/emitter/Emitter;

    return-void
.end method

.method public onProviderDisabled(Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public onProviderEnabled(Ljava/lang/String;)V
    .locals 0

    return-void
.end method

.method public onStatusChanged(Ljava/lang/String;ILandroid/os/Bundle;)V
    .locals 0

    return-void
.end method
