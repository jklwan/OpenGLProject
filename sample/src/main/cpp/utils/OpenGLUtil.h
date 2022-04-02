
#ifndef NATIVEGLESVIEW_GLUTIL_H
#define NATIVEGLESVIEW_GLUTIL_H

#include <GLES2/gl2.h>
#include <GLES3/gl3.h>
#include <GLES3/gl32.h>
#include <android/log.h>
#include <jni.h>

#define  LOG_TAG    "libopengljni"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

static const char *defaultVertexShaderCode =
        "attribute vec4 aPosition; \n"
        "void main() { \n"
        "  gl_Position = aPosition; \n"
        "  gl_PointSize = 19.0; \n"
        "}";
static const char *defaultFragmentShaderCode =
        "precision mediump float;\n"
        "uniform vec4 vColor;\n"
        "void main() {\n"
        "  gl_FragColor = vColor;\n"
        "}";
static float defaultBg[4] = {0.0f, 0.0f, 0.0f, 1.0f};
static float defaultColor[4] = {0.70703125f, 0.10546875f, 0.84375f, 1.0f};

class OpenGLUtil {

public:

    static void surfaceCreated();

    static void drawFrame(jint width, jint height);

    static GLuint createProgram(JNIEnv *env);

    static GLuint createProgram(JNIEnv *env, const char *vertexSource, const char *fragmentSource);

    static GLuint createProgram(JNIEnv *env, const char *vertexSource, const char *fragmentSource,
                                jobjectArray attributes);

    static GLuint createProgram(JNIEnv *env, const char *vertexSource, const char *fragmentSource,
                                const char *geometrySource);

    static GLuint createProgram(JNIEnv *env, const char *vertexSource, const char *fragmentSource,
                                const char *geometrySource, jobjectArray attributes);

    static GLuint loadShader(jint shaderType, const char *source);

    static void checkGlError(const char *op);

};

#endif //NATIVEGLESVIEW_GLUTIL_H