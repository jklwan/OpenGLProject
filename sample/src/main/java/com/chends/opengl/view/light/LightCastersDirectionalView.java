package com.chends.opengl.view.light;

import android.content.Context;

import com.chends.opengl.renderer.light.LightCastersDirectionalRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseGLView;

/**
 * 投光物-平行光
 * @author chends create on 2019/12/27.
 */
public class LightCastersDirectionalView extends BaseGLView {
    public LightCastersDirectionalView(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        // 设置版本
        //setEGLContextClientVersion(2);
        setEGLContextFactory(OpenGLUtil.createFactory());
        // 设置Renderer
        setRenderer(new LightCastersDirectionalRenderer(getContext()));
        // 设置渲染模式（默认RENDERMODE_CONTINUOUSLY）
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
