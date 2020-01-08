package com.chends.opengl.renderer.advanced.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.chends.opengl.R;
import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import java.nio.FloatBuffer;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author chends create on 2020/1/7.
 */
public class BlendingRenderer extends BaseRenderer {

    private int type = 2;
    private float[] mViewPos = new float[]{0f, 1f, 3f, 1f};

    private float[] cubeVertices = {
            // positions          // texture Coords
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,

            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,

            -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,

            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f
    };
    private float[] planeVertices = {
            // positions          // texture Coords
            5.0f, -0.5f, 5.0f, 2.0f, 0.0f,
            -5.0f, -0.5f, 5.0f, 0.0f, 0.0f,
            -5.0f, -0.5f, -5.0f, 0.0f, 2.0f,

            5.0f, -0.5f, 5.0f, 2.0f, 0.0f,
            -5.0f, -0.5f, -5.0f, 0.0f, 2.0f,
            5.0f, -0.5f, -5.0f, 2.0f, 2.0f
    };
    private float[] transparentVertices = {
            // positions         // texture Coords
            0.0f, 0.5f, 0.0f, 0.0f, 0.0f,
            0.0f, -0.5f, 0.0f, 0.0f, 1.0f,
            1.0f, -0.5f, 0.0f, 1.0f, 1.0f,

            0.0f, 0.5f, 0.0f, 0.0f, 0.0f,
            1.0f, -0.5f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.5f, 0.0f, 1.0f, 0.0f
    };

    private float[][] translate = new float[][]{
            {-1.5f, 0.0f, -0.48f},
            {1.5f, 0.0f, 0.51f},
            {0.0f, 0.0f, 0.7f},
            {-0.3f, 0.0f, -2.3f},
            {0.5f, 0.0f, -0.6f}};

    public BlendingRenderer(Context context) {
        super(context);
        vertexShaderCode = OpenGLUtil.getShaderFromResources(context, R.raw.advanced_opengl_blending_vertext);
        fragmentShaderCode = OpenGLUtil.getShaderFromResources(context, R.raw.advanced_opengl_blending_fragment);
    }


