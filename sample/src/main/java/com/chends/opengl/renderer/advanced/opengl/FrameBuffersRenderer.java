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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 帧缓冲
 * @author chends create on 2020/1/10.
 */
public class FrameBuffersRenderer extends BaseRenderer {

    private float[] mViewPos = new float[]{0f, 0f, 4.5f, 1f};

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
            // positions        // texture Coords
            5.0f, -0.5f, 5.0f, 2.0f, 0.0f,
            -5.0f, -0.5f, 5.0f, 0.0f, 0.0f,
            -5.0f, -0.5f, -5.0f, 0.0f, 2.0f,

            5.0f, -0.5f, 5.0f, 2.0f, 0.0f,
            -5.0f, -0.5f, -5.0f, 0.0f, 2.0f,
            5.0f, -0.5f, -5.0f, 2.0f, 2.0f
    };
    private float[] quadVertices = { // vertex attributes for a quad that fills the entire screen in Normalized Device Coordinates.
            // positions   // texCoords
            -1.0f, 1.0f, 0.0f, 1.0f,
            -1.0f, -1.0f, 0.0f, 0.0f,
            1.0f, -1.0f, 1.0f, 0.0f,

            -1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, -1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 1.0f, 1.0f
    };

    public FrameBuffersRenderer(Context context) {
        super(context);
        vertexShaderCode = OpenGLUtil.getShaderFromResources(context, R.raw.texture_vertext);
        fragmentShaderCode = OpenGLUtil.getShaderFromResources(context, R.raw.texture_fragment);
        vertexFrameShader = OpenGLUtil.getShaderFromResources(context, R.raw.advanced_opengl_frame_buffers_vertext);
        fragmentFrameShader = OpenGLUtil.getShaderFromResources(context, R.raw.advanced_opengl_frame_buffers_fragment);
    }

    private final float[] mMVPMatrix = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16], modelMatrix = new float[16];
    private int cubeTexture, floorTexture;
    private int[] frameBuffer=new int[1], frameBufferTexture = new int[1];
    private String vertexFrameShader, fragmentFrameShader;


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_depth_testing_marble);
        Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_depth_testing_metal);

        cubeTexture = OpenGLUtil.createTextureNormal(bitmap);
        floorTexture = OpenGLUtil.createTextureNormal(bitmap2);
        bitmap.recycle();
        bitmap2.recycle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        OpenGLUtil.createFrameBuffer(frameBuffer, frameBufferTexture, width, height);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);

        super.onSurfaceChanged(gl, width, height);
        float ratio = (float) width / height;

        // 设置透视投影矩阵，近点是3，远点是7
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1f, 10f);
        Matrix.setLookAtM(viewMatrix, 0, mViewPos[0], mViewPos[1], mViewPos[2],
                0f, 0f, 0f,
                0f, 1.0f, 0.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        drawCube();
        drawFloor();
        drawFrameBuffers();
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
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, -1.0f, 0.0f, -1.0f);
        //Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);

        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        int texturePosHandle = GLES20.glGetUniformLocation(shaderProgram, "texture");
        OpenGLUtil.bindTexture(texturePosHandle, cubeTexture, 0);

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

    private void drawFloor() {
        int shaderProgram = OpenGLUtil.createProgram(vertexFrameShader, fragmentFrameShader);
        GLES20.glUseProgram(shaderProgram);
        // 传入顶点坐标
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        // 纹理坐标
        int textHandle = GLES20.glGetAttribLocation(shaderProgram, "aTextCoords");
        GLES20.glEnableVertexAttribArray(textHandle);

        // 传入顶点坐标
        FloatBuffer quadVertexBuffer = OpenGLUtil.createFloatBuffer(quadVertices);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                false, 5 * 4, quadVertexBuffer);
        // 纹理坐标
        quadVertexBuffer.position(3);
        GLES20.glVertexAttribPointer(textHandle, 2, GLES20.GL_FLOAT,
                false, 5 * 4, quadVertexBuffer);

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
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

    private void drawFrameBuffers() {
        // now bind back to default framebuffer and draw a quad plane with the attached framebuffer color texture
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST); // disable depth test so screen-space quad isn't discarded due to depth test.
        // clear all relevant buffers
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // set clear color to white (not really necessery actually, since we won't be able to see behind the quad anyways)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

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
        Matrix.setIdentityM(modelMatrix, 0);
        //Matrix.translateM(modelMatrix, 0, 2.0f, 0.0f, -4.0f);
        //Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);

        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        int texturePosHandle = GLES20.glGetUniformLocation(shaderProgram, "texture");
        OpenGLUtil.bindTexture(texturePosHandle, frameBufferTexture[0], 0);

        // 绘制顶点
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textHandle);

    }
}
