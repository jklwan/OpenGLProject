#include <cstdio>
#include <jni.h>
#include "window/PointLine.cpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    return JNI_VERSION_1_6;
}

PointLine *renderer = nullptr;
jint desWidth = 0, desHeight = 0;
extern "C"
JNIEXPORT void JNICALL
Java_com_chends_opengl_utils_JniRendererUtil_surfaceCreated(JNIEnv *env, jobject thiz, jint type) {
    switch (type) {
        case 1:
            PointLine ra = PointLine();
            renderer = &ra;
            break;
    }
    if (renderer != nullptr) {
        LOGE("PointLine surfaceCreated renderer: %p",renderer);
        renderer->surfaceCreated(env);
    }
}
extern "C" JNIEXPORT void JNICALL
Java_com_chends_opengl_utils_JniRendererUtil_surfaceChanged(JNIEnv *env, jobject thiz, jint width,
                                                            jint height) {
    desWidth = width;
    desHeight = height;
    if (renderer != nullptr) {
        LOGE("PointLine surfaceChanged renderer: %p",renderer);
        renderer->surfaceChanged(env, width, height);
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_chends_opengl_utils_JniRendererUtil_drawFrame(JNIEnv *env, jobject thiz) {
    if (renderer != nullptr) {
        LOGE("PointLine drawFrame renderer: %p",renderer);
        renderer->drawFrame(env, desWidth, desHeight);
    }
}