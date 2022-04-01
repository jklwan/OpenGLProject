#include <GLES3/gl3.h>
#include <cstring>
#include <jni.h>
#include "utils/OpenGLUtil.h"

class BaseRenderer {

public:
    const char *vertexShaderCode =
            "attribute vec4 aPosition; \n"
            "void main() { \n"
            "  gl_Position = aPosition; \n"
            "  gl_PointSize = 19.0; \n"
            "}";
    const char *fragmentShaderCode =
            "precision mediump float;\n"
            "uniform vec4 vColor;\n"
            "void main() {\n"
            "  gl_FragColor = vColor;\n"
            "}";
    unsigned int bg = 0xFF000000;
    jint disWidth = 0;
    jint disHeight = 0;
    float color[4] = {0.70703125f, 0.10546875f, 0.84375f, 1.0f};

    virtual void surfaceCreated(JNIEnv *env, jobject object) {
        glClearColor(red(bg) / 255.0f, green(bg) / 255.0f,
                     blue(bg) / 255.0f, alpha(bg) / 255.0f);
    }

    virtual void surfaceChanged(JNIEnv *env, int width, int height) {
        disWidth = width;
        disHeight = height;
    }

    virtual void drawFrame(JNIEnv *env) {
        // 设置显示范围
        glViewport(0, 0, disWidth, disHeight);
        //GLES20.glEnable(GLES20.GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        // 清屏
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    unsigned int alpha(unsigned int color) {
        return color >> 24;
    }

    unsigned int red(unsigned int color) {
        return (color >> 16) & 0xFF;
    }

    unsigned int green(unsigned int color) {
        return (color >> 8) & 0xFF;
    }

    unsigned int blue(unsigned int color) {
        return color & 0xFF;
    }

};