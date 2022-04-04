package com.aihuan.main.activity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.aihuan.common.utils.DpUtil;
import com.aihuan.main.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.sdk.bean.TiRotation;
import cn.tillusory.sdk.gles.TiGLUtils;
import cn.tillusory.sdk.renderer.TakePictureRenderer;
import cn.tillusory.sdk.renderer.TiShowRenderer;
import cn.tillusory.tiui.TiCamera;
import cn.tillusory.tiui.TiGlSurfaceView;
import cn.tillusory.tiui.TiPanelLayout;

public class BeautySettingActivity extends AppCompatActivity implements GLSurfaceView.Renderer {

    private String TAG = "GLSurfaceViewCameraActivity";

    private TiGlSurfaceView glSurfaceView;
    private TiCamera camera;

    private TiShowRenderer showRenderer;
    private TakePictureRenderer takePictureRenderer;

    private SurfaceTexture surfaceTexture;
    private int oesTextureId;

    /**
     * 相机采集的宽高
     */
    private final int imageWidth = 1280;
    private final int imageHeight = 720;

    private final boolean isFrontCamera = true;
    private TiRotation tiRotation;
    private int cameraId;

    private boolean isTakePicture = false;
    private int pictureWidth = 720, pictureHeight = 1280;
    private String picturePath;
    private HandlerThread pictureHandlerThread;
    private Handler pictureHandler;

    /**
     * 页面显示的宽高
     */
    private int surfaceWidth, surfaceHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 设置 4.4 及以上版本导航栏透明。
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        super.onCreate(savedInstanceState);
        glSurfaceView = new TiGlSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(this);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        setContentView(glSurfaceView);
        TiSDKManager.getInstance().destroy();
        TiPanelLayout tiPanelLayout = new TiPanelLayout(this).init(TiSDKManager.getInstance());
        addContentView(tiPanelLayout,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ImageView button = new ImageView(this);
        button.setImageResource(R.mipmap.icon_live_close);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(DpUtil.dp2px(30), DpUtil.dp2px(30));
        params.leftMargin = DpUtil.dp2px(20);
        params.topMargin = DpUtil.dp2px(20);
        addContentView(button, params);
        tiPanelLayout.showBeautyControl();
        pictureHandlerThread = new HandlerThread("TakePicture");
        pictureHandlerThread.start();
        pictureHandler = new Handler(pictureHandlerThread.getLooper());
        glSurfaceView.setAspectRatio(pictureWidth, pictureHeight);
        camera = new TiCamera(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TiSDKManager.getInstance().destroy();
        pictureHandlerThread.quit();
        camera.releaseCamera();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        Log.i(TAG, "onSurfaceChanged width = " + width + ", height = " + height);
        surfaceWidth = width;
        surfaceHeight = height;

        showRenderer = new TiShowRenderer(surfaceWidth, surfaceHeight);
        showRenderer.create(isFrontCamera);

        takePictureRenderer = new TakePictureRenderer(pictureWidth, pictureHeight);
        takePictureRenderer.create(isFrontCamera);

        oesTextureId = TiGLUtils.getExternalOESTextureID();
        surfaceTexture = new SurfaceTexture(oesTextureId);
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                glSurfaceView.requestRender();
            }
        });

        cameraId = isFrontCamera ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
        tiRotation = isFrontCamera ? TiRotation.CLOCKWISE_ROTATION_270 : TiRotation.CLOCKWISE_ROTATION_90;
        camera.openCamera(cameraId, imageWidth, imageHeight);

        camera.setPreviewSurface(surfaceTexture);
        camera.startPreview();
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        //        Log.i(TAG, "onDrawFrame");
        int textureId = TiSDKManager.getInstance().renderOESTexture(oesTextureId, imageWidth, imageHeight, tiRotation, isFrontCamera);
        showRenderer.render(textureId);

        if (isTakePicture) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(pictureWidth * pictureHeight * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            byteBuffer.position(0);
            takePictureRenderer.takePicture(textureId, byteBuffer);
            saveBitmap(pictureWidth, pictureHeight, byteBuffer);
            isTakePicture = false;
        }
        surfaceTexture.updateTexImage();
    }

    private void takePicture() {
        isTakePicture = true;
    }

    private void saveBitmap(final int width, final int height, final ByteBuffer bf) {

        pictureHandler.post(new Runnable() {
            @Override
            public void run() {

                //根据需要自己调节图片的大小，如果卡顿将质量调低即可
                //                Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                result.copyPixelsFromBuffer(bf);
                picturePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                new File(picturePath).mkdirs();
                picturePath = picturePath + "/" + System.currentTimeMillis() + ".png";

                boolean isSuccess = saveBitmap(result, new File(picturePath));
                Log.e(TAG, "saveBitmap,path:" + picturePath);

            }
        });
    }

    private boolean saveBitmap(Bitmap bitmap, File file) {
        if (bitmap == null) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

}