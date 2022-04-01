
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

class OpenGLUtil {

public:

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