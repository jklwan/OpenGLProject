package com.chends.opengl.view.advanced.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.widget.Toast;

import com.chends.opengl.renderer.advanced.opengl.InstancingRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.BaseTypeGLView;

/**
 * @author chends create on 2020/3/6.
 */
public class InstancingView extends BaseTypeGLView {

    public InstancingView(Context context, int type) {
        this(context, null, type);
    }

    public InstancingView(Context context, AttributeSet attrs, int type) {
        super(context, attrs, type);
    }

    @Override
    protected void init() {
        setEGLContextFactory(OpenGLUtil.createFactory());
        if (!OpenGLUtil.checkOpenGL(getContext(), 3)) {
            Toast.makeText(getContext(), "该设备不支持实例化", Toast.LENGTH_SHORT).show();
            return;
        }
        setRenderer(new InstancingRenderer(getContext(), type));
        /*switch (type) {
            case 3:
            case 4:
                setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                break;
            default:
                setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                break;
        }*/
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}

