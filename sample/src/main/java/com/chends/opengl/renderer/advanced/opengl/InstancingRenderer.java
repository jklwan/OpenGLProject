package com.chends.opengl.renderer.advanced.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.Build;
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
 * 实例化
 * @author chends create on 2020/3/6.
 */
public class InstancingRenderer extends BaseRenderer {
    private float[] points;
    private float[] translations;
    private float[][] translationArray;

    private String normalVertexShaderCode, normalFragmentShaderCode;
    private QuadsRenderer quadsRenderer;
    private AsteroidsRenderer asteroidsRenderer;

    private int type;
    private final float[] mMVPMatrix = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16], modelMatrix = new float[16];
    private float[] mViewPos = new float[]{0f, 0f, 55f, 1f};

    public InstancingRenderer(Context context, int type) {
        super(context);
        this.type = type;
        int vertexRes = -1, fragmentRes = -1;
        switch (type) {
            case 0:
            case 1:
            case 2:
                points = new float[]{
                        // 顶点坐标     // 颜色
                        -0.05f, 0.05f, 1.0f, 0.0f, 0.0f,
                        0.05f, -0.05f, 0.0f, 1.0f, 0.0f,
                        -0.05f, -0.05f, 0.0f, 0.0f, 1.0f,

                        -0.05f, 0.05f, 1.0f, 0.0f, 0.0f,
                        0.05f, -0.05f, 0.0f, 1.0f, 0.0f,
                        0.05f, 0.05f, 0.0f, 1.0f, 1.0f
                };
                vertexRes = R.raw.advanced_opengl_instancing_quads_vertex;
                fragmentRes = R.raw.advanced_opengl_instancing_quads_fragment;
                break;
            case 3:
                vertexRes = R.raw.advanced_opengl_instancing_asteroids_vertex;
                fragmentRes = R.raw.advanced_opengl_instancing_asteroids_fragment;
                break;
            default:
                return;
        }
        vertexShaderCode = OpenGLUtil.getShaderFromResources(context, vertexRes);
        fragmentShaderCode = OpenGLUtil.getShaderFromResources(context, fragmentRes);
    }

    private class QuadsRenderer {
        private int shaderProgram, positionHandle, colorHandle, typeHandle, offsetHandle;

        public QuadsRenderer() {
            shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
            positionHandle = 0;
            colorHandle = 1;
            typeHandle = GLES20.glGetUniformLocation(shaderProgram, "type");
            if (type != 0) {
                offsetHandle = 2;
            }
        }

        public void start() {
            GLES20.glUseProgram(shaderProgram);
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glEnableVertexAttribArray(colorHandle);
            if (type != 0) {
                GLES20.glEnableVertexAttribArray(offsetHandle);
            }
        }

        public void end() {
            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(colorHandle);
            if (type != 0) {
                GLES20.glDisableVertexAttribArray(offsetHandle);
            }
            GLES20.glUseProgram(0);
        }
    }

    /**
     * 小行星带（不使用实例化）
     */
    private class AsteroidsRenderer {
        private int shaderProgram, positionHandle, texCoordsHandle, mMVPMatrixHandle,
                diffuseHandle;

        public AsteroidsRenderer() {
            shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
            positionHandle = 0;
            texCoordsHandle = 1;
            mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
            diffuseHandle = GLES20.glGetUniformLocation(shaderProgram, "texture_diffuse1");
        }

        private void drawPlanet() {
            String planetDir = "planet";
            List<ObjectBean> planetList = LoadObjectUtil.loadObject(planetDir + "/planet.obj",
                    context.getResources(), planetDir);
            drawModel(planetList, planetDir);
        }

        private void drawRock() {
            String rockDir = "rock";
            List<ObjectBean> rockList = LoadObjectUtil.loadObject(rockDir + "/rock.obj",
                    context.getResources(), rockDir);
            drawModel(rockList, rockDir);
        }

        private void drawModel(List<ObjectBean> list, String dir) {
            if (list != null && !list.isEmpty()) {
                for (ObjectBean item : list) {
                    if (item != null) {
                        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                                false, 3 * 4, OpenGLUtil.createFloatBuffer(item.aVertices));

                        GLES20.glVertexAttribPointer(texCoordsHandle, 2, GLES20.GL_FLOAT,
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

                            OpenGLUtil.bindTexture(diffuseHandle, item.diffuse, 0);

                        }
                        // 绘制顶点
                        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, item.aVertices.length / 3);
                    }
                }
            }
        }

        public void start() {
            GLES20.glUseProgram(shaderProgram);
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glEnableVertexAttribArray(texCoordsHandle);
        }

        public void end() {
            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(texCoordsHandle);
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
            case 1:
            case 2:
                quadsRenderer = new QuadsRenderer();
                break;
            case 3:
            case 4:
                asteroidsRenderer = new AsteroidsRenderer();
                break;
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        switch (type) {
            case 3:
                break;
            case 4:
                break;
        }
        if (type == 3 || type == 4) {
            float ratio = (float) width / height;
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 0.1f, 1000f);
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
            case 1:
            case 2:
                drawQuads();
                break;
            case 3:
                drawAsteroids();
                break;
            case 4:
                drawAsteroidsPlanet();
                break;
        }
    }

    private void drawQuads() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (type == 0) {
                // 使用二维数组的方式
                translationArray = new float[100][2];
                int index = 0;
                float offset = 0.1f;
                for (int y = -10; y < 10; y += 2) {
                    for (int x = -10; x < 10; x += 2) {
                        float[] translation = new float[2];
                        translation[0] = (float) x / 10.0f + offset;
                        translation[1] = (float) y / 10.0f + offset;
                        translationArray[index++] = translation;
                    }
                }
            } else {
                translations = new float[200];
                int index = 0;
                float offset = 0.1f;
                for (int y = -10; y < 10; y += 2) {
                    for (int x = -10; x < 10; x += 2) {
                        translations[index++] = (float) x / 10.0f + offset;
                        translations[index++] = (float) y / 10.0f + offset;
                    }
                }
            }

            quadsRenderer.start();

            FloatBuffer buffer = OpenGLUtil.createFloatBuffer(points);
            buffer.position(0);
            GLES20.glVertexAttribPointer(quadsRenderer.positionHandle, 2, GLES20.GL_FLOAT,
                    false, 5 * 4, buffer);
            buffer.position(2);
            GLES20.glVertexAttribPointer(quadsRenderer.colorHandle, 3, GLES20.GL_FLOAT,
                    false, 5 * 4, buffer);
            GLES20.glUniform1i(quadsRenderer.typeHandle, type);

            if (type == 0) {
                for (int i = 0; i < 100; i++) {
                    GLES20.glUniform2fv(GLES20.glGetUniformLocation(quadsRenderer.shaderProgram,
                            "offsets[" + i + "]"), 2, OpenGLUtil.createFloatBuffer(translationArray[i]));
                }
            } else {
                GLES20.glVertexAttribPointer(quadsRenderer.offsetHandle, 2, GLES20.GL_FLOAT,
                        false, 2 * 4, OpenGLUtil.createFloatBuffer(translations));

                GLES30.glVertexAttribDivisor(2, 1);
            }

            GLES30.glDrawArraysInstanced(GLES20.GL_TRIANGLES, 0, 6, 100);
            quadsRenderer.end();
        }
    }

    private void drawAsteroids() {
        int amount = 100;
        float[][] modelMatrices = new float[amount][16];
        //srand(glfwGetTime()); // initialize random seed
        float radius = 50.0f;
        float offset = 2.5f;
        for (int i = 0; i < amount; i++) {
            Matrix.setIdentityM(modelMatrix, 0);
            // 1. translation: displace along circle with 'radius' in range [-offset, offset]
            float angle = (float) i / (float) amount * 360.0f;
            float displacement = (float) (Math.random() % (int) (2 * offset * 100)) / 100.0f - offset;
            float x = (float) Math.sin(angle) * radius + displacement;
            displacement = (float) (Math.random() % (int) (2 * offset * 100)) / 100.0f - offset;
            float y = displacement * 0.4f; // keep height of asteroid field smaller compared to width of x and z
            displacement = (float) (Math.random() % (int) (2 * offset * 100)) / 100.0f - offset;
            float z = (float) Math.cos(angle) * radius + displacement;
            Matrix.translateM(modelMatrix, 0, x, y, z);
            // 2. scale: Scale between 0.05 and 0.25f
            float scale = (float) (Math.random() % 20) / 100.0f + 0.05f;
            Matrix.scaleM(modelMatrix, 0, scale, scale, scale);

            // 3. rotation: add random rotation around a (semi)randomly picked rotation axis vector
            float rotAngle = (float) (Math.random() % 360f);
            Matrix.rotateM(modelMatrix, 0, rotAngle, 0.4f, 0.6f, 0.8f);

            // 4. now add to list of matrices
            modelMatrices[i] = modelMatrix;
        }

        asteroidsRenderer.start();
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0.0f, -3.0f, 0.0f);
        Matrix.scaleM(modelMatrix, 0, 4.0f, 4.0f, 4.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(asteroidsRenderer.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        asteroidsRenderer.drawPlanet();

        for (int i = 0; i < amount; i++) {
            Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrices[i], 0);
            Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
            GLES20.glUniformMatrix4fv(asteroidsRenderer.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
            asteroidsRenderer.drawRock();
        }

        asteroidsRenderer.end();
    }

    private void drawAsteroidsPlanet() {

    }
}