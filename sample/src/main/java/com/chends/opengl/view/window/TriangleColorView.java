package com.chends.opengl.view.window;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.chends.opengl.renderer.window.TriangleColorRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseGLView;

/**
 * @author chends create on 2019/12/10.
 */
public class TriangleColorView extends BaseGLView {

    public TriangleColorView(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        setEGLContextFactory(OpenGLUtil.createFactory());
        setRenderer(new TriangleColorRenderer());
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
