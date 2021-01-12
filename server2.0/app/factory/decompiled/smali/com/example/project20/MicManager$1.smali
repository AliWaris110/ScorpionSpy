.class final Lcom/example/project20/MicManager$1;
.super Ljava/util/TimerTask;
.source "MicManager.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/example/project20/MicManager;->startRecording(I)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = null
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    .line 46
    invoke-direct {p0}, Ljava/util/TimerTask;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 1

    .line 50
    sget-object v0, Lcom/example/project20/MicManager;->recorder:Landroid/media/MediaRecorder;

    invoke-virtual {v0}, Landroid/media/MediaRecorder;->stop()V

    .line 51
    sget-object v0, Lcom/example/project20/MicManager;->recorder:Landroid/media/MediaRecorder;

    invoke-virtual {v0}, Landroid/media/MediaRecorder;->release()V

    .line 52
    sget-object v0, Lcom/example/project20/MicManager;->audiofile:Ljava/io/File;

    invoke-static {v0}, Lcom/example/project20/MicManager;->access$000(Ljava/io/File;)V

    .line 53
    sget-object v0, Lcom/example/project20/MicManager;->audiofile:Ljava/io/File;

    invoke-virtual {v0}, Ljava/io/File;->delete()Z

    return-void
.end method
