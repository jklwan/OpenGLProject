package com.chends.opengl.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLES31;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Build;
import android.text.TextUtils;

import com.chends.opengl.interfaces.BaseListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import androidx.annotation.RawRes;

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
    private static Integer OpenGLVersion = null;

    public static void init(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            int ver = am.getDeviceConfigurationInfo().reqGlEsVersion;
            if (ver >= 0x30002) {
                OpenGLVersion = 5;
            } else if (ver >= 0x30001) {
                OpenGLVersion = 4;
            } else if (ver >= 0x30000) {
                OpenGLVersion = 3;
            } else if (ver >= 0x20000) {
                OpenGLVersion = 2;
            } else {
                OpenGLVersion = 0;
            }
        }
    }

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
            int version;
            while (true) {
                if (OpenGLVersion == null) {
                    version = 3;
                } else {
                    switch (OpenGLVersion) {
                        case 5:
                        case 4:
                        case 3:
                            version = 3;
                            break;
                        default:
                            version = 2;
                            break;
                    }
                }
                try {
                    context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT,
                            new int[]{EGL_CONTEXT_CLIENT_VERSION, version, EGL10.EGL_NONE});
                } catch (Exception ex) {
                    LogUtil.e(ex);
                }
                if (context == null || context == EGL10.EGL_NO_CONTEXT) {
                    version--;
                    if (version < 2) {
                        break;
                    }
                } else {
                    break;
                }
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

    /**
     * 检查OpenGL ES版本
     * @param context context
     * @param version <ul>版本<li>2:OpenGL ES 2.0</li>
     *                <li>3:OpenGL ES 3.0</li>
     *                <li>4:OpenGL ES 3.1</li>
     *                <li>5:OpenGL ES 3.2</li></ul>
     * @return 是否符合
     */
    public static boolean checkOpenGL(Context context, int version) {
        if (OpenGLVersion == null) {
            init(context);
        }
        return OpenGLVersion >= version;
    }

    public static boolean checkOpenGLES20(Context activity) {
        return checkOpenGL(activity, 2);
    }

    public static boolean checkOpenGLES30(Context activity) {
        return checkOpenGL(activity, 3);
    }

    public static boolean checkOpenGLES31(Context activity) {
        return checkOpenGL(activity, 4);
    }

    /**
     * 创建program
     * @param vertexSource   vertexSource
     * @param fragmentSource fragmentSource
     * @return program
     */
    public static int createProgram(String vertexSource, String fragmentSource) {
        return createProgram(vertexSource, fragmentSource, null, null);
    }

    public static int createProgram(String vertexSource, String fragmentSource, String[] attributes) {
        return createProgram(vertexSource, fragmentSource, null, attributes);
    }

    public static int createProgram(String vertexSource, String fragmentSource, String geometrySource) {
        return createProgram(vertexSource, fragmentSource, geometrySource, null);
    }

    public static int createProgram(String vertexSource, String fragmentSource, String geometrySource,
                                    String[] attributes) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }
        int geometryShader = 0;
        boolean loadGeometry = !TextUtils.isEmpty(geometrySource) && OpenGLVersion >= 5
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
        if (loadGeometry) {
            geometryShader = loadShader(GLES32.GL_GEOMETRY_SHADER, geometrySource);
            if (geometryShader == 0) {
                return 0;
            }
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
        if (loadGeometry) {
            GLES20.glAttachShader(program, geometryShader);
            checkGlError("glAttachShader");
        }
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
        return createTextureNormal(bitmap, false);
    }

    /**
     * 加载bitmap纹理
     * @param bitmap bitmap图片
     * @return int
     */
    public static int createTextureNormal(Bitmap bitmap, boolean withAlpha) {
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
                    withAlpha ? GLES20.GL_CLAMP_TO_EDGE : GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    withAlpha ? GLES20.GL_CLAMP_TO_EDGE : GLES20.GL_REPEAT);
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

    /**
     * 从资源文件中读取shader字符串
     * @param rawResId rawResId
     * @return string
     */
    public static String getShaderFromResources(Context context, @RawRes int rawResId) {
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().openRawResource(rawResId);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return getShaderStringFromStream(inputStream);
    }

    /**
     * 从文件路径中读取shader字符串
     * @param filePath filePath
     * @return string
     */
    public static String getShaderFromFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if (file.isDirectory()) {
            return null;
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return getShaderStringFromStream(inputStream);
    }

    /**
     * 从Assets文件夹中读取shader字符串
     * @param context context
     * @param path    shader相对路径
     * @return string
     */
    public static String getShaderFromAssets(Context context, String path) {
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().getAssets().open(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getShaderStringFromStream(inputStream);
    }

    /**
     * 从输入流中读取shader字符创
     * @param stream input stream
     * @return string
     */
    private static String getShaderStringFromStream(InputStream stream) {
        if (stream == null) {
            return null;
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            reader.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建帧缓冲
     * @param width  width
     * @param height height
     * @return texture,
     */
    public static int[] createFrameBuffer(int width, int height) {
        int[] values = new int[1];
        // 纹理缓冲
        GLES20.glGenTextures(1, values, 0);
        int mOffscreenTexture = values[0];   // expected > 0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mOffscreenTexture);

        // 创建纹理存储。
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        // 设置参数。
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);

        // 自定义帧缓冲
        GLES20.glGenFramebuffers(1, values, 0);
        int mFramebuffer = values[0];    // expected > 0
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebuffer);

        // 深度缓冲
        GLES20.glGenRenderbuffers(1, values, 0);
        int mDepthBuffer = values[0];    // expected > 0
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mDepthBuffer);

        // 为深度缓冲区分配存储空间。
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16,
                width, height);

        // 将深度缓冲区和纹理（颜色缓冲区）附加到帧缓冲区对象。
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, mDepthBuffer);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mOffscreenTexture, 0);

        // 判断是否创建成功
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            // 未创建成功
            throw new RuntimeException("Framebuffer not complete, status=" + status);
        }
        // 切换到默认缓冲
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        return new int[]{mOffscreenTexture, mFramebuffer, mDepthBuffer};
    }

    /**
     * 创建帧缓冲
     * @param width  width
     * @param height height
     * @return texture,
     */
    public static int[] createMSAAFrameBuffer(int width, int height) {
        int[] values = new int[1];
        // 深度缓冲
        GLES20.glGenRenderbuffers(1, values, 0);
        int mDepthBuffer = values[0];    // expected > 0
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mDepthBuffer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 为深度缓冲区分配存储空间。
            GLES31.glRenderbufferStorageMultisample(GLES20.GL_RENDERBUFFER, 4, GLES30.GL_RGBA8,
                    width, height);
        }
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);

        //创建缓冲区对象，附加纹理和深度
        GLES20.glGenFramebuffers(1, values, 0);
        int multiFramebuffer = values[0];    // expected > 0
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, multiFramebuffer);

        GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_RENDERBUFFER, mDepthBuffer);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

        // 纹理缓冲
        GLES20.glGenTextures(1, values, 0);
        int mOffscreenTexture = values[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mOffscreenTexture);

        // 创建纹理存储。
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        // 设置参数。
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return new int[]{multiFramebuffer, mDepthBuffer, mOffscreenTexture};
        /*int[] values = new int[1];
        // 深度缓冲
        GLES20.glGenRenderbuffers(1, values, 0);
        int mDepthBuffer = values[0];    // expected > 0
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mDepthBuffer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 为深度缓冲区分配存储空间。
            GLES31.glRenderbufferStorageMultisample(GLES20.GL_RENDERBUFFER, 4, GLES20.GL_DEPTH_COMPONENT16,
                    width, height);
        }
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);

        // 纹理缓冲
        GLES20.glGenTextures(1, values, 0);
        int mOffscreenTexture = values[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mOffscreenTexture);

        // 创建纹理存储。
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        // 设置参数。
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        //创建缓冲区对象，附加纹理和深度
        GLES20.glGenFramebuffers(1, values, 0);
        int multiFramebuffer = values[0];    // expected > 0
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, multiFramebuffer);

        // 将深度缓冲区和纹理（颜色缓冲区）附加到帧缓冲区对象。
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, mDepthBuffer);
        GLES32.glFramebufferTexture2D();

        GLES20.glGenTextures(1, values, 0);
        int textureColorBufferMultiSampled = values[0];   // expected > 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            GLES20.glBindTexture(GLES31.GL_TEXTURE_2D_MULTISAMPLE, textureColorBufferMultiSampled);

            // 创建纹理存储。
            GLES31.glTexStorage2DMultisample(GLES31.GL_TEXTURE_2D_MULTISAMPLE, 4, GLES20.GL_RGBA,
                    width, height, false);

            GLES20.glBindTexture(GLES31.GL_TEXTURE_2D_MULTISAMPLE, 0);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES31.GL_TEXTURE_2D_MULTISAMPLE, textureColorBufferMultiSampled, 0);
        }



        // 判断是否创建成功
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            // 未创建成功
            throw new RuntimeException("Framebuffer not complete, status=" + status);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        // 自定义帧缓冲
        GLES20.glGenFramebuffers(1, values, 0);
        int mFramebuffer = values[0];    // expected > 0
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebuffer);



        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mOffscreenTexture, 0);

        // 判断是否创建成功
        status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            // 未创建成功
            throw new RuntimeException("Framebuffer not complete, status=" + status);
        }
        // 切换到默认缓冲
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        return new int[]{multiFramebuffer, textureColorBufferMultiSampled, mFramebuffer, mDepthBuffer, mOffscreenTexture};*/
    }

    /**
     * 立方体贴图
     * @param context context
     * @param resIds  贴图集合，顺序是：
     *                <ul><li>右{@link GLES20#GL_TEXTURE_CUBE_MAP_POSITIVE_X}</li>
     *                <li>左{@link GLES20#GL_TEXTURE_CUBE_MAP_NEGATIVE_X}</li>
     *                <li>上{@link GLES20#GL_TEXTURE_CUBE_MAP_POSITIVE_Y}</li>
     *                <li>下{@link GLES20#GL_TEXTURE_CUBE_MAP_NEGATIVE_Y}</li>
     *                <li>后{@link GLES20#GL_TEXTURE_CUBE_MAP_POSITIVE_Z}</li>
     *                <li>前{@link GLES20#GL_TEXTURE_CUBE_MAP_NEGATIVE_Z}</li></ul>
     * @return int
     */
    public static int createTextureCube(Context context, int[] resIds) {
        if (resIds != null && resIds.length >= 6) {
            int[] texture = new int[1];
            //生成纹理
            GLES20.glGenTextures(1, texture, 0);
            checkGlError("glGenTexture");
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, texture[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            if (OpenGLVersion > 2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_WRAP_R, GLES20.GL_CLAMP_TO_EDGE);
            }

            Bitmap bitmap;
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            for (int i = 0; i < resIds.length; i++) {
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        resIds[i], options);
                if (bitmap == null) {
                    LogUtil.w("Resource ID " + resIds[i] + " could not be decoded.");
                    GLES20.glDeleteTextures(1, texture, 0);
                    return 0;
                }
                GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, bitmap, 0);
                bitmap.recycle();
            }
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            return texture[0];
        }
        return 0;
    }

}
