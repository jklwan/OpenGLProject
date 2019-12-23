package com.chends.opengl.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import com.chends.opengl.interfaces.BaseListener;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * @author chends create on 2019/12/6.
 */
public class OpenGLUtil {

    private static final int SIZEOF_FLOAT = 4;
    private static final int SIZEOF_INT = 4;
    private static final int SIZEOF_SHORT = 2;
    // 初始化失败
    public static final int GL_NOT_INIT = -1;
    // 没有Texture
    public static final int GL_NOT_TEXTURE = -1;

    public static GLSurfaceView.EGLContextFactory createFactory() {
        return createFactory(null);
    }

    public static GLSurfaceView.EGLContextFactory createFactory(BaseListener<Integer> listener) {
        return new ContextFactory(listener);
    }

    private static class ContextFactory implements GLSurfaceView.EGLContextFactory {

        private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
        private BaseListener<Integer> listener;

        public ContextFactory(BaseListener<Integer> listener) {
            this.listener = listener;
        }

        @Override
        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
            EGLContext context = null;
            Integer version = null;
            try {
                context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT,
                        new int[]{EGL_CONTEXT_CLIENT_VERSION, 3, EGL10.EGL_NONE});
            } catch (Exception ex) {
                LogUtil.e(ex);
            }

            if (context == null || context == EGL10.EGL_NO_CONTEXT) {
                LogUtil.d("un support OpenGL ES 3.0 ");
                try {
                    context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT,
                            new int[]{EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE});
                } catch (Exception ex) {
                    LogUtil.e(ex);
                }
            } else {
                version = 3;
            }
            if (context == null || context == EGL10.EGL_NO_CONTEXT) {
                LogUtil.d("un support OpenGL ES 2.0 ");
            } else {
                version = 2;
            }
            if (listener != null) {
                listener.onFinish(version);
            }
            return context;
        }

        @Override
        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            if (!egl.eglDestroyContext(display, context)) {
                LogUtil.d("destroyContext false");
            }
        }
    }

    public static boolean checkOpenGL(Activity activity, int version) {
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            return am.getDeviceConfigurationInfo().reqGlEsVersion >= version;
        }
        return false;
    }

    public static boolean checkOpenGLES20(Activity activity) {
        return checkOpenGL(activity, 0x20000);
    }

    public static boolean checkOpenGLES30(Activity activity) {
        return checkOpenGL(activity, 0x30000);
    }

    public static boolean checkOpenGLES31(Activity activity) {
        return checkOpenGL(activity, 0x30001);
    }

    /**
     * 创建program
     * @param vertexSource   vertexSource
     * @param fragmentSource fragmentSource
     * @return program
     */
    public static int createProgram(String vertexSource, String fragmentSource) {
        return createProgram(vertexSource, fragmentSource, null);
    }

    public static int createProgram(String vertexSource, String fragmentSource, String[] attributes) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        checkGlError("glCreateProgram");
        if (program == 0) {
            LogUtil.e("Could not create program");
        }
        GLES20.glAttachShader(program, vertexShader);
        checkGlError("glAttachShader");
        GLES20.glAttachShader(program, pixelShader);
        checkGlError("glAttachShader");
        if (attributes != null) {
            final int size = attributes.length;
            for (int i = 0; i < size; i++) {
                GLES20.glBindAttribLocation(program, i, attributes[i]);
            }
        }

        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            LogUtil.e("Could not link program: ");
            LogUtil.e(GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        return program;
    }

    /**
     * 加载Shader
     * @param shaderType shaderType
     * @param source     source
     * @return shader
     */
    public static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        checkGlError("glCreateShader type=" + shaderType);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            LogUtil.e("Could not compile shader " + shaderType + ":" + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }

    /**
     * 检查是否出错
     * @param op op
     */
    public static void checkGlError(String op) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            String msg = op + ": glError 0x" + Integer.toHexString(error);
            LogUtil.e(msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * 创建IntBuffer
     * @param arr arr
     * @return IntBuffer
     */
    public static IntBuffer createIntBuffer(int[] arr) {
        // 初始化ByteBuffer，长度为arr数组的长度*4，因为一个int占4个字节
        ByteBuffer qbb = ByteBuffer.allocateDirect(arr.length * SIZEOF_INT);
        // 数组排列用nativeOrder
        qbb.order(ByteOrder.nativeOrder());
        IntBuffer ib = qbb.asIntBuffer();
        ib.put(arr);
        ib.position(0);
        return ib;
    }

    /**
     * 创建FloatBuffer
     * @param arr arr
     * @return FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(float[] arr) {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * SIZEOF_FLOAT);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(arr);
        fb.position(0);
        return fb;
    }

    /**
     * 创建FloatBuffer
     * @param data data
     * @return FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(ArrayList<Float> data) {
        float[] aar = new float[data.size()];
        for (int i = 0; i < aar.length; i++) {
            aar[i] = data.get(i);
        }
        return createFloatBuffer(aar);
    }

    /**
     * 创建ShortBuffer
     * @param arr arr
     * @return ShortBuffer
     */
    public static ShortBuffer createShortBuffer(short[] arr) {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * SIZEOF_SHORT);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer sb = bb.asShortBuffer();
        sb.put(arr);
        sb.position(0);
        return sb;
    }

    /**
     * 创建ShortBuffer
     * @param data data
     * @return ShortBuffer
     */
    public static ShortBuffer createShortBuffer(ArrayList<Short> data) {
        short[] aar = new short[data.size()];
        for (int i = 0; i < aar.length; i++) {
            aar[i] = data.get(i);
        }
        return createShortBuffer(aar);
    }

    /**
     * 加载bitmap纹理
     * @param bitmap bitmap图片
     * @return int
     */
    public static int createTextureNormal(Bitmap bitmap) {
        int[] texture = new int[1];
        if (bitmap != null && !bitmap.isRecycled()) {
            //生成纹理
            GLES20.glGenTextures(1, texture, 0);
            checkGlError("glGenTexture");
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_REPEAT);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            return texture[0];
        }
        return 0;
    }

    /**
     * 绑定纹理
     * @param location 句柄
     * @param texture  纹理id
     * @param index    索引
     */
    public static void bindTexture(int location, int texture, int index) {
        bindTexture(location, texture, index, GLES20.GL_TEXTURE_2D);
    }

    /**
     * 绑定纹理
     * @param location    句柄
     * @param texture     纹理值
     * @param index       绑定的位置
     * @param textureType 纹理类型
     */
    public static void bindTexture(int location, int texture, int index, int textureType) {
        // 最多支持绑定32个纹理
        if (index > 31) {
            throw new IllegalArgumentException("index must be no more than 31!");
        }
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
        GLES20.glBindTexture(textureType, texture);
        GLES20.glUniform1i(location, index);
    }
}
