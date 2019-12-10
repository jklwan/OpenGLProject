package com.chends.opengl.view.window;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.chends.opengl.renderer.window.SquareRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseGLView;

/**
 * @author chends create on 2019/12/10.
 */
public class SquareView extends BaseGLView {

    public SquareView(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        setEGLContextFactory(OpenGLUtil.createFactory());
        setRenderer(new SquareRenderer());
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
