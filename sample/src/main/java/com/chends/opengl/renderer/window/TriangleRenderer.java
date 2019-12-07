package com.chends.opengl.renderer.window;

import com.chends.opengl.renderer.BaseRenderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author chends create on 2019/12/7.
 */
public class TriangleRenderer extends BaseRenderer {
    public TriangleRenderer() {
    }

    public TriangleRenderer(int bg) {
        super(bg);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
    }
}
