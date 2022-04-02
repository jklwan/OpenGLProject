#include <GLES3/gl3.h>
#include <cstring>
#include <jni.h>
#include "utils/OpenGLUtil.h"

class BaseRenderer {

public:
    virtual void surfaceCreated(JNIEnv *env) {

    }

    virtual void surfaceChanged(JNIEnv *env, jint width, jint height) {

    }

    virtual void drawFrame(JNIEnv *env, jint width, jint height) {
    }

};