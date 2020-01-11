package com.chends.opengl.view.advanced.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.chends.opengl.renderer.advanced.opengl.DepthTestingRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseTypeGLView;

/**
 * 深度测试
 * @author chends create on 2020/1/6.
 */
public class DepthTestingView extends BaseTypeGLView {

    public DepthTestingView(Context context, int type) {
        this(context, null, type);
    }

    public DepthTestingView(Context context, AttributeSet attrs, int type) {
        super(context, attrs, type);
    }

    @Override
    protected void init() {
        setEGLContextFactory(OpenGLUtil.createFactory());
        setRenderer(new DepthTestingRenderer(getContext(), type));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}