    private final float[] mMVPMatrix = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16], modelMatrix = new float[16];
    private int cubeTexture, floorTexture, grassTexture, windowTexture;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_depth_testing_marble);
        Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_depth_testing_metal);
        Bitmap bitmap3 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_blending_grass);
        Bitmap bitmap4 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_blending_window);

        cubeTexture = OpenGLUtil.createTextureNormal(bitmap);
        floorTexture = OpenGLUtil.createTextureNormal(bitmap2);
        grassTexture = OpenGLUtil.createTextureNormal(bitmap3, true);
        windowTexture = OpenGLUtil.createTextureNormal(bitmap4);
        bitmap.recycle();
        bitmap2.recycle();
        bitmap3.recycle();
        bitmap4.recycle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        if (type == 2) {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            mViewPos[0] = 1.2f;
            mViewPos[1] = 0.6f;
            mViewPos[2] = 2;
        }
        float ratio = (float) width / height;

        // 设置透视投影矩阵，近点是3，远点是7
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1f, 10f);
        Matrix.setLookAtM(viewMatrix, 0, mViewPos[0], mViewPos[1], mViewPos[2],
                0.5f, 0f, 0f,
                0f, 1.0f, 0.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        drawFloor();

        drawCube();

        if (type == 2) {
            drawWindow();
        } else {
            drawGrass();
        }
    }

    private void drawFloor() {
        int shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
        GLES20.glUseProgram(shaderProgram);

        // 传入顶点坐标
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        // 纹理坐标
        int textHandle = GLES20.glGetAttribLocation(shaderProgram, "aTextCoords");
        GLES20.glEnableVertexAttribArray(textHandle);

        // 传入顶点坐标
        FloatBuffer planVertexBuffer = OpenGLUtil.createFloatBuffer(planeVertices);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                false, 5 * 4, planVertexBuffer);
        // 纹理坐标
        planVertexBuffer.position(3);
        GLES20.glVertexAttribPointer(textHandle, 2, GLES20.GL_FLOAT,
                false, 5 * 4, planVertexBuffer);

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        int typeHandle = GLES20.glGetUniformLocation(shaderProgram, "type");
        GLES20.glUniform1i(typeHandle, type);
        Matrix.setIdentityM(modelMatrix, 0);
        //Matrix.translateM(modelMatrix, 0, 2.0f, 0.0f, -4.0f);
        //Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);

        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        int texturePosHandle = GLES20.glGetUniformLocation(shaderProgram, "texture");
        OpenGLUtil.bindTexture(texturePosHandle, floorTexture, 0);

        // 绘制顶点
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textHandle);
    }

    private void drawCube() {
        int shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
        GLES20.glUseProgram(shaderProgram);

        // 传入顶点坐标
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        FloatBuffer vertexBuffer = OpenGLUtil.createFloatBuffer(cubeVertices);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                false, 5 * 4, vertexBuffer);
        // 纹理坐标
        int textHandle = GLES20.glGetAttribLocation(shaderProgram, "aTextCoords");
        GLES20.glEnableVertexAttribArray(textHandle);
        vertexBuffer.position(3);
        GLES20.glVertexAttribPointer(textHandle, 2, GLES20.GL_FLOAT,
                false, 5 * 4, vertexBuffer);

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        int typeHandle = GLES20.glGetUniformLocation(shaderProgram, "type");
        GLES20.glUniform1i(typeHandle, type);
        int texturePosHandle = GLES20.glGetUniformLocation(shaderProgram, "texture");
        OpenGLUtil.bindTexture(texturePosHandle, cubeTexture, 0);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, -1.0f, 0.0f, -1.0f);
        //Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);

        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 2.0f, 0.0f, 0.0f);
        //Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);

        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        OpenGLUtil.bindTexture(texturePosHandle, cubeTexture, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textHandle);
    }

    private void drawGrass() {
        int shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
        GLES20.glUseProgram(shaderProgram);

        // 传入顶点坐标
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        FloatBuffer vertexBuffer = OpenGLUtil.createFloatBuffer(transparentVertices);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                false, 5 * 4, vertexBuffer);
        // 纹理坐标
        int textHandle = GLES20.glGetAttribLocation(shaderProgram, "aTextCoords");
        GLES20.glEnableVertexAttribArray(textHandle);
        vertexBuffer.position(3);
        GLES20.glVertexAttribPointer(textHandle, 2, GLES20.GL_FLOAT,
                false, 5 * 4, vertexBuffer);

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        int typeHandle = GLES20.glGetUniformLocation(shaderProgram, "type");
        GLES20.glUniform1i(typeHandle, type);

        int texturePosHandle = GLES20.glGetUniformLocation(shaderProgram, "texture");
        OpenGLUtil.bindTexture(texturePosHandle, grassTexture, 0);

        for (float[] vegetation : translate) {
            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.translateM(modelMatrix, 0, vegetation[0], vegetation[1], vegetation[2]);
            //Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);

            Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        }

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textHandle);
    }

    private void drawWindow() {
        int shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
        GLES20.glUseProgram(shaderProgram);

        // 传入顶点坐标
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        FloatBuffer vertexBuffer = OpenGLUtil.createFloatBuffer(transparentVertices);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                false, 5 * 4, vertexBuffer);
        // 纹理坐标
        int textHandle = GLES20.glGetAttribLocation(shaderProgram, "aTextCoords");
        GLES20.glEnableVertexAttribArray(textHandle);
        vertexBuffer.position(3);
        GLES20.glVertexAttribPointer(textHandle, 2, GLES20.GL_FLOAT,
                false, 5 * 4, vertexBuffer);

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        int typeHandle = GLES20.glGetUniformLocation(shaderProgram, "type");
        GLES20.glUniform1i(typeHandle, type);

        int texturePosHandle = GLES20.glGetUniformLocation(shaderProgram, "texture");
        OpenGLUtil.bindTexture(texturePosHandle, windowTexture, 0);

        Map<Float, float[]> map = new TreeMap<>(new Comparator<Float>() {
            @Override
            public int compare(Float o1, Float o2) {
                switch (o1.compareTo(o2)){
                    case 1:
                        return -1;
                    case -1:
                        return 1;
                    default:
                        return 0;
                }
            }
        });
        for (float[] vegetation : translate) {
            float result = (float) Math.sqrt((vegetation[0] - mViewPos[0]) * (vegetation[0] - mViewPos[0]) +
                    (vegetation[1] - mViewPos[1]) * (vegetation[1] - mViewPos[1]) +
                    (vegetation[2] - mViewPos[2]) * (vegetation[2] - mViewPos[2]));
            map.put(result, vegetation);
        }

        for (Map.Entry<Float, float[]> entry : map.entrySet()) {
            float[] vegetation = entry.getValue();
            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.translateM(modelMatrix, 0, vegetation[0], vegetation[1], vegetation[2]);
            //Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);

            Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        }

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textHandle);
    }
}
