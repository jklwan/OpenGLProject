package com.chends.opengl.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.chends.opengl.BuildConfig;
import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

/**
 * @author chends create on 2019/12/7.
 */
public class BaseGLView extends GLSurfaceView {
    public BaseGLView(Context context) {
        this(context, null);
    }

    public BaseGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDebug();
        if (createInit()) {
            init();
        }
    }

    protected boolean createInit() {
        return true;
    }

    protected void setDebug() {
        setDebugFlags(BuildConfig.DEBUG ? DEBUG_LOG_GL_CALLS : DEBUG_CHECK_GL_ERROR);
    }

    protected void init() {
        // 设置版本
        //setEGLContextClientVersion(2);
        setEGLContextFactory(OpenGLUtil.createFactory());
        // 设置Renderer
        setRenderer(new BaseRenderer());
        // 设置渲染模式（默认RENDERMODE_CONTINUOUSLY）
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
