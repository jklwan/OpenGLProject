package com.chends.opengl.renderer.advanced.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.text.TextUtils;

import com.chends.opengl.R;
import com.chends.opengl.model.model.ObjectBean;
import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.LogUtil;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.utils.model.LoadObjectUtil;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 几何着色器
 * @author chends create on 2020/3/3.
 */
public class GeometryShaderRenderer extends BaseRenderer {
    private final float[] points;

    private String geometryShaderCode, normalVertexShaderCode, normalFragmentShaderCode;
    private GeometryRenderer geometryRenderer;
    private ExplodingRenderer explodingRenderer;
    private NormalRenderer normalRenderer;
    private VisualizationRenderer visualizationRenderer;

    private int type;
    private String dir = "nanosuit";
    private List<ObjectBean> list;
    private final float[] mMVPMatrix = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16], modelMatrix = new float[16];
    private float[] mViewPos = new float[]{0f, 0f, 12f, 1f};

    public GeometryShaderRenderer(Context context, int type) {
        super(context);
        this.type = type;
        int geometryRes, vertexRes = R.raw.advanced_opengl_geometry_shader_vertex,
                fragmentRes = R.raw.advanced_opengl_geometry_shader_fragment;
        switch (type) {
            case 0:
                geometryRes = R.raw.advanced_opengl_geometry_point_geometry;
                points = new float[]{0f, 0f, 1.0f, 0.0f, 0.0f};
                break;
            case 1:
                geometryRes = R.raw.advanced_opengl_geometry_line_geometry;
                points = new float[]{
                        -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, // 左上
                        0.5f, 0.5f, 0.0f, 1.0f, 0.0f, // 右上
                        0.5f, -0.5f, 0.0f, 0.0f, 1.0f, // 右下
                        -0.5f, -0.5f, 1.0f, 1.0f, 0.0f  // 左下
                };
                break;
            case 2:
                geometryRes = R.raw.advanced_opengl_geometry_house_geometry;
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
                    bg = Color.GRAY;
                    list = LoadObjectUtil.loadObject(dir + "/nanosuit.obj", context.getResources(), dir);
                    if (type == 3) {
                        vertexRes = R.raw.advanced_opengl_geometry_exploding_vertex;
                        fragmentRes = R.raw.advanced_opengl_geometry_exploding_fragment;
                        geometryRes = R.raw.advanced_opengl_geometry_exploding_geometry;
                    } else {
                        normalVertexShaderCode = OpenGLUtil.getShaderFromResources(context,
                                R.raw.advanced_opengl_geometry_normal_vertex);
                        normalFragmentShaderCode = OpenGLUtil.getShaderFromResources(context,
                                R.raw.advanced_opengl_geometry_exploding_fragment);

                        vertexRes = R.raw.advanced_opengl_geometry_normal_visualization_vertex;
                        fragmentRes = R.raw.advanced_opengl_geometry_normal_visualization_fragment;
                        geometryRes = R.raw.advanced_opengl_geometry_normal_visualization_geometry;
                    }
                } else {
                    return;
                }
                break;
        }
        vertexShaderCode = OpenGLUtil.getShaderFromResources(context, vertexRes);
        fragmentShaderCode = OpenGLUtil.getShaderFromResources(context, fragmentRes);
        geometryShaderCode = OpenGLUtil.getShaderFromResources(context, geometryRes);

    }

    private class GeometryRenderer {
        private int shaderProgram, positionHandle, colorHandle;

        public GeometryRenderer() {
            shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode, geometryShaderCode);
            positionHandle = 0;
            colorHandle = 1;
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
     * 纳米装爆破效果
     */
    private class ExplodingRenderer {
        private int shaderProgram, positionHandle, textureHandle, timeHandle, mMVPMatrixHandle,
                diffuseHandle;

        public ExplodingRenderer() {
            shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode, geometryShaderCode);
            positionHandle = 0;
            textureHandle = 1;
            timeHandle = GLES20.glGetUniformLocation(shaderProgram, "time");
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
            shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode, geometryShaderCode);
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
            case 1:
            case 2:
                geometryRenderer = new GeometryRenderer();
                break;
            case 3:
                explodingRenderer = new ExplodingRenderer();
                break;
            case 4:
                normalRenderer = new NormalRenderer();
                visualizationRenderer = new VisualizationRenderer();
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
                draw(1);
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

    private void draw(int size) {
        geometryRenderer.start();
        FloatBuffer buffer = OpenGLUtil.createFloatBuffer(points);
        buffer.position(0);
        GLES20.glVertexAttribPointer(geometryRenderer.positionHandle, 2, GLES20.GL_FLOAT,
                false, 5 * 4, buffer);
        buffer.position(2);
        GLES20.glVertexAttribPointer(geometryRenderer.colorHandle, 3, GLES20.GL_FLOAT,
                false, 5 * 4, buffer);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, size);
        geometryRenderer.end();
    }

    private void drawExploding() {
        explodingRenderer.start();
        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(explodingRenderer.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        float time = SystemClock.uptimeMillis() / 1000f;
        //LogUtil.d("time: " + time);
        GLES20.glUniform1f(explodingRenderer.timeHandle, time);
        if (list != null && !list.isEmpty()) {
            for (ObjectBean item : list) {
                if (item != null) {
                    GLES20.glVertexAttribPointer(explodingRenderer.positionHandle, 3, GLES20.GL_FLOAT,
                            false, 3 * 4, OpenGLUtil.createFloatBuffer(item.aVertices));

                    GLES20.glVertexAttribPointer(explodingRenderer.textureHandle, 2, GLES20.GL_FLOAT,
                            false, 2 * 4, OpenGLUtil.createFloatBuffer(item.aTexCoords));
                    if (item.mtl != null) {
                        if (!TextUtils.isEmpty(item.mtl.Kd_Texture)) {
                            if (item.diffuse < 0) {
                                try {
                                    Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(
                                            dir + "/" + item.mtl.Kd_Texture));
                                    item.diffuse = OpenGLUtil.createTextureNormal(bitmap);
                                    bitmap.recycle();
                                } catch (IOException e) {
                                    LogUtil.e(e);
                                }
                            }
                        } else {
                            if (item.diffuse < 0) {
                                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_default_texture);
                                item.diffuse = OpenGLUtil.createTextureNormal(bitmap);
                                bitmap.recycle();
                            }
                        }

                        OpenGLUtil.bindTexture(explodingRenderer.diffuseHandle, item.diffuse, 0);

                    }
                    // 绘制顶点
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, item.aVertices.length / 3);
                }
            }
        }
        explodingRenderer.end();
    }

    private void drawNormal(){
        normalRenderer.start();
        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(normalRenderer.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        if (list != null && !list.isEmpty()) {
            for (ObjectBean item : list) {
                if (item != null) {
                    GLES20.glVertexAttribPointer(normalRenderer.positionHandle, 3, GLES20.GL_FLOAT,
                            false, 3 * 4, OpenGLUtil.createFloatBuffer(item.aVertices));

                    GLES20.glVertexAttribPointer(normalRenderer.textureHandle, 2, GLES20.GL_FLOAT,
                            false, 2 * 4, OpenGLUtil.createFloatBuffer(item.aTexCoords));
                    if (item.mtl != null) {
                        if (!TextUtils.isEmpty(item.mtl.Kd_Texture)) {
                            if (item.diffuse < 0) {
                                try {
                                    Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(
                                            dir + "/" + item.mtl.Kd_Texture));
                                    item.diffuse = OpenGLUtil.createTextureNormal(bitmap);
                                    bitmap.recycle();
                                } catch (IOException e) {
                                    LogUtil.e(e);
                                }
                            }
                        } else {
                            if (item.diffuse < 0) {
                                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_default_texture);
                                item.diffuse = OpenGLUtil.createTextureNormal(bitmap);
                                bitmap.recycle();
                            }
                        }

                        OpenGLUtil.bindTexture(normalRenderer.diffuseHandle, item.diffuse, 0);

                    }
                    // 绘制顶点
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, item.aVertices.length / 3);
                }
            }
        }
        normalRenderer.end();

        visualizationRenderer.start();
        GLES20.glUniformMatrix4fv(visualizationRenderer.modelHandle, 1, false, modelMatrix, 0);
        GLES20.glUniformMatrix4fv(visualizationRenderer.viewHandle, 1, false, viewMatrix, 0);
        GLES20.glUniformMatrix4fv(visualizationRenderer.projectionHandle, 1, false, projectionMatrix, 0);

        if (list != null && !list.isEmpty()) {
            for (ObjectBean item : list) {
                if (item != null) {
                    GLES20.glVertexAttribPointer(visualizationRenderer.positionHandle, 3, GLES20.GL_FLOAT,
                            false, 3 * 4, OpenGLUtil.createFloatBuffer(item.aVertices));

                    GLES20.glVertexAttribPointer(visualizationRenderer.normalHandle, 3, GLES20.GL_FLOAT,
                            false, 3 * 4, OpenGLUtil.createFloatBuffer(item.aNormals));

                    // 绘制顶点
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, item.aVertices.length / 3);
                }
            }
        }

        visualizationRenderer.end();
    }
}
