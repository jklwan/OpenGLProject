#include <cstring>
#include <sstream>
#include "OpenGLUtil.h"


void OpenGLUtil::surfaceCreated() {
    glClearColor(defaultBg[0], defaultBg[1],
                 defaultBg[2], defaultBg[3]);
}

void OpenGLUtil::drawFrame(jint width, jint height) {
    // 设置显示范围
    glViewport(0, 0, width, height);
    //GLES20.glEnable(GLES20.GL_CULL_FACE);
    glEnable(GL_DEPTH_TEST);
    // 清屏
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
}

GLuint OpenGLUtil::createProgram(JNIEnv *env) {
    return createProgram(env, defaultVertexShaderCode, defaultFragmentShaderCode, nullptr, nullptr);
}

GLuint OpenGLUtil::createProgram(JNIEnv *env, const char *vertexSource, const char *fragmentSource) {
    return createProgram(env, vertexSource, fragmentSource, nullptr, nullptr);
}

GLuint OpenGLUtil::createProgram(JNIEnv *env, const char *vertexSource, const char *fragmentSource,
                                 jobjectArray attributes) {
    return createProgram(env, vertexSource, fragmentSource, nullptr, attributes);
}

GLuint OpenGLUtil::createProgram(JNIEnv *env, const char *vertexSource, const char *fragmentSource,
                                 const char *geometrySource) {
    return createProgram(env, vertexSource, fragmentSource, geometrySource, nullptr);
}

