package com.chends.opengl.view.advanced.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.chends.opengl.renderer.advanced.opengl.StencilTestingRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseGLView;

/**
 * @author chends create on 2020/1/7.
 */
public class StencilTestingView extends BaseGLView {

    public StencilTestingView(Context context) {
        this(context, null);
    }

    public StencilTestingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        setEGLContextFactory(OpenGLUtil.createFactory());
        setRenderer(new StencilTestingRenderer(getContext()));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}