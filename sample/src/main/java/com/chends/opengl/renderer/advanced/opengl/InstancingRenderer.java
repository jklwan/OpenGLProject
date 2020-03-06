package com.chends.opengl.renderer.advanced.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.Build;

import com.chends.opengl.R;
import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 实例化
 * @author chends create on 2020/3/6.
 */
public class InstancingRenderer extends BaseRenderer {
    private final float[] points;
    private float[] translations;

    private String normalVertexShaderCode, normalFragmentShaderCode;
    private QuadsRenderer quadsRenderer;
    private NormalRenderer normalRenderer;
    private VisualizationRenderer visualizationRenderer;

    private int type;
    private final float[] mMVPMatrix = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16], modelMatrix = new float[16];
    private float[] mViewPos = new float[]{0f, 0f, 12f, 1f};

    public InstancingRenderer(Context context, int type) {
        super(context);
        this.type = type;
        int vertexRes = -1, fragmentRes = -1;
        switch (type) {
            case 0:
                points = new float[]{
                        // 顶点坐标     // 颜色
                        -0.05f, 0.05f, 1.0f, 0.0f, 0.0f,
                        0.05f, -0.05f, 0.0f, 1.0f, 0.0f,
                        -0.05f, -0.05f, 0.0f, 0.0f, 1.0f,

                        -0.05f, 0.05f, 1.0f, 0.0f, 0.0f,
                        0.05f, -0.05f, 0.0f, 1.0f, 0.0f,
                        0.05f, 0.05f, 0.0f, 1.0f, 1.0f
                };
                translations = new float[200];
                int index = 0;
                float offset = 0.1f;
                for (int y = -10; y < 10; y += 2) {
                    for (int x = -10; x < 10; x += 2) {
                        translations[index++] = (float) x / 10.0f + offset;
                        translations[index++] = (float) y / 10.0f + offset;
                    }
                }
                vertexRes = R.raw.advanced_opengl_instancing_quads_vertex;
                fragmentRes = R.raw.advanced_opengl_instancing_quads_fragment;
                break;
            case 1:
                points = new float[]{
                        -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, // 左上
                        0.5f, 0.5f, 0.0f, 1.0f, 0.0f, // 右上
                        0.5f, -0.5f, 0.0f, 0.0f, 1.0f, // 右下
                        -0.5f, -0.5f, 1.0f, 1.0f, 0.0f  // 左下
                };
                break;
            case 2:
                points = new float[]{
                        -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, // 左上
                        0.5f, 0.5f, 0.0f, 1.0f, 0.0f, // 右上
                        0.5f, -0.5f, 0.0f, 0.0f, 1.0f, // 右下
                        -0.5f, -0.5f, 1.0f, 1.0f, 0.0f  // 左下
                };
                break;
            default:
                points = null;
                if (type == 3 || type == 4) {

                } else {
                    return;
                }
                break;
        }
        vertexShaderCode = OpenGLUtil.getShaderFromResources(context, vertexRes);
        fragmentShaderCode = OpenGLUtil.getShaderFromResources(context, fragmentRes);
    }

    private class QuadsRenderer {
        private int shaderProgram, positionHandle, colorHandle, offsetHandle;

        public QuadsRenderer() {
            shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
            positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
            colorHandle = GLES20.glGetAttribLocation(shaderProgram, "aColor");
            offsetHandle = GLES20.glGetAttribLocation(shaderProgram, "aOffset");
        }

        public void start() {
            GLES20.glUseProgram(shaderProgram);
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glEnableVertexAttribArray(colorHandle);
            GLES20.glEnableVertexAttribArray(offsetHandle);
        }

        public void end() {
            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(colorHandle);
            GLES20.glDisableVertexAttribArray(offsetHandle);
            GLES20.glUseProgram(0);
        }
    }

    /**
     * 纳米装爆破效果
     */
    private class ExplodingRenderer {
        private int shaderProgram, positionHandle, colorHandle, timeHandle, mMVPMatrixHandle,
                diffuseHandle;

        public ExplodingRenderer() {
            shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
            positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
            colorHandle = GLES20.glGetAttribLocation(shaderProgram, "aColor");
            timeHandle = GLES20.glGetUniformLocation(shaderProgram, "time");
            mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
            diffuseHandle = GLES20.glGetUniformLocation(shaderProgram, "texture_diffuse1");
        }

        public void start() {
            GLES20.glUseProgram(shaderProgram);
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glEnableVertexAttribArray(colorHandle);
        }

        public void end() {
            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(colorHandle);
            GLES20.glUseProgram(0);
        }
    }

    /**
     * 纳米装
     */
    private class NormalRenderer {
        private int shaderProgram, positionHandle, textureHandle, mMVPMatrixHandle,
                diffuseHandle;

        public NormalRenderer() {
            shaderProgram = OpenGLUtil.createProgram(normalVertexShaderCode, normalFragmentShaderCode);
            positionHandle = 0;
            textureHandle = 1;
            mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
            diffuseHandle = GLES20.glGetUniformLocation(shaderProgram, "texture_diffuse1");
        }

        public void start() {
            GLES20.glUseProgram(shaderProgram);
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glEnableVertexAttribArray(textureHandle);
        }

        public void end() {
            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(textureHandle);
            GLES20.glUseProgram(0);
        }
    }

    /**
     * 法向量可视化
     */
    private class VisualizationRenderer {
        private int shaderProgram, positionHandle, normalHandle, modelHandle, viewHandle,
                projectionHandle;

        public VisualizationRenderer() {
            shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
            positionHandle = 0;
            normalHandle = 1;

            modelHandle = GLES20.glGetUniformLocation(shaderProgram, "model");
            viewHandle = GLES20.glGetUniformLocation(shaderProgram, "view");
            projectionHandle = GLES20.glGetUniformLocation(shaderProgram, "projection");
        }

        public void start() {
            GLES20.glUseProgram(shaderProgram);
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glEnableVertexAttribArray(normalHandle);
        }

        public void end() {
            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(normalHandle);
            GLES20.glUseProgram(0);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        switch (type) {
            case 0:
                quadsRenderer = new QuadsRenderer();
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        if (type == 3 || type == 4) {
            float ratio = (float) width / height;
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1f, 1200f);
            Matrix.setLookAtM(viewMatrix, 0, mViewPos[0], mViewPos[1], mViewPos[2],
                    0f, 0f, 0f,
                    0f, 1.0f, 0.0f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        switch (type) {
            case 0:
                drawQuads();
                break;
            case 1:
            case 2:
                draw(4);
                break;
            case 3:
            case 4:
                Matrix.setIdentityM(modelMatrix, 0);
                Matrix.translateM(modelMatrix, 0, 0f, -10.0f, 0.0f);
                if (type == 3) {
                    drawExploding();
                } else {
                    drawNormal();
                }
                break;
        }
    }

    private void drawQuads() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            quadsRenderer.start();
            FloatBuffer buffer = OpenGLUtil.createFloatBuffer(points);
            buffer.position(0);
            GLES20.glVertexAttribPointer(quadsRenderer.positionHandle, 2, GLES20.GL_FLOAT,
                    false, 5 * 4, buffer);
            buffer.position(2);
            GLES20.glVertexAttribPointer(quadsRenderer.colorHandle, 3, GLES20.GL_FLOAT,
                    false, 5 * 4, buffer);

            GLES20.glVertexAttribPointer(quadsRenderer.offsetHandle, 2, GLES20.GL_FLOAT,
                    false, 2 * 4, OpenGLUtil.createFloatBuffer(translations));

            GLES30.glVertexAttribDivisor(2, 1);

            GLES30.glDrawArraysInstanced(GLES20.GL_TRIANGLES, 0, 6,100);
            quadsRenderer.end();
        }
    }

    private void draw(int size) {

    }

    private void drawExploding() {

    }

    private void drawNormal() {

    }
}