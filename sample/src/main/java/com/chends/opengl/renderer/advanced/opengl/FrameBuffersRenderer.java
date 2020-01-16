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

    private float[] mViewPos = new float[]{0f, 0f, 3f, 1f};

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

    public FrameBuffersRenderer(Context context, int type) {
        super(context);
        this.type = type;
        vertexShaderCode = OpenGLUtil.getShaderFromResources(context, R.raw.texture_vertext);
        fragmentShaderCode = OpenGLUtil.getShaderFromResources(context, R.raw.texture_fragment);
        vertexFrameShader = OpenGLUtil.getShaderFromResources(context, R.raw.advanced_opengl_frame_buffers_vertext);
        fragmentFrameShader = OpenGLUtil.getShaderFromResources(context, R.raw.advanced_opengl_frame_buffers_fragment);
    }

    private int type;

    private final float[] mMVPMatrix = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16], modelMatrix = new float[16];
    private int cubeTexture, floorTexture;
    private int frameBuffer, frameBufferTexture, renderBuffer;
    private String vertexFrameShader, fragmentFrameShader;

    private TextureRenderer textureRenderer;
    private FrameRenderer frameRenderer;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_frame_buffers_container);
        Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_depth_testing_metal);

        cubeTexture = OpenGLUtil.createTextureNormal(bitmap);
        floorTexture = OpenGLUtil.createTextureNormal(bitmap2);
        bitmap.recycle();
        bitmap2.recycle();

        textureRenderer = new TextureRenderer();
        frameRenderer = new FrameRenderer();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1f, 10f);
        Matrix.setLookAtM(viewMatrix, 0, mViewPos[0], mViewPos[1], mViewPos[2],
                0f, 0f, 0f,
                0f, 1.0f, 0.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        int[] result = OpenGLUtil.createFrameBuffer(disWidth, disHeight);
        frameBufferTexture = result[0];
        frameBuffer = result[1];
        renderBuffer = result[2];
        // 绑定到我们自定义的帧缓冲
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
        super.onDrawFrame(gl);

        drawFloor();
        drawCube();

        // 重新绑定到系统的帧缓冲
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        // 绘制我们自定义帧缓冲的内容
        drawFrameBuffers();

        release();
    }

    private void release() {
        int[] values = new int[1];
        if (frameBufferTexture > 0) {
            values[0] = frameBufferTexture;
            GLES20.glDeleteTextures(1, values, 0);
            frameBufferTexture = -1;
        }
        if (frameBuffer > 0) {
            values[0] = frameBuffer;
            GLES20.glDeleteFramebuffers(1, values, 0);
            frameBuffer = -1;
        }
        if (renderBuffer > 0) {
            values[0] = renderBuffer;
            GLES20.glDeleteRenderbuffers(1, values, 0);
            renderBuffer = -1;
        }
    }

    private class TextureRenderer {
        private int shaderProgram, positionHandle, TexCoordsHandle, mMVPMatrixHandle, texturePosHandle;

        public TextureRenderer() {
            shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
            positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
            TexCoordsHandle = GLES20.glGetAttribLocation(shaderProgram, "aTexCoords");
            mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
            texturePosHandle = GLES20.glGetUniformLocation(shaderProgram, "texture");
        }

        public void start() {
            GLES20.glUseProgram(shaderProgram);
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glEnableVertexAttribArray(TexCoordsHandle);
        }

        public void end() {
            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(TexCoordsHandle);
            GLES20.glUseProgram(0);
        }
    }

    private class FrameRenderer {
        private int shaderProgram, positionHandle, TexCoordsHandle, typeHandle, texturePosHandle,
                kernelHandle, offsetsHandle;
        private float[] kernel, offsets;

        public FrameRenderer() {
            shaderProgram = OpenGLUtil.createProgram(vertexFrameShader, fragmentFrameShader);
            positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
            TexCoordsHandle = GLES20.glGetAttribLocation(shaderProgram, "aTexCoords");
            typeHandle = GLES20.glGetUniformLocation(shaderProgram, "type");
            texturePosHandle = GLES20.glGetUniformLocation(shaderProgram, "texture");
            kernelHandle = GLES20.glGetUniformLocation(shaderProgram, "kernel");
            offsetsHandle = GLES20.glGetUniformLocation(shaderProgram, "offsets");

            if (type == 4 || type == 5 || type == 6) {
                // 创建 kernel 和 offsets
                float offset = 1.0f / 300.0f;
                offsets = new float[]{
                        -offset, offset, // 左上
                        0.0f, offset, // 正上
                        offset, offset, // 右上
                        -offset, 0.0f, // 左
                        0.0f, 0.0f, // 中
                        offset, 0.0f, // 右
                        -offset, -offset, // 左下
                        0.0f, -offset, // 正下
                        offset, -offset// 右下
                };
                switch (type) {
                    case 4:
                        kernel = new float[]{
                                -1.0f, -1.0f, -1.0f,
                                -1.0f, 9.0f, -1.0f,
                                -1.0f, -1.0f, -1.0f
                        };
                        break;
                    case 5:
                        kernel = new float[]{
                                1.0f / 16.0f, 2.0f / 16.0f, 1.0f / 16.0f,
                                2.0f / 16.0f, 4.0f / 16.0f, 2.0f / 16.0f,
                                1.0f / 16.0f, 2.0f / 16.0f, 1.0f / 16.0f
                        };
                        break;
                    case 6:
                        kernel = new float[]{
                                1.0f, 1.0f, 1.0f,
                                1.0f, -8.0f, 1.0f,
                                1.0f, 1.0f, 1.0f
                        };
                        break;
                }
            }
        }

        public void start() {
            GLES20.glUseProgram(shaderProgram);
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glEnableVertexAttribArray(TexCoordsHandle);
        }

        public void end() {
            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(TexCoordsHandle);


            GLES20.glBindTexture(frameBufferTexture, 0);
            GLES20.glUseProgram(0);
        }
    }

    private void drawFloor() {
        textureRenderer.start();

        // 传入顶点坐标
        FloatBuffer quadVertexBuffer = OpenGLUtil.createFloatBuffer(planeVertices);
        GLES20.glVertexAttribPointer(textureRenderer.positionHandle, 3, GLES20.GL_FLOAT,
                false, 5 * 4, quadVertexBuffer);
        // 纹理坐标
        quadVertexBuffer.position(3);
        GLES20.glVertexAttribPointer(textureRenderer.TexCoordsHandle, 2, GLES20.GL_FLOAT,
                false, 5 * 4, quadVertexBuffer);

        Matrix.setIdentityM(modelMatrix, 0);
        //Matrix.translateM(modelMatrix, 0, 2.0f, 0.0f, -4.0f);
        //Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);

        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(textureRenderer.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        OpenGLUtil.bindTexture(textureRenderer.texturePosHandle, floorTexture, 0);
        // 绘制顶点
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        textureRenderer.end();
    }

    private void drawCube() {
        textureRenderer.start();

        FloatBuffer vertexBuffer = OpenGLUtil.createFloatBuffer(cubeVertices);
        GLES20.glVertexAttribPointer(textureRenderer.positionHandle, 3, GLES20.GL_FLOAT,
                false, 5 * 4, vertexBuffer);
        vertexBuffer.position(3);
        GLES20.glVertexAttribPointer(textureRenderer.TexCoordsHandle, 2, GLES20.GL_FLOAT,
                false, 5 * 4, vertexBuffer);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, -1.0f, 0.0f, -1.0f);
        //Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);

        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(textureRenderer.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        OpenGLUtil.bindTexture(textureRenderer.texturePosHandle, cubeTexture, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 1.0f, 0.0f, 0.0f);
        //Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);

        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(textureRenderer.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);

        textureRenderer.end();
    }

    private void drawFrameBuffers() {
        frameRenderer.start();
        FloatBuffer quadVertexBuffer = OpenGLUtil.createFloatBuffer(quadVertices);
        GLES20.glVertexAttribPointer(frameRenderer.positionHandle, 2, GLES20.GL_FLOAT,
                false, 4 * 4, quadVertexBuffer);
        quadVertexBuffer.position(2);
        GLES20.glVertexAttribPointer(frameRenderer.TexCoordsHandle, 2, GLES20.GL_FLOAT,
                false, 4 * 4, quadVertexBuffer);

        GLES20.glUniform1i(frameRenderer.typeHandle, type);
        if (type == 4 || type == 5 || type == 6) {
            GLES20.glUniform1fv(frameRenderer.kernelHandle, 9, frameRenderer.kernel, 0);
            GLES20.glUniform2fv(frameRenderer.offsetsHandle, 9, frameRenderer.offsets, 0);
        }
        OpenGLUtil.bindTexture(frameRenderer.texturePosHandle, frameBufferTexture, 0);

        // 绘制顶点
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        frameRenderer.end();
    }
}