GLuint OpenGLUtil::createProgram(JNIEnv *env, const char *vertexSource, const char *fragmentSource,
                                 const char *geometrySource, jobjectArray attributes) {
    GLuint vertexShader = loadShader(GL_VERTEX_SHADER, vertexSource);
    if (vertexShader == 0) {
        return 0;
    }
    GLuint pixelShader = loadShader(GL_FRAGMENT_SHADER, fragmentSource);
    if (pixelShader == 0) {
        return 0;
    }
    GLuint geometryShader = 0;
    jboolean loadGeometry = geometrySource != nullptr;
    /*boolean loadGeometry = !TextUtils.isEmpty(geometrySource) && OpenGLVersion >= 5
                           && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;*/
    if (loadGeometry) {
        geometryShader = loadShader(GL_GEOMETRY_SHADER, geometrySource);
        if (geometryShader == 0) {
            return 0;
        }
    }
    GLuint program = glCreateProgram();
    checkGlError("glCreateProgram");
    if (program == 0) {
        LOGE("Could not create program");
    }
    glAttachShader(program, vertexShader);
    checkGlError("glAttachShader");
    glAttachShader(program, pixelShader);
    checkGlError("glAttachShader");
    if (loadGeometry) {
        glAttachShader(program, geometryShader);
        checkGlError("glAttachShader");
    }
    if (attributes != nullptr) {
        jsize size = env->GetArrayLength(attributes);
        for (GLuint i = 0; i < size; i++) {
            glBindAttribLocation(program, i, (GLchar *) &attributes[i]);
        }
    }

    glLinkProgram(program);
    GLint linkStatus;
    glGetProgramiv(program, GL_LINK_STATUS, &linkStatus);
    if (linkStatus != GL_TRUE) {
        GLchar compilerSpew[256];
        glGetProgramInfoLog(program, sizeof(compilerSpew), nullptr, compilerSpew);
        LOGE("Could not link program: %p", compilerSpew);
        glDeleteProgram(program);
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
GLuint OpenGLUtil::loadShader(jint shaderType, const char *source) {
    GLuint shader = glCreateShader(shaderType);
    checkGlError((&"glCreateShader type="[shaderType]));

    glShaderSource(shader, 1, &source, nullptr);
    glCompileShader(shader);

    GLint compiled;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
    if (compiled == 0) {
        GLchar compilerSpew[256];
        glGetShaderInfoLog(shader, sizeof(compilerSpew), nullptr, compilerSpew);
        LOGE("Could not compile shader %d : %p", shaderType, compilerSpew);
        glDeleteShader(shader);
        shader = 0;
    }
    return shader;
}

/**
 * 检查是否出错
 * @param op op
 */
void OpenGLUtil::checkGlError(const char *op) {
    GLenum error = glGetError();
    if (error != GL_NO_ERROR) {
        LOGE("%p : glError (0x%x)", op, error);
        throw std::runtime_error("glError");
    }
}


/**
 * 创建IntBuffer
 * @param arr arr
 * @return IntBuffer
 */
/*
public

static IntBuffer createIntBuffer(int[] arr) {
    // 初始化ByteBuffer，长度为arr数组的长度*4，因为一个int占4个字节
    ByteBuffer qbb = ByteBuffer.allocateDirect(arr.length * SIZEOF_INT);
    // 数组排列用nativeOrder
    qbb.order(ByteOrder.nativeOrder());
    IntBuffer ib = qbb.asIntBuffer();
    ib.put(arr);
    ib.position(0);
    return ib;
}

*/
/**
 * 创建FloatBuffer
 * @param arr arr
 * @return FloatBuffer
 */

/*public

static FloatBuffer createFloatBuffer(float[] arr) {
    ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * SIZEOF_FLOAT);
    bb.order(ByteOrder.nativeOrder());
    FloatBuffer fb = bb.asFloatBuffer();
    fb.put(arr);
    fb.position(0);
    return fb;
}*/


/**
 * 创建FloatBuffer
 * @param data data
 * @return FloatBuffer
 *//*

public

static FloatBuffer createFloatBuffer(ArrayList <Float> data) {
    float[]
    aar = new float[data.size()];
    for (int i = 0; i < aar.length; i++) {
        aar[i] = data.get(i);
    }
    return createFloatBuffer(aar);
}

*/
/**
 * 创建ShortBuffer
 * @param arr arr
 * @return ShortBuffer
 *//*

public

static ShortBuffer createShortBuffer(short[] arr) {
    ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * SIZEOF_SHORT);
    bb.order(ByteOrder.nativeOrder());
    ShortBuffer sb = bb.asShortBuffer();
    sb.put(arr);
    sb.position(0);
    return sb;
}

*/
/**
 * 创建ShortBuffer
 * @param data data
 * @return ShortBuffer
 *//*

public

static ShortBuffer createShortBuffer(ArrayList <Short> data) {
    short[]
    aar = new short[data.size()];
    for (int i = 0; i < aar.length; i++) {
        aar[i] = data.get(i);
    }
    return createShortBuffer(aar);
}

*/
/**
 * 加载bitmap纹理
 * @param bitmap bitmap图片
 * @return int
 *//*

public

static int createTextureNormal(Bitmap bitmap) {
    return createTextureNormal(bitmap, false);
}

*/
/**
 * 加载bitmap纹理
 * @param bitmap bitmap图片
 * @return int
 *//*

public

static int createTextureNormal(Bitmap bitmap, boolean withAlpha) {
    int[]
    texture = new int[1];
    if (bitmap != null && !bitmap.isRecycled()) {
        //生成纹理
        GLES20.glGenTextures(1, texture, 0);
        checkGlError("glGenTexture");
        //生成纹理
        GLES20.glBindTexture(GLES20.
        GL_TEXTURE_2D, texture[0]);
        GLES20.glTexParameteri(GLES20.
        GL_TEXTURE_2D, GLES20.
        GL_TEXTURE_MIN_FILTER,
                GLES20.
        GL_LINEAR);
        GLES20.glTexParameteri(GLES20.
        GL_TEXTURE_2D, GLES20.
        GL_TEXTURE_MAG_FILTER,
                GLES20.
        GL_LINEAR);
        GLES20.glTexParameteri(GLES20.
        GL_TEXTURE_2D, GLES20.
        GL_TEXTURE_WRAP_S,
                withAlpha ? GLES20.GL_CLAMP_TO_EDGE : GLES20.
        GL_REPEAT);
        GLES20.glTexParameteri(GLES20.
        GL_TEXTURE_2D, GLES20.
        GL_TEXTURE_WRAP_T,
                withAlpha ? GLES20.GL_CLAMP_TO_EDGE : GLES20.
        GL_REPEAT);
        //根据以上指定的参数，生成一个2D纹理
        GLUtils.texImage2D(GLES20.
        GL_TEXTURE_2D, 0, bitmap, 0);
        return texture[0];
    }
    return 0;
}

*/
/**
 * 绑定纹理
 * @param location 句柄
 * @param texture  纹理id
 * @param index    索引
 *//*

public

static void bindTexture(int location, int texture, int index) {
    bindTexture(location, texture, index, GLES20.
    GL_TEXTURE_2D);
}

*/
/**
 * 绑定纹理
 * @param location    句柄
 * @param texture     纹理值
 * @param index       绑定的位置
 * @param textureType 纹理类型
 *//*

public

static void bindTexture(int location, int texture, int index, int textureType) {
    // 最多支持绑定32个纹理
    if (index > 31) {
        throw new IllegalArgumentException("index must be no more than 31!");
    }
    GLES20.glActiveTexture(GLES20.
    GL_TEXTURE0 + index);
    GLES20.glBindTexture(textureType, texture);
    GLES20.glUniform1i(location, index);
}

*/
/**
 * 从资源文件中读取shader字符串
 * @param rawResId rawResId
 * @return string
 *//*

public

static String getShaderFromResources(Context context,

@
RawRes int rawResId
) {
InputStream inputStream = null;
try {
inputStream = context.getResources().openRawResource(rawResId);
} catch (Resources.
NotFoundException e
) {
e.

printStackTrace();

}
return
getShaderStringFromStream(inputStream);
}

*/
/**
 * 从文件路径中读取shader字符串
 * @param filePath filePath
 * @return string
 *//*

public

static String getShaderFromFile(String filePath) {
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

*/
/**
 * 从Assets文件夹中读取shader字符串
 * @param context context
 * @param path    shader相对路径
 * @return string
 *//*

public

static String getShaderFromAssets(Context context, String path) {
    InputStream inputStream = null;
    try {
        inputStream = context.getResources().getAssets().open(path);
    } catch (IOException e) {
        e.printStackTrace();
    }
    return getShaderStringFromStream(inputStream);
}

*/
/**
 * 从输入流中读取shader字符创
 * @param stream input stream
 * @return string
 *//*

private

static String getShaderStringFromStream(InputStream stream) {
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

*/
/**
 * 创建帧缓冲
 * @param width  width
 * @param height height
 * @return texture,
 *//*

public static int[]

createFrameBuffer(int width, int height) {
    int[]
    values = new int[1];
    // 纹理缓冲
    GLES20.glGenTextures(1, values, 0);
    int mOffscreenTexture = values[0];   // expected > 0
    GLES20.glBindTexture(GLES20.
    GL_TEXTURE_2D, mOffscreenTexture);

    // 创建纹理存储。
    GLES20.glTexImage2D(GLES20.
    GL_TEXTURE_2D, 0, GLES20.
    GL_RGBA, width, height, 0,
            GLES20.
    GL_RGBA, GLES20.
    GL_UNSIGNED_BYTE, null);

    // 设置参数。
    GLES20.glTexParameterf(GLES20.
    GL_TEXTURE_2D, GLES20.
    GL_TEXTURE_MIN_FILTER,
            GLES20.
    GL_LINEAR);
    GLES20.glTexParameterf(GLES20.
    GL_TEXTURE_2D, GLES20.
    GL_TEXTURE_MAG_FILTER,
            GLES20.
    GL_LINEAR);
    GLES20.glTexParameteri(GLES20.
    GL_TEXTURE_2D, GLES20.
    GL_TEXTURE_WRAP_S,
            GLES20.
    GL_CLAMP_TO_EDGE);
    GLES20.glTexParameteri(GLES20.
    GL_TEXTURE_2D, GLES20.
    GL_TEXTURE_WRAP_T,
            GLES20.
    GL_CLAMP_TO_EDGE);

    // 自定义帧缓冲
    GLES20.glGenFramebuffers(1, values, 0);
    int mFramebuffer = values[0];    // expected > 0
    GLES20.glBindFramebuffer(GLES20.
    GL_FRAMEBUFFER, mFramebuffer);

    // 深度缓冲
    GLES20.glGenRenderbuffers(1, values, 0);
    int mDepthBuffer = values[0];    // expected > 0
    GLES20.glBindRenderbuffer(GLES20.
    GL_RENDERBUFFER, mDepthBuffer);

    // 为深度缓冲区分配存储空间。
    GLES20.glRenderbufferStorage(GLES20.
    GL_RENDERBUFFER, GLES20.
    GL_DEPTH_COMPONENT16,
            width, height);

    // 将深度缓冲区和纹理（颜色缓冲区）附加到帧缓冲区对象。
    GLES20.glFramebufferRenderbuffer(GLES20.
    GL_FRAMEBUFFER, GLES20.
    GL_DEPTH_ATTACHMENT,
            GLES20.
    GL_RENDERBUFFER, mDepthBuffer);
    GLES20.glFramebufferTexture2D(GLES20.
    GL_FRAMEBUFFER, GLES20.
    GL_COLOR_ATTACHMENT0,
            GLES20.
    GL_TEXTURE_2D, mOffscreenTexture, 0);

    // 判断是否创建成功
    int status = GLES20.glCheckFramebufferStatus(GLES20.
    GL_FRAMEBUFFER);
    if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
        // 未创建成功
        throw new RuntimeException("Framebuffer not complete, status=" + status);
    }
    // 切换到默认缓冲
    GLES20.glBindFramebuffer(GLES20.
    GL_FRAMEBUFFER, 0);

    return new int[]{mOffscreenTexture, mFramebuffer, mDepthBuffer};
}

*/
/**
 * 创建帧缓冲
 * @param width  width
 * @param height height
 * @return texture,
 *//*

public static int[]

createMSAAFrameBuffer(int width, int height) {
    int[]
    values = new int[1];
    int status;
    GLES20.glGenFramebuffers(1, values, 0);
    int framebuffer = values[0];    // expected > 0
    GLES20.glBindFramebuffer(GLES20.
    GL_FRAMEBUFFER, framebuffer);

    // 纹理缓冲
    GLES20.glGenTextures(1, values, 0);
    int textureColorBufferMultiSampled = values[0];   // expected > 0
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        GLES20.glBindTexture(GLES31.GL_TEXTURE_2D_MULTISAMPLE, textureColorBufferMultiSampled);
        GLES31.glTexStorage2DMultisample(GLES31.GL_TEXTURE_2D_MULTISAMPLE, 4, GLES31.
        GL_RGBA8,
                width, height, true);
        //GLES20.glBindTexture(GLES31.GL_TEXTURE_2D_MULTISAMPLE, 0);
        GLES20.glFramebufferTexture2D(GLES20.
        GL_FRAMEBUFFER, GLES20.
        GL_COLOR_ATTACHMENT0,
                GLES31.GL_TEXTURE_2D_MULTISAMPLE, textureColorBufferMultiSampled, 0);
        //GLES31.glDrawBuffers(1, new int[]{ GLES31.GL_COLOR_ATTACHMENT0 }, 0);
    }
    status = GLES20.glCheckFramebufferStatus(GLES20.
    GL_FRAMEBUFFER);
    if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
        // 未创建成功
        throw new RuntimeException("Framebuffer not complete, status=" + status);
    }

    // rbo
    GLES20.glGenRenderbuffers(1, values, 0);
    int rbo = values[0];    // expected > 0
    GLES20.glBindRenderbuffer(GLES20.
    GL_RENDERBUFFER, rbo);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        int[]
        param = new int[1];
        GLES30.glGetIntegerv(GLES30.
        GL_MAX_SAMPLES, param, 0);
        GLES31.glRenderbufferStorageMultisample(GLES20.
        GL_RENDERBUFFER, param[0], GLES32.
        GL_DEPTH24_STENCIL8,
                width, height);
    }
    //GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        GLES20.glFramebufferRenderbuffer(GLES20.
        GL_FRAMEBUFFER, GLES30.
        GL_DEPTH_STENCIL_ATTACHMENT,
                GLES20.
        GL_RENDERBUFFER, rbo);
    }
    status = GLES20.glCheckFramebufferStatus(GLES20.
    GL_FRAMEBUFFER);
    if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
        // 未创建成功
        throw new RuntimeException("Framebuffer not complete, status=" + status);
    }

    GLES20.glBindFramebuffer(GLES20.
    GL_FRAMEBUFFER, 0);

    // fbo
    GLES20.glGenFramebuffers(1, values, 0);
    int intermediateFBO = values[0];    // expected > 0
    GLES20.glBindFramebuffer(GLES20.
    GL_FRAMEBUFFER, intermediateFBO);

    GLES20.glGenTextures(1, values, 0);
    int screenTexture = values[0];
    GLES20.glBindTexture(GLES20.
    GL_TEXTURE_2D, screenTexture);

    // 设置参数。
    GLES20.glTexParameterf(GLES20.
    GL_TEXTURE_2D, GLES20.
    GL_TEXTURE_MIN_FILTER, GLES20.
    GL_LINEAR);
    GLES20.glTexParameterf(GLES20.
    GL_TEXTURE_2D, GLES20.
    GL_TEXTURE_MAG_FILTER, GLES20.
    GL_LINEAR);
    GLES20.glTexParameteri(GLES20.
    GL_TEXTURE_2D, GLES20.
    GL_TEXTURE_WRAP_S, GLES20.
    GL_CLAMP_TO_EDGE);
    GLES20.glTexParameteri(GLES20.
    GL_TEXTURE_2D, GLES20.
    GL_TEXTURE_WRAP_T, GLES20.
    GL_CLAMP_TO_EDGE);
    GLES20.glTexImage2D(GLES20.
    GL_TEXTURE_2D, 0, GLES20.
    GL_RGBA, width, height, 0,
            GLES20.
    GL_RGBA, GLES20.
    GL_UNSIGNED_BYTE, null);
    GLES20.glFramebufferTexture2D(GLES20.
    GL_FRAMEBUFFER, GLES20.
    GL_COLOR_ATTACHMENT0,
            GLES20.
    GL_TEXTURE_2D, screenTexture, 0);

    // 判断是否创建成功
    status = GLES20.glCheckFramebufferStatus(GLES20.
    GL_FRAMEBUFFER);
    if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.
    GL_FRAMEBUFFER_COMPLETE) {
        // 未创建成功
        throw new RuntimeException("Framebuffer not complete, status=" + status);
    }
    // 切换到默认缓冲
    GLES20.glBindFramebuffer(GLES20.
    GL_FRAMEBUFFER, 0);

    return new int[]{framebuffer, textureColorBufferMultiSampled, intermediateFBO, rbo,
                     screenTexture};
}

*/
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
 *//*

public

static int createTextureCube(Context context, int[] resIds) {
    if (resIds != null && resIds.length >= 6) {
        int[]
        texture = new int[1];
        //生成纹理
        GLES20.glGenTextures(1, texture, 0);
        checkGlError("glGenTexture");
        //生成纹理
        GLES20.glBindTexture(GLES20.
        GL_TEXTURE_CUBE_MAP, texture[0]);
        GLES20.glTexParameteri(GLES20.
        GL_TEXTURE_CUBE_MAP, GLES20.
        GL_TEXTURE_MAG_FILTER, GLES20.
        GL_LINEAR);
        GLES20.glTexParameteri(GLES20.
        GL_TEXTURE_CUBE_MAP, GLES20.
        GL_TEXTURE_MIN_FILTER, GLES20.
        GL_LINEAR);
        GLES20.glTexParameteri(GLES20.
        GL_TEXTURE_CUBE_MAP, GLES20.
        GL_TEXTURE_WRAP_S, GLES20.
        GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.
        GL_TEXTURE_CUBE_MAP, GLES20.
        GL_TEXTURE_WRAP_T, GLES20.
        GL_CLAMP_TO_EDGE);
        if (OpenGLVersion > 2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            GLES20.glTexParameteri(GLES20.
            GL_TEXTURE_CUBE_MAP, GLES30.
            GL_TEXTURE_WRAP_R, GLES20.
            GL_CLAMP_TO_EDGE);
        }

        Bitmap bitmap;
        final
        BitmapFactory.Options
        options = new BitmapFactory.Options();
        options.inScaled = false;
        for (int i = 0; i < resIds.length; i++) {
            bitmap = BitmapFactory.decodeResource(context.getResources(),
                                                  resIds[i], options);
            if (bitmap == null) {
                LogUtil.w("Resource ID " + resIds[i] + " could not be decoded.");
                GLES20.glDeleteTextures(1, texture, 0);
                return 0;
            }
            GLUtils.texImage2D(GLES20.
            GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, bitmap, 0);
            bitmap.recycle();
        }
        GLES20.glBindTexture(GLES20.
        GL_TEXTURE_2D, 0);
        return texture[0];
    }
    return 0;
}*/
