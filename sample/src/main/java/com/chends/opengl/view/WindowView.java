package com.chends.opengl.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.chends.opengl.utils.OpenGLUtil;

/**
 * @author chends create on 2019/12/6.
 */
public class WindowView extends GLSurfaceView {
    public WindowView(Context context) {
        this(context, null);
    }

    public WindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected void init() {
        setEGLContextFactory(OpenGLUtil.createFactory());
        setRenderer(new BaseRenderer());
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


}
