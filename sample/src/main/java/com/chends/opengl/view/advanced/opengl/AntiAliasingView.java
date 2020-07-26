package com.chends.opengl.view.advanced.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.widget.Toast;

import com.chends.opengl.renderer.advanced.opengl.AntiAliasingRenderer;
import com.chends.opengl.renderer.window.CubeRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseTypeGLView;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

/**
 * 抗锯齿
 * @author chends create on 2020/3/12.
 */
public class AntiAliasingView extends BaseTypeGLView {

    public AntiAliasingView(Context context, int type) {
        this(context, null, type);
    }

    public AntiAliasingView(Context context, AttributeSet attrs, int type) {
        super(context, attrs, type);
    }

    @Override
    protected void init() {
        setEGLContextFactory(OpenGLUtil.createFactory());
        switch (type) {
            case 0:
            case 1:
                if (type == 0) {
                    setEGLConfigChooser(true);
                } else {
                    if (!OpenGLUtil.checkOpenGL(getContext(), 3)){
                        Toast.makeText(getContext(), "该设备不支持抗锯齿", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    setEGLConfigChooser(new MyConfigChooser());
                }
                setRenderer(new CubeRenderer());
                setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                break;
            case 2:
                if (!OpenGLUtil.checkOpenGL(getContext(), 4)){
                    Toast.makeText(getContext(), "该设备不支持抗锯齿", Toast.LENGTH_SHORT).show();
                    return;
                }
                setEGLConfigChooser(true);
                setRenderer(new AntiAliasingRenderer(getContext()));
                setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                break;
        }
    }

    private static class MyConfigChooser implements GLSurfaceView.EGLConfigChooser {
        @Override
        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            final int[] attributes = {
                    EGL10.EGL_LEVEL, 0,
                    EGL10.EGL_RENDERABLE_TYPE, 4,  // EGL_OPENGL_ES2_BIT
                    EGL10.EGL_COLOR_BUFFER_TYPE,
                    EGL10.EGL_RGB_BUFFER,
                    EGL10.EGL_RED_SIZE, 8,
                    EGL10.EGL_GREEN_SIZE, 8,
                    EGL10.EGL_BLUE_SIZE, 8,
                    EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_SAMPLE_BUFFERS, GL10.GL_TRUE,
                    EGL10.EGL_SAMPLES, 4,  // 在这里修改MSAA的倍数，4就是4xMSAA
                    EGL10.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] configCounts = new int[1];
            egl.eglChooseConfig(display, attributes, configs, 1, configCounts);

            if (configCounts[0] == 0) {
                // Failed! Error handling.
                return null;
            } else {
                return configs[0];
            }
        }
    }
}