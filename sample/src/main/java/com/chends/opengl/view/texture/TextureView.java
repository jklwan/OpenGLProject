package com.chends.opengl.view.texture;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.chends.opengl.renderer.texture.TextureRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseGLView;

/**
 * @author cds created on 2019/12/13.
 */
public class TextureView extends BaseGLView {
    public TextureView(Context context) {
        super(context);
    }

    public TextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        setEGLContextFactory(OpenGLUtil.createFactory());
        setRenderer(new TextureRenderer(getContext()));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
