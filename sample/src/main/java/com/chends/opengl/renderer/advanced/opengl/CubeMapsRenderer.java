package com.chends.opengl.renderer.advanced.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;

import com.chends.opengl.R;
import com.chends.opengl.renderer.BaseRenderer;
import com.chends.opengl.utils.OpenGLUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 立方体贴图
 * @author chends create on 2020/1/15.
 */
public class CubeMapsRenderer extends BaseRenderer {
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

    /**
     * 立方体的8个顶点
     */
    private float[] skyboxVertices = new float[]{
            -1f, 1f, 1f, // 上左前顶点
            1f, 1f, 1f, // 上右前顶点
            -1f, 1f, -1f, // 上左后顶点
            1f, 1f, -1f, // 上右后顶点

            -1f, -1f, 1f, // 下左前顶点
            1f, -1f, 1f, // 下右前顶点
            -1f, -1f, -1f, // 下左后顶点
            1f, -1f, -1f, // 下右后顶点
    };
    // 立方体索引
    private static final short[] skyboxIndex = new short[]{
            // Front
            1, 3, 0,
            0, 3, 2,

            // Back
            4, 6, 5,
            5, 6, 7,

            // Left
            0, 2, 4,
            4, 2, 6,

            // Right
            5, 7, 1,
            1, 7, 3,

            // Top
            5, 1, 4,
            4, 1, 0,

            // Bottom
            6, 2, 7,
            7, 2, 3
    };

    private String skyboxVertexShader, skyboxFragmentShader;
    private final float[] mMVPMatrix = new float[16], projectionMatrix = new float[16],
            viewMatrix = new float[16], modelMatrix = new float[16];
    private float[] rotationMatrix = new float[16];
    private int cubeTexture, skyboxTexture;
    private SkyBoxRenderer skyBoxRenderer;
    private TextureRenderer textureRenderer;

    public CubeMapsRenderer(Context context) {
        super(context);
        vertexShaderCode = OpenGLUtil.getShaderFromResources(context, R.raw.texture_vertext);
        fragmentShaderCode = OpenGLUtil.getShaderFromResources(context, R.raw.texture_fragment);
        skyboxVertexShader = OpenGLUtil.getShaderFromResources(context, R.raw.advanced_opengl_cube_maps_vertext);
        skyboxFragmentShader = OpenGLUtil.getShaderFromResources(context, R.raw.advanced_opengl_cube_maps_fragment);
    }

    private class SkyBoxRenderer {
        private int shaderProgram, positionHandle, mMVPMatrixHandle, skyBoxPosHandle;

        public SkyBoxRenderer() {
            shaderProgram = OpenGLUtil.createProgram(skyboxVertexShader, skyboxFragmentShader);
            positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
            mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
            skyBoxPosHandle = GLES20.glGetUniformLocation(shaderProgram, "skybox");
        }

        public void start() {
            GLES20.glUseProgram(shaderProgram);
            GLES20.glEnableVertexAttribArray(positionHandle);
        }

        public void end() {
            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glUseProgram(0);
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

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_frame_buffers_container);

        cubeTexture = OpenGLUtil.createTextureNormal(bitmap);
        bitmap.recycle();

        skyboxTexture = OpenGLUtil.createTextureCube(context, new int[]{
                R.drawable.ic_cube_maps_right, R.drawable.ic_cube_maps_left, R.drawable.ic_cube_maps_top,
                R.drawable.ic_cube_maps_bottom, R.drawable.ic_cube_maps_back, R.drawable.ic_cube_maps_front
        });
        skyBoxRenderer = new SkyBoxRenderer();
        textureRenderer = new TextureRenderer();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);

        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1f, 1000f);
        Matrix.setLookAtM(viewMatrix, 0,
                0, 0, 0,
                0, 0, -1f,
                0, 1, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        drawTexture();
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        drawSkyBox();
        GLES20.glDepthFunc(GLES20.GL_LESS);
    }

    public void rotation(float[] rotationMatrix) {
        this.rotationMatrix = rotationMatrix;
    }

    private void drawSkyBox() {
        skyBoxRenderer.start();
        FloatBuffer vertexBuffer = OpenGLUtil.createFloatBuffer(skyboxVertices);
        GLES20.glVertexAttribPointer(skyBoxRenderer.positionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, vertexBuffer);

        Matrix.setIdentityM(modelMatrix, 0);
        //Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);

        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, rotationMatrix, 0, mMVPMatrix, 0);
        Matrix.rotateM(mMVPMatrix, 0, 90, 1, 0, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(skyBoxRenderer.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, skyboxTexture);

        GLES30.glUniform1i(skyBoxRenderer.skyBoxPosHandle, 0);
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 36,
                GLES30.GL_UNSIGNED_SHORT, OpenGLUtil.createShortBuffer(skyboxIndex));
        skyBoxRenderer.end();
    }

    private void drawTexture() {
        textureRenderer.start();
        FloatBuffer vertexBuffer = OpenGLUtil.createFloatBuffer(cubeVertices);
        GLES20.glVertexAttribPointer(textureRenderer.positionHandle, 3, GLES20.GL_FLOAT,
                false, 5 * 4, vertexBuffer);
        vertexBuffer.position(3);
        GLES20.glVertexAttribPointer(textureRenderer.TexCoordsHandle, 2, GLES20.GL_FLOAT,
                false, 5 * 4, vertexBuffer);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0.5f, 0.5f, -2f);
        Matrix.scaleM(modelMatrix, 0, 0.5f, 0.5f, 0.5f);
        //Matrix.rotateM(modelMatrix, 0, 45, 1.0f, 0f, 0f);

        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(textureRenderer.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        OpenGLUtil.bindTexture(textureRenderer.texturePosHandle, cubeTexture, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
        textureRenderer.end();
    }
}
