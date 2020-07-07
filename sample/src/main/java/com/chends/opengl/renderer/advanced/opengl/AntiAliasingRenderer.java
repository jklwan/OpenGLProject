package com.chends.opengl.renderer.advanced.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES31;
import android.opengl.Matrix;
import android.os.Build;

import com.chends.opengl.R;
import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 离屏MSAA
 * @author chends create on 2019/12/12.
 */
public class AntiAliasingRenderer extends BaseRenderer {

    /**
     * 立方体的8个顶点
     */
    private float[] CubeCoords = new float[]{
            -0.5f, 0.5f, 0.5f, // 上左前顶点
            0.5f, 0.5f, 0.5f, // 上右前顶点
            -0.5f, 0.5f, -0.5f, // 上左后顶点
            0.5f, 0.5f, -0.5f, // 上右后顶点

            -0.5f, -0.5f, 0.5f, // 下左前顶点
            0.5f, -0.5f, 0.5f, // 下右前顶点
            -0.5f, -0.5f, -0.5f, // 下左后顶点
            0.5f, -0.5f, -0.5f, // 下右后顶点
    };

    private short[] indices = new short[]{
            0, 2, 3, 1, 5, 4, 6, 2
    };
    private short[] indices2 = new short[]{
            7, 6, 4, 5, 1, 3, 2, 6
    };
    /**
     * 颜色
     */
    private float[] colors = {
            0f, 0f, 0f, 1f,
            0f, 0f, 1f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 1f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 1f, 1f,
            1f, 1f, 0f, 1f,
            1f, 1f, 1f, 1f,
            1f, 0f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 0f, 1f, 1f,
            1f, 0f, 1f, 1f
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
    private String vertexFrameShader, fragmentFrameShader;

    public AntiAliasingRenderer(Context context) {
        super(context);
        vertexShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 aPosition;" +
                        "attribute vec4 aColor;" +
                        "varying vec4 ourColor;" +
                        "void main() {" +
                        "  gl_Position = uMVPMatrix * aPosition;" +
                        "  ourColor = aColor;" + // 将ourColor设置为我们输入的颜色
                        "}";
        fragmentShaderCode =
                "precision mediump float;" + //预定义的全局默认精度
                        "varying vec4 ourColor;" +
                        "void main() {" +
                        "  gl_FragColor = ourColor;" +
                        "}"; // 动态改变颜色
        vertexFrameShader = OpenGLUtil.getShaderFromResources(context, R.raw.advanced_opengl_antialiasing_vertex);
        fragmentFrameShader = OpenGLUtil.getShaderFromResources(context, R.raw.advanced_opengl_antialiasing_fragment);
    }

    private int angle = 0;

    private final float[] mMVPMatrix = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16], modelMatrix = new float[16];
    private int multiFramebuffer, textureColorBufferMultiSampled, mFramebuffer, mDepthBuffer, mOffscreenTexture;

