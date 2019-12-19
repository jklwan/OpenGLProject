package com.chends.opengl.renderer.texture;

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
 * @author chends create on 2019/12/16.
 */
public class TextureOverRenderer extends BaseRenderer {

    public TextureOverRenderer(Context context) {
        super(context);
        vertexShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 aPos;" +
                        "attribute vec2 aTextCoord1;" +
                        "attribute vec2 aTextCoord2;" +
                        "varying vec2 TextCoord1;" +
                        "varying vec2 TextCoord2;" +
                        "attribute vec3 aColor;" +
                        "varying vec3 ourColor;" +
                        "void main() {" +
                        "  gl_Position = uMVPMatrix * aPos;" +
                        "  ourColor = aColor;" +
                        "  TextCoord1 = aTextCoord1;" +
                        "  TextCoord2 = aTextCoord2;" +
                        "}";

        fragmentShaderCode =
                "precision mediump float;" +
                        "uniform sampler2D texture1;" +
                        "uniform sampler2D texture2;" +
                        "varying vec2 TextCoord1;" +
                        "varying vec2 TextCoord2;" +
                        "void main() {" +
                        "  gl_FragColor = mix(texture2D(texture1, TextCoord1),texture2D(texture2, TextCoord2),0.5);" +
                        "}";
    }

    private float[] vertices = {
            // ---- 位置 ----  - 纹理坐标 -
            -0.5f, 0.5f, 0.0f, 0.0f, 1 - 1.0f,    // 左上
            0.5f, 0.5f, 0.0f, 1.0f, 1 - 1.0f,   // 右上
            -0.5f, -0.5f, 0.0f, 0.0f, 1 - 0.0f,   // 左下
            0.5f, -0.5f, 0.0f, 1.0f, 1 - 0.0f   // 右下
    }, texture2Array = {
            0.0f, 1 - 1.0f,    // 左上
            1.0f, 1 - 1.0f,   // 右上
            0.0f, 1 - 0.0f,   // 左下
            1.0f, 1 - 0.0f   // 右下
    };

    private short[] indices = new short[]{
            0, 1, 2, 2, 3, 1
    };

    private float imageWH1, imageWH2;
    private int texture1, texture2;
    private final float[] projectionMatrix = new float[16], tempMatrix = new float[16], vPMatrix = new float[16];

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.image1);
        Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.image2);
        imageWH1 = (float) bitmap1.getWidth() / (float) bitmap1.getHeight();
        imageWH2 = (float) bitmap2.getWidth() / (float) bitmap2.getHeight();
        texture1 = OpenGLUtil.createTextureNormal(bitmap1);
        texture2 = OpenGLUtil.createTextureNormal(bitmap2);
        bitmap1.recycle();
        bitmap2.recycle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        // 根据图片宽高比和屏幕宽高比把纹理显示为正常比例（不拉伸）
        float ratio = (float) width / height;
        float w = ratio / imageWH1, h = 1;
        // 不使用此段代码时，使用image2横向无法完全展示
        if (w < 0.5f) {
            // 宽度超出
            w = 0.5f;
            // 相应的需要增加垂直方向的比例
            h = 0.5f / (ratio / imageWH1);
        }
        // 设置透视投影矩阵，近点是3，远点是7
        Matrix.frustumM(projectionMatrix, 0, -w, w, -h, h, 3, 7);
        Matrix.setLookAtM(tempMatrix, 0, 0, 0, 3f,
                0f, 0f, 0f,
                0f, 1f, 0f);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, tempMatrix, 0);

        // 根据 w，h 和第二张图片的宽高比计算纹理坐标
        if (imageWH1 != imageWH2) {
            if (imageWH1 > imageWH2) {
                // 第二张图比较高
                texture2Array[2] = texture2Array[6] = 1 + (imageWH1 - imageWH2);
            } else {
                // 第二张图比较宽
                texture2Array[5] = texture2Array[7] = 1 + (imageWH2 - imageWH1);
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        int shaderProgram = OpenGLUtil.createProgram(vertexShaderCode, fragmentShaderCode);
        GLES20.glUseProgram(shaderProgram);

        FloatBuffer vertexBuffer = OpenGLUtil.createFloatBuffer(vertices);
        vertexBuffer.position(0);
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPos");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT,
                false, 5 * 4, vertexBuffer);

        vertexBuffer.position(3);
        // 第一个纹理坐标
        int coordHandle1 = GLES20.glGetAttribLocation(shaderProgram, "aTextCoord1");
        GLES20.glEnableVertexAttribArray(coordHandle1);
        GLES20.glVertexAttribPointer(coordHandle1, 2, GLES20.GL_FLOAT,
                false, 5 * 4, vertexBuffer);
        // 第二个纹理坐标
        FloatBuffer coordBuffer = OpenGLUtil.createFloatBuffer(texture2Array);
        int coordHandle2 = GLES20.glGetAttribLocation(shaderProgram, "aTextCoord2");
        GLES20.glEnableVertexAttribArray(coordHandle2);
        GLES20.glVertexAttribPointer(coordHandle2, 2, GLES20.GL_FLOAT,
                false, 2 * 4, coordBuffer);


        int textureHandle1 = GLES20.glGetUniformLocation(shaderProgram, "texture1");
        //GLES20.glUniform1i(textureHandle, 0);
        OpenGLUtil.bindTexture(textureHandle1, texture1, 0);
        int textureHandle2 = GLES20.glGetUniformLocation(shaderProgram, "texture2");
        //GLES20.glUniform1i(textureHandle, 0);
        OpenGLUtil.bindTexture(textureHandle2, texture2, 1);

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        // 将视图转换传递给着色器
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, vPMatrix, 0);

        // 用 glDrawElements 来绘制，mVertexIndexBuffer 指定了顶点绘制顺序
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, OpenGLUtil.createShortBuffer(indices));
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureHandle1);
        GLES20.glDisableVertexAttribArray(textureHandle2);

    }
}
