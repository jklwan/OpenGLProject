package com.chends.opengl.view.window;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.chends.opengl.Constant;
import com.chends.opengl.renderer.window.PointLineRenderer;
import com.chends.opengl.utils.JniRendererUtil;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseGLView;

/**
 * @author chends create on 2019/12/7.
 */
public class PointLineView extends BaseGLView {
    public PointLineView(Context context) {
        super(context);
    }

    public PointLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        setEGLContextFactory(OpenGLUtil.createFactory());
        if (Constant.UserJni) {
            setRenderer(JniRendererUtil.create(Constant.PointLine));
        } else {
            setRenderer(new PointLineRenderer());
        }
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
