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
 * 纹理颜色
 * @author chends create on 2019/12/16.
 */
public class TextureColorRenderer extends BaseRenderer {

    public TextureColorRenderer(Context context) {
        super(context);
        vertexShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 aPos;" +
                        "attribute vec2 aTextCoord;" +
                        "varying vec2 TextCoord;" +
                        "attribute vec3 aColor;" +
                        "varying vec3 ourColor;" +
                        "void main() {" +
                        "  gl_Position = uMVPMatrix * aPos;" +
                        "  ourColor = aColor;" +
                        "  TextCoord = aTextCoord;" +
                        "}";

        fragmentShaderCode =
                "precision mediump float;" +
                        "uniform sampler2D ourTexture;" +
                        "varying vec2 TextCoord;" +
                        "varying vec3 ourColor;" +
                        "void main() {" +
                        "  gl_FragColor = texture2D(ourTexture, TextCoord) * vec4(ourColor, 1.0);" +
                        "}";
    }

    private float[] vertices = {
            //     ---- 位置 ----       ---- 颜色 ----     - 纹理坐标 -
            0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1 - 1.0f,   // 右上
            0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1 - 0.0f,   // 右下
            -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1 - 0.0f,   // 左下
            -0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1 - 1.0f    // 左上
    };
    private short[] indices = new short[]{
            0, 1, 2, 2, 3, 0
    };

    private float imageWH;
    private int texture;
    private final float[] projectionMatrix = new float[16], tempMatrix = new float[16], vPMatrix = new float[16];

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_texture_image1);
        imageWH = (float) bitmap.getWidth() / (float) bitmap.getHeight();
        texture = OpenGLUtil.createTextureNormal(bitmap);
        bitmap.recycle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        // 根据图片宽高比和屏幕宽高比把纹理显示为正常比例（不拉伸）
        float ratio = (float) width / height;
        float w = ratio / imageWH, h = 1;
        // 不使用此段代码时，使用image2横向无法完全展示
        if (w < 0.5f) {
            // 宽度超出
            w = 0.5f;
            // 相应的需要增加垂直方向的比例
            h = 0.5f / (ratio / imageWH);
        }
        // 设置透视投影矩阵，近点是3，远点是7
        Matrix.frustumM(projectionMatrix, 0, -w, w, -h, h, 3, 7);
        Matrix.setLookAtM(tempMatrix, 0, 0, 0, 3f,
                0f, 0f, 0f,
                0f, 1f, 0f);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
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
                false, 8 * 4, vertexBuffer);

        vertexBuffer.position(3);
        int colorHandle = GLES20.glGetAttribLocation(shaderProgram, "aColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, 3, GLES20.GL_FLOAT,
                false, 8 * 4, vertexBuffer);

        vertexBuffer.position(6);
        int coordHandle = GLES20.glGetAttribLocation(shaderProgram, "aTextCoord");
        GLES20.glEnableVertexAttribArray(coordHandle);
        GLES20.glVertexAttribPointer(coordHandle, 2, GLES20.GL_FLOAT,
                false, 8 * 4, vertexBuffer);

        int textureHandle = GLES20.glGetUniformLocation(shaderProgram, "ourTexture");
        //GLES20.glUniform1i(textureHandle, 0);
        OpenGLUtil.bindTexture(textureHandle, texture, 0);

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        // 将视图转换传递给着色器
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, vPMatrix, 0);

        // 用 glDrawElements 来绘制，mVertexIndexBuffer 指定了顶点绘制顺序
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, OpenGLUtil.createShortBuffer(indices));
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
        GLES20.glDisableVertexAttribArray(textureHandle);

    }
}