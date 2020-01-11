package com.chends.opengl.view.advanced.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.chends.opengl.renderer.advanced.opengl.FrameBuffersRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseTypeGLView;

/**
 * 帧缓冲
 * @author chends create on 2020/1/10.
 */
public class FrameBuffersView extends BaseTypeGLView {

    public FrameBuffersView(Context context, int type) {
        this(context, null, type);
    }

    public FrameBuffersView(Context context, AttributeSet attrs, int type) {
        super(context, attrs, type);
    }

    @Override
    protected void init() {
        setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        setEGLContextFactory(OpenGLUtil.createFactory());
        setRenderer(new FrameBuffersRenderer(getContext(), type));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
