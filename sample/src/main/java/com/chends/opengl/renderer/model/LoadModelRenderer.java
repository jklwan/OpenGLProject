package com.chends.opengl.renderer.model;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.chends.opengl.model.model.ObjectBean;
import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.utils.model.LoadObjectUtil;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author chends create on 2020/1/3.
 */
public class LoadModelRenderer extends BaseRenderer {

    public LoadModelRenderer(Context context) {
        super(context);
        list = LoadObjectUtil.loadObject("deer.obj", context.getResources());
        vertexShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 aPosition;" +
                        "void main() {" +
                        "  gl_Position = uMVPMatrix * aPosition;" +
                        "}";
        fragmentShaderCode =
                "precision mediump float;" +
                        "void main() {" +
                        "  gl_FragColor = vec4(1.0);" +
                        "}";
    }

    private final float[] MVPMatrix = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16], modelMatrix = new float[16];
    private int angle = 0;
    private List<ObjectBean> list;

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        float ratio = (float) width / height;

        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1f, 10000f);
        Matrix.setLookAtM(viewMatrix, 0, 0, -500, 1200f,
                0f, 0f, 0f,
                0f, 1.0f, 0.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        int shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
        GLES20.glUseProgram(shaderProgram);

        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");

        Matrix.setIdentityM(modelMatrix, 0);
        // 创建一个旋转矩阵
        Matrix.setRotateM(modelMatrix, 0, angle, 0, 1, 0);
        // 计算
        Matrix.multiplyMM(MVPMatrix, 0, modelMatrix, 0, viewMatrix, 0);
        // 计算
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);

        // 将视图转换传递给着色器
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, MVPMatrix, 0);

        if (list != null && !list.isEmpty()) {
            for (ObjectBean item : list) {
                if (item != null) {
                    GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                            false, 3 * 4, OpenGLUtil.createFloatBuffer(item.aVertices));
                    // 绘制顶点
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, item.aVertices.length / 3);
                }
            }
        }
        GLES20.glDisableVertexAttribArray(positionHandle);
        angle += 2;
    }
}
