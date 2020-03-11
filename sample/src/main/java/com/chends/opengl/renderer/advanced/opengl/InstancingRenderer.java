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
import java.util.Random;

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

    private String asteroidsInstancedVertexShaderCode;
    private QuadsRenderer quadsRenderer;
    private AsteroidsRenderer asteroidsRenderer;
    private AsteroidsInstancedPlanetRenderer instancedPlanetRenderer;

    private int type;
    private final float[] mMVPMatrix = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16], modelMatrix = new float[16];
    private float[] eyePos, centerPos;
    public static final String planetDir = "planet", rockDir = "rock";
    private List<ObjectBean> planetList, rockList;

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
            case 4:
                vertexRes = R.raw.advanced_opengl_instancing_asteroids_vertex;
                fragmentRes = R.raw.advanced_opengl_instancing_asteroids_fragment;
                asteroidsInstancedVertexShaderCode = OpenGLUtil.getShaderFromResources(context,
                        R.raw.advanced_opengl_instancing_asteroids_instanced_vertex);
                planetList = LoadObjectUtil.loadObject(planetDir + "/planet.obj",
                        context.getResources(), planetDir);
                rockList = LoadObjectUtil.loadObject(rockDir + "/rock.obj",
                        context.getResources(), rockDir);
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
            texCoordsHandle = 2;
            mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
            diffuseHandle = GLES20.glGetUniformLocation(shaderProgram, "texture_diffuse1");
        }

        private void drawPlanet() {
            drawModel(planetList, planetDir);
        }

        private void drawRock() {
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
     * 小行星带_小行星（使用实例化）
     */
    private class AsteroidsInstancedRenderer {
        private int shaderProgram, positionHandle, texCoordsHandle, instanceMatrixHandle, mVPMatrixHandle,
                diffuseHandle;

        public AsteroidsInstancedRenderer() {
            shaderProgram = OpenGLUtil.createProgram(asteroidsInstancedVertexShaderCode, fragmentShaderCode);
            positionHandle = 0;
            texCoordsHandle = 2;
            instanceMatrixHandle = 3;
            mVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uVPMatrix");
            diffuseHandle = GLES20.glGetUniformLocation(shaderProgram, "texture_diffuse1");
        }

        private void drawRock(float[] matrices, int amount) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (!rockList.isEmpty()) {
                    for (ObjectBean item : rockList) {
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
                                                    rockDir + "/" + item.mtl.Kd_Texture));
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

                            FloatBuffer buffer = OpenGLUtil.createFloatBuffer(matrices);
                            GLES20.glVertexAttribPointer(instanceMatrixHandle, 4, GLES20.GL_FLOAT,
                                    false, 16 * 4, buffer);
                            buffer.position(4);
                            GLES20.glVertexAttribPointer(instanceMatrixHandle + 1, 4, GLES20.GL_FLOAT,
                                    false, 16 * 4, buffer);
                            buffer.position(8);
                            GLES20.glVertexAttribPointer(instanceMatrixHandle + 2, 4, GLES20.GL_FLOAT,
                                    false, 16 * 4, buffer);
                            buffer.position(12);
                            GLES20.glVertexAttribPointer(instanceMatrixHandle + 3, 4, GLES20.GL_FLOAT,
                                    false, 16 * 4, buffer);

                            GLES30.glVertexAttribDivisor(instanceMatrixHandle, 1);
                            GLES30.glVertexAttribDivisor(instanceMatrixHandle + 1, 1);
                            GLES30.glVertexAttribDivisor(instanceMatrixHandle + 2, 1);
                            GLES30.glVertexAttribDivisor(instanceMatrixHandle + 3, 1);


                            // 绘制顶点
                            /*GLES30.glDrawElementsInstanced(GLES20.GL_TRIANGLES, item.vertexIndices.size(),
                                    GLES20.GL_UNSIGNED_INT, 0, amount);*/
                            GLES30.glDrawArraysInstanced(GLES20.GL_TRIANGLES, 0, item.aVertices.length / 3,
                                    amount);
                        }
                    }
                }
            }
        }

        public void start() {
            GLES20.glUseProgram(shaderProgram);
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glEnableVertexAttribArray(texCoordsHandle);
            GLES20.glEnableVertexAttribArray(instanceMatrixHandle);
            GLES20.glEnableVertexAttribArray(instanceMatrixHandle + 1);
            GLES20.glEnableVertexAttribArray(instanceMatrixHandle + 2);
            GLES20.glEnableVertexAttribArray(instanceMatrixHandle + 3);
        }

        public void end() {
            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(texCoordsHandle);
            GLES20.glDisableVertexAttribArray(instanceMatrixHandle);
            GLES20.glDisableVertexAttribArray(instanceMatrixHandle + 1);
            GLES20.glDisableVertexAttribArray(instanceMatrixHandle + 2);
            GLES20.glDisableVertexAttribArray(instanceMatrixHandle + 3);
            GLES20.glUseProgram(0);
        }
    }

    /**
     * 小行星带_行星（使用实例化）
     */
    private class AsteroidsInstancedPlanetRenderer {
        private int shaderProgram, positionHandle, texCoordsHandle, mMVPMatrixHandle,
                diffuseHandle;

        public AsteroidsInstancedPlanetRenderer() {
            shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
            positionHandle = 0;
            texCoordsHandle = 2;
            mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
            diffuseHandle = GLES20.glGetUniformLocation(shaderProgram, "texture_diffuse1");
        }

        private void drawPlanet() {
            if (!planetList.isEmpty()) {
                for (ObjectBean item : planetList) {
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
                                                planetDir + "/" + item.mtl.Kd_Texture));
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
                eyePos = new float[]{0, 10, 65};
                centerPos = new float[]{-10, 0, 0};
                asteroidsRenderer = new AsteroidsRenderer();
                break;
            case 4:
                eyePos = new float[]{0, 22, 165};
                centerPos = new float[]{-10, 0, 0};
                instancedPlanetRenderer = new AsteroidsInstancedPlanetRenderer();
                break;
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        switch (type) {
            case 3:
            case 4:
                float ratio = (float) width / height;
                //Matrix.perspectiveM(projectionMatrix, 0, 135, ratio, 0.1f, 1000f);
                Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1f, 1000f);
                Matrix.setLookAtM(viewMatrix, 0, eyePos[0], eyePos[1], eyePos[2],
                        centerPos[0], centerPos[1], centerPos[2],
                        0f, 1.0f, 0.0f);
                break;
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
                drawAsteroidsInstancedPlanet();
                drawAsteroidsInstanced();
                break;
        }
    }

    /**
     * 绘制矩形
     */
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

                GLES30.glVertexAttribDivisor(quadsRenderer.offsetHandle, 1);
            }

            GLES30.glDrawArraysInstanced(GLES20.GL_TRIANGLES, 0, 6, 100);
            quadsRenderer.end();
        }
    }

    private float[][] createMatrices(int amount, float radius, float offset) {
        float[][] modelMatrices = new float[amount][16];
        Random random = new Random(System.nanoTime());

        for (int i = 0; i < amount; i++) {
            float[] modelMatrix = new float[16];
            Matrix.setIdentityM(modelMatrix, 0);
            // 1. 位移：分布在半径为 'radius' 的圆形上，偏移的范围是 [-offset, offset]
            float angle = (float) i / (float) amount * 360.0f;
            float displacement = (float) (random.nextInt((int) (2 * offset * 100))) / 100.0f - offset;
            float x = (float) Math.sin(Math.toRadians(angle)) * radius + displacement;
            displacement = (float) (random.nextInt((int) (2 * offset * 100))) / 100.0f - offset;
            float y = displacement * 0.4f;
            displacement = (float) (random.nextInt((int) (2 * offset * 100))) / 100.0f - offset;
            float z = (float) Math.cos(Math.toRadians(angle)) * radius + displacement;
            Matrix.translateM(modelMatrix, 0, x, y, z);
            // 2. 缩放：在 0.05 和 0.25f 之间缩放
            float scale = (float) (random.nextInt(20)) / 100.0f + 0.05f;
            Matrix.scaleM(modelMatrix, 0, scale, scale, scale);

            // 3. 旋转：绕着一个（半）随机选择的旋转轴向量进行随机的旋转
            float rotAngle = (float) random.nextInt(360);
            Matrix.rotateM(modelMatrix, 0, rotAngle, 0.4f, 0.6f, 0.8f);

            modelMatrices[i] = modelMatrix;
        }
        return modelMatrices;
    }


    /**
     * 绘制小行星带（不使用实例化）
     */
    private void drawAsteroids() {
        int amount = 2000; // 如果是10w个加载时间需要十几秒
        float[][] modelMatrices = createMatrices(amount, 50.0f, 2.5f);

        asteroidsRenderer.start();
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0.0f, -3.0f, 0.0f);
        Matrix.scaleM(modelMatrix, 0, 5.0f, 5.0f, 5.0f);

        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(asteroidsRenderer.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        asteroidsRenderer.drawPlanet();

        for (int i = 0; i < amount; i++) {
            Matrix.setIdentityM(mMVPMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrices[i], 0);
            Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
            GLES20.glUniformMatrix4fv(asteroidsRenderer.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
            asteroidsRenderer.drawRock();
        }

        asteroidsRenderer.end();
    }

    /**
     * 绘制小行星带_小行星（使用实例化）
     */
    private void drawAsteroidsInstanced() {
        AsteroidsInstancedRenderer instancedRenderer = new AsteroidsInstancedRenderer();
        int amount = 100_000;
        float[][] modelMatrices = createMatrices(amount, 120f, 25f);
        float[] matrices = new float[amount * 16];

        for (int i = 0; i < amount; i++) {
            System.arraycopy(modelMatrices[i], 0, matrices, i * 16, 16);
        }

        instancedRenderer.start();

        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        GLES20.glUniformMatrix4fv(instancedRenderer.mVPMatrixHandle, 1, false, mMVPMatrix, 0);

        instancedRenderer.drawRock(matrices, amount);
        instancedRenderer.end();
    }

    /**
     * 绘制小行星带_行星（使用实例化）
     */
    private void drawAsteroidsInstancedPlanet() {
        instancedPlanetRenderer.start();

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0.0f, -3.0f, 0.0f);
        Matrix.scaleM(modelMatrix, 0, 5.0f, 5.0f, 5.0f);

        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(instancedPlanetRenderer.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        instancedPlanetRenderer.drawPlanet();
        instancedPlanetRenderer.end();
    }
}