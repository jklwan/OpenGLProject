package com.chends.opengl.view.model;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.chends.opengl.renderer.model.LoadModelRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseGLView;

/**
 * @author chends create on 2020/1/2.
 */
public class LoadModelView extends BaseGLView {
    public LoadModelView(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        // 设置版本
        //setEGLContextClientVersion(2);
        setEGLContextFactory(OpenGLUtil.createFactory());
        // 设置Renderer
        setRenderer(new LoadModelRenderer(getContext()));
        // 设置渲染模式（默认RENDERMODE_CONTINUOUSLY）
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}