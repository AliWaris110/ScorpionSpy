.class Lcom/example/project20/CameraManager$1;
.super Ljava/lang/Object;
.source "CameraManager.java"

# interfaces
.implements Landroid/hardware/Camera$PictureCallback;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/example/project20/CameraManager;->startUp(I)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/example/project20/CameraManager;


# direct methods
.method constructor <init>(Lcom/example/project20/CameraManager;)V
    .locals 0

    .line 37
    iput-object p1, p0, Lcom/example/project20/CameraManager$1;->this$0:Lcom/example/project20/CameraManager;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onPictureTaken([BLandroid/hardware/Camera;)V
    .locals 0

    .line 40
    iget-object p2, p0, Lcom/example/project20/CameraManager$1;->this$0:Lcom/example/project20/CameraManager;

    invoke-static {p2}, Lcom/example/project20/CameraManager;->access$000(Lcom/example/project20/CameraManager;)V

    .line 41
    iget-object p2, p0, Lcom/example/project20/CameraManager$1;->this$0:Lcom/example/project20/CameraManager;

    invoke-static {p2, p1}, Lcom/example/project20/CameraManager;->access$100(Lcom/example/project20/CameraManager;[B)V

    return-void
.end method
