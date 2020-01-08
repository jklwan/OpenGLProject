package com.chends.opengl.view.advanced.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.chends.opengl.renderer.advanced.opengl.BlendingRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseGLView;

/**
 *混合
 * @author chends create on 2020/1/7.
 */
public class BlendingView extends BaseGLView {

    public BlendingView(Context context) {
        this(context, null);
    }

    public BlendingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        setEGLContextFactory(OpenGLUtil.createFactory());
        setRenderer(new BlendingRenderer(getContext()));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}