    private TextureRenderer textureRenderer;
    private FrameRenderer frameRenderer;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);

        textureRenderer = new TextureRenderer();
        frameRenderer = new FrameRenderer();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        float ratio = (float) width / height;

        // 设置透视投影矩阵，近点是3，远点是7
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        // 设置观察点，当eyeZ是3时最大，是7时最小，超过这个范围时不可见
        Matrix.setLookAtM(viewMatrix, 0, 1, 1, 4f,
                0f, 0f, 0f,
                0f, 1.0f, 0.0f);
    }

    private class TextureRenderer {
        private int shaderProgram, positionHandle, colorHandle, mMVPMatrixHandle;
        private FloatBuffer vertexBuffer, colorBuffer;

        public TextureRenderer() {
            shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
            positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
            vertexBuffer = OpenGLUtil.createFloatBuffer(CubeCoords);
            colorHandle = GLES20.glGetAttribLocation(shaderProgram, "aColor");
            colorBuffer = OpenGLUtil.createFloatBuffer(colors);
            // 得到形状的变换矩阵的句柄
            mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");

        }

        public void start() {
            GLES20.glUseProgram(shaderProgram);
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glEnableVertexAttribArray(colorHandle);
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                    false, 3 * 4, vertexBuffer);
            GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT,
                    false, 4 * 4, colorBuffer);
            // 创建一个旋转矩阵
            Matrix.setRotateM(modelMatrix, 0, angle, 0, 1, 0);
            // 计算
            Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
            // 计算
            Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);

            angle += 2;
            // 将视图转换传递给着色器
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, indices.length,
                    GLES20.GL_UNSIGNED_SHORT, OpenGLUtil.createShortBuffer(indices));
            GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, indices2.length,
                    GLES20.GL_UNSIGNED_SHORT, OpenGLUtil.createShortBuffer(indices2));
        }

        public void end() {
            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(colorHandle);
            GLES20.glUseProgram(0);
        }
    }

    private class FrameRenderer {
        private int shaderProgram, positionHandle, texCoordsHandle, texturePosHandle;

        public FrameRenderer() {
            shaderProgram = OpenGLUtil.createProgram(vertexFrameShader, fragmentFrameShader);
            positionHandle = 0;
            texCoordsHandle = 1;
            texturePosHandle = GLES20.glGetUniformLocation(shaderProgram, "screenTexture");
        }

        public void start() {
            GLES20.glUseProgram(shaderProgram);
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glEnableVertexAttribArray(texCoordsHandle);
        }

        public void end() {
            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(texCoordsHandle);

            GLES20.glBindTexture(mOffscreenTexture, 0);
            GLES20.glUseProgram(0);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int[] result = OpenGLUtil.createMSAAFrameBuffer(disWidth, disHeight);
            multiFramebuffer = result[0];
            textureColorBufferMultiSampled = result[1];
            mFramebuffer = result[2];
            mDepthBuffer = result[3];
            mOffscreenTexture = result[4];
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            // 绑定到我们自定义的帧缓冲
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, multiFramebuffer);
            super.onDrawFrame(gl);
            textureRenderer.start();
            textureRenderer.end();
            GLES20.glBindFramebuffer(GLES31.GL_READ_FRAMEBUFFER, multiFramebuffer);
            GLES20.glBindFramebuffer(GLES31.GL_DRAW_FRAMEBUFFER, mFramebuffer);
            GLES31.glBlitFramebuffer(0, 0, disWidth, disHeight, 0, 0,
                    disWidth, disHeight,
                    GLES20.GL_COLOR_BUFFER_BIT,
                    GLES20.GL_NEAREST);

            // 重新绑定到系统的帧缓冲
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            // 绘制我们自定义帧缓冲的内容
            drawFrameBuffers();

            release();
        }
    }

    private void drawFrameBuffers() {
        frameRenderer.start();
        FloatBuffer quadVertexBuffer = OpenGLUtil.createFloatBuffer(quadVertices);
        GLES20.glVertexAttribPointer(frameRenderer.positionHandle, 2, GLES20.GL_FLOAT,
                false, 4 * 4, quadVertexBuffer);
        quadVertexBuffer.position(2);
        GLES20.glVertexAttribPointer(frameRenderer.texCoordsHandle, 2, GLES20.GL_FLOAT,
                false, 4 * 4, quadVertexBuffer);

        OpenGLUtil.bindTexture(frameRenderer.texturePosHandle, mOffscreenTexture, 0);

        // 绘制顶点
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        frameRenderer.end();
    }

    private void release() {
        int[] values = new int[1];
        if (multiFramebuffer > 0) {
            values[0] = multiFramebuffer;
            GLES20.glDeleteTextures(1, values, 0);
            multiFramebuffer = -1;
        }
        if (textureColorBufferMultiSampled > 0) {
            values[0] = textureColorBufferMultiSampled;
            GLES20.glDeleteTextures(1, values, 0);
            textureColorBufferMultiSampled = -1;
        }
        if (mFramebuffer > 0) {
            values[0] = mFramebuffer;
            GLES20.glDeleteFramebuffers(1, values, 0);
            mFramebuffer = -1;
        }
        if (mDepthBuffer > 0) {
            values[0] = mDepthBuffer;
            GLES20.glDeleteRenderbuffers(1, values, 0);
            mDepthBuffer = -1;
        }
        if (mOffscreenTexture > 0) {
            values[0] = mOffscreenTexture;
            GLES20.glDeleteRenderbuffers(1, values, 0);
            mOffscreenTexture = -1;
        }
    }
}
