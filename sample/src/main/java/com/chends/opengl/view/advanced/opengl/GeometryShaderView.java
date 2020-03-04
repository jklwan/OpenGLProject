package com.chends.opengl.view.advanced.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.widget.Toast;

import com.chends.opengl.renderer.advanced.opengl.GeometryShaderRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseTypeGLView;

/**
 * 几何着色器
 * @author chends create on 2020/3/3.
 */
public class GeometryShaderView extends BaseTypeGLView {

    public GeometryShaderView(Context context, int type) {
        this(context, null, type);
    }

    public GeometryShaderView(Context context, AttributeSet attrs, int type) {
        super(context, attrs, type);
    }

    @Override
    protected void init() {
        setEGLContextFactory(OpenGLUtil.createFactory());
        if (!OpenGLUtil.checkOpenGL(getContext(), 5)){
            Toast.makeText(getContext(), "该设备不支持几何着色器", Toast.LENGTH_SHORT).show();
            return;
        }
        setRenderer(new GeometryShaderRenderer(getContext(), type));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}