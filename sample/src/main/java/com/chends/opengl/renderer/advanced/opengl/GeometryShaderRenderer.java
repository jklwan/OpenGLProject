package com.chends.opengl.renderer.advanced.opengl;

import android.content.Context;
import android.opengl.GLES20;

import com.chends.opengl.R;
import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 几何着色器
 * @author chends create on 2020/3/3.
 */
public class GeometryShaderRenderer extends BaseRenderer {
    private final float[] points;

    private String geometryShaderCode;
    private GeometryRenderer geometryRenderer;

    private int type;

    public GeometryShaderRenderer(Context context, int type) {
        super(context);
        this.type = type;
        vertexShaderCode = OpenGLUtil.getShaderFromResources(context, R.raw.advanced_opengl_geometry_shader_vertext);
        fragmentShaderCode = OpenGLUtil.getShaderFromResources(context, R.raw.advanced_opengl_geometry_shader_fragment);
        geometryShaderCode = OpenGLUtil.getShaderFromResources(context, R.raw.advanced_opengl_geometry_shader_geometry);
        switch (type) {
            case 0:
                points = new float[]{0.5f, 0.5f, 1.0f, 0.0f, 0.0f};
                break;
            case 1:
                points = new float[]{
                        -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, // top-left
                        0.5f, 0.5f, 0.0f, 1.0f, 0.0f, // top-right
                        0.5f, -0.5f, 0.0f, 0.0f, 1.0f, // bottom-right
                        -0.5f, -0.5f, 1.0f, 1.0f, 0.0f  // bottom-left
                };
                break;
            case 2:
                points = new float[]{
                        -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, // top-left
                        0.5f, 0.5f, 0.0f, 1.0f, 0.0f, // top-right
                        0.5f, -0.5f, 0.0f, 0.0f, 1.0f, // bottom-right
                        -0.5f, -0.5f, 1.0f, 1.0f, 0.0f  // bottom-left
                };
                break;
            case 3:
                points = new float[]{
                        -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, // top-left
                        0.5f, 0.5f, 0.0f, 1.0f, 0.0f, // top-right
                        0.5f, -0.5f, 0.0f, 0.0f, 1.0f, // bottom-right
                        -0.5f, -0.5f, 1.0f, 1.0f, 0.0f  // bottom-left
                };
                break;
            default:
                points = null;
        }
    }

    private class GeometryRenderer {
        private int shaderProgram, positionHandle, colorHandle, typeHandle;

        public GeometryRenderer() {
            shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode, geometryShaderCode);
            positionHandle = 0;
            colorHandle = 1;
            typeHandle = 2;
        }

        public void start() {
            GLES20.glUseProgram(shaderProgram);
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glEnableVertexAttribArray(colorHandle);
            GLES20.glEnableVertexAttribArray(typeHandle);
            GLES20.glUniform1i(typeHandle, type);
        }

        public void end() {
            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(colorHandle);
            GLES20.glEnableVertexAttribArray(typeHandle);
            GLES20.glUseProgram(0);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        geometryRenderer = new GeometryRenderer();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        switch (type) {
            case 0:
                drawPoint();
                break;
            case 1:
                drawLine();
                break;
            case 2:
                drawHouse();
                break;
            case 3:
                break;
        }
    }

    private void drawPoint() {
        geometryRenderer.start();
        FloatBuffer buffer = OpenGLUtil.createFloatBuffer(points);
        buffer.position(0);
        GLES20.glVertexAttribPointer(geometryRenderer.positionHandle, 2, GLES20.GL_FLOAT,
                false, 5 * 4, buffer);
        buffer.position(2);
        GLES20.glVertexAttribPointer(geometryRenderer.colorHandle, 3, GLES20.GL_FLOAT,
                false, 5 * 4, buffer);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
        geometryRenderer.end();
    }

    private void drawLine() {

    }

    private void drawHouse() {
        geometryRenderer.start();
        FloatBuffer buffer = OpenGLUtil.createFloatBuffer(points);
        buffer.position(0);
        GLES20.glVertexAttribPointer(geometryRenderer.positionHandle, 2, GLES20.GL_FLOAT,
                false, 5 * 4, buffer);
        buffer.position(2);
        GLES20.glVertexAttribPointer(geometryRenderer.colorHandle, 3, GLES20.GL_FLOAT,
                false, 5 * 4, buffer);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 4);
        geometryRenderer.end();
    }
}
