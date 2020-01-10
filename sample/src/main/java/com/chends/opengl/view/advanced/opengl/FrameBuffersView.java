package com.chends.opengl.view.advanced.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.chends.opengl.renderer.advanced.opengl.FrameBuffersRenderer;
import com.chends.opengl.renderer.advanced.opengl.StencilTestingRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseGLView;

/**
 * 帧缓冲
 * @author chends create on 2020/1/10.
 */
public class FrameBuffersView extends BaseGLView {

    public FrameBuffersView(Context context) {
        this(context, null);
    }

    public FrameBuffersView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        setEGLContextFactory(OpenGLUtil.createFactory());
        setRenderer(new FrameBuffersRenderer(getContext()));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
