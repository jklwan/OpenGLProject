package com.chends.opengl.view.window;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseGLView;

/**
 * @author chends create on 2019/12/6.
 */
public class TriangleView extends BaseGLView {
    public TriangleView(Context context) {
        super(context);
    }

    public TriangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        setEGLContextFactory(OpenGLUtil.createFactory());
        setRenderer(new BaseRenderer(Color.GRAY));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


}
