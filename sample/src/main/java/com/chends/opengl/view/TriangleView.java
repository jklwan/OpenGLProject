package com.chends.opengl.view;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.chends.opengl.utils.OpenGLUtil;

/**
 * @author chends create on 2019/12/6.
 */
public class TriangleView extends WindowView {
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
