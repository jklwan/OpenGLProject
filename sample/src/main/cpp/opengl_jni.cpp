#include <jni.h>
#include "window/PointLine.cpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    return JNI_VERSION_1_6;
}

BaseRenderer *renderer = nullptr;

extern "C"
JNIEXPORT void JNICALL
Java_com_chends_opengl_utils_JniRendererUtil_surfaceCreated(JNIEnv *env, jobject thiz, jint type) {
    switch (type) {
        case 1:
            PointLine ra = PointLine();
            renderer = &ra;
            break;
    }
}
extern "C" JNIEXPORT void JNICALL
Java_com_chends_opengl_utils_JniRendererUtil_surfaceChanged(JNIEnv *env, jobject thiz, jint width,jint height) {
    if (renderer != nullptr) {
        renderer->surfaceChanged(env, width, height);
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_chends_opengl_utils_JniRendererUtil_drawFrame(JNIEnv *env, jobject thiz) {
    if (renderer != nullptr) {
        renderer->drawFrame(env);
    }
}