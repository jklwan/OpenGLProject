package com.chends.opengl.renderer.window;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author chends create on 2019/12/7.
 */
public class TriangleMatrixRenderer extends BaseRenderer {
    private final float[] TriangleCoords = {
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
    };

    public TriangleMatrixRenderer() {
        super();
        vertexShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 aPosition;" +
                        "void main() {" +
                        "  gl_Position = uMVPMatrix * aPosition;" +
                        "}";
    }

    private final float[] projectionMatrix = new float[16], rotationMatrix = new float[16],
            translateMatrix = new float[16], tempMatrix = new float[16], vPMatrix = new float[16];

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);

        float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;

        if (width > height) {
            // 横屏
            Matrix.scaleM(projectionMatrix, 0, 1 / aspectRatio, 1, 1);
            //Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            // 竖屏or正方形
            Matrix.scaleM(projectionMatrix, 0, 1, 1 / aspectRatio, 1);
            //Matrix.orthoM(mProjectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.setIdentityM(translateMatrix, 0);

        int shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
        GLES20.glUseProgram(shaderProgram);
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        FloatBuffer vertexBuffer = OpenGLUtil.createFloatBuffer(TriangleCoords);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, vertexBuffer);
        int colorHandle = GLES20.glGetUniformLocation(shaderProgram, "vColor");
        // 设置颜色
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        // 得到形状的变换矩阵的句柄
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");

        // 旋转从0到360循环旋转
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(rotationMatrix, 0, angle, 0, 0, -1.0f);
        // 位移 在x轴上循环滑动
        float translate = (SystemClock.uptimeMillis() % 4000f) / 2000f;
        Matrix.translateM(translateMatrix, 0, translate <= 1 ? (translate - 0.5f) : (1 - (translate - 0.5f)), 0, 0);
        // 计算
        Matrix.multiplyMM(tempMatrix, 0, projectionMatrix, 0, translateMatrix, 0);
        // 计算
        Matrix.multiplyMM(vPMatrix, 0, tempMatrix, 0, rotationMatrix, 0);

        // 将视图转换传递给着色器
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, vPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
