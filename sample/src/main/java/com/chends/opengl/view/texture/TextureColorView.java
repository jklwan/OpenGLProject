package com.chends.opengl.view.texture;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.chends.opengl.renderer.texture.TextureColorRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseGLView;

/**
 * @author cds created on 2019/12/13.
 */
public class TextureColorView extends BaseGLView {
    public TextureColorView(Context context) {
        super(context);
    }

    public TextureColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        setEGLContextFactory(OpenGLUtil.createFactory());
        setRenderer(new TextureColorRenderer(getContext()));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
