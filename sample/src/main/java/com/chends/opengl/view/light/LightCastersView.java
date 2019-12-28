package com.chends.opengl.view.light;

import android.content.Context;

import com.chends.opengl.renderer.light.LightCastersRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseGLView;

/**
 * 投光物
 * @author chends create on 2019/12/27.
 */
public class LightCastersView extends BaseGLView {
    public LightCastersView(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        // 设置版本
        //setEGLContextClientVersion(2);
        setEGLContextFactory(OpenGLUtil.createFactory());
        // 设置Renderer
        setRenderer(new LightCastersRenderer(getContext()));
        // 设置渲染模式（默认RENDERMODE_CONTINUOUSLY）
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
