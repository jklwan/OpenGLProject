package com.chends.opengl.renderer.texture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import com.chends.opengl.R;
import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author cds created on 2019/12/13.
 */
public class TextureRenderer extends BaseRenderer {

    public TextureRenderer(Context context) {
        super(context);
        vertexShaderCode =
                "attribute vec4 aPos;" +
                        "attribute vec2 aTextCoord;" +
                        "varying vec2 TextCoord;" +
                        "void main() {" +
                        "  gl_Position = aPos;" +
                        "  TextCoord = aTextCoord;" +
                        "}";

        fragmentShaderCode =
                "precision mediump float;" +
                        "uniform sampler2D ourTexture;" +
                        "varying vec2 TextCoord;" +
                        "void main() {" +
                        "  gl_FragColor = texture2D(ourTexture, TextCoord);" +
                        "}";
    }

    private float[] vertices = {
            //---- 位置 ---   -- 纹理坐标 -
            0.5f, 0.5f, 0.0f, 1.0f, 1-1.0f,   // 右上
            0.5f, -0.5f, 0.0f, 1.0f, 1-0.0f,   // 右下
            -0.5f, -0.5f, 0.0f, 0.0f, 1-0.0f,   // 左下
            -0.5f, 0.5f, 0.0f, 0.0f, 1-1.0f    // 左上
    };
    private short[] indices = new short[]{
            0, 1, 2, 2, 3, 0
    };

    private int texture;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_texture_image);

        texture = OpenGLUtil.createTextureNormal(bitmap);
        bitmap.recycle();
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
        int coordHandle = GLES20.glGetAttribLocation(shaderProgram, "aTextCoord");
        GLES20.glEnableVertexAttribArray(coordHandle);
        GLES20.glVertexAttribPointer(coordHandle, 2, GLES20.GL_FLOAT,
                false, 5 * 4, vertexBuffer);

        int textureHandle = GLES20.glGetUniformLocation(shaderProgram, "ourTexture");
        //GLES20.glUniform1i(textureHandle, 0);
        OpenGLUtil.bindTexture(textureHandle, texture, 0);

        // 用 glDrawElements 来绘制，mVertexIndexBuffer 指定了顶点绘制顺序
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, OpenGLUtil.createShortBuffer(indices));
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
