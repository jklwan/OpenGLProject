package com.chends.opengl.view.model;

import android.content.Context;

import com.chends.opengl.renderer.model.LoadModelMaterialRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseGLView;

/**
 * 加载模型和材料
 * @author chends create on 2020/1/4.
 */
public class LoadModelMaterialView extends BaseGLView {
    public LoadModelMaterialView(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        // 设置版本
        //setEGLContextClientVersion(2);
        setEGLContextFactory(OpenGLUtil.createFactory());
        // 设置Renderer
        setRenderer(new LoadModelMaterialRenderer(getContext()));
        // 设置渲染模式（默认RENDERMODE_CONTINUOUSLY）
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}