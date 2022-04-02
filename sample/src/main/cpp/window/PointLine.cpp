#include <GLES3/gl3.h>
#include <cstring>
#include <jni.h>
#include "utils/OpenGLUtil.h"

class PointLine {
    const GLfloat *TriangleCoords = new GLfloat[]{
            -0.9f, 0.9f, 0.0f,
            -0.9f, 0.8f, 0.0f,
            -0.8f, -0.1f, 0.0f,
            -0.6f, -0.5f, 0.0f,
            -0.5f, -0.8f, 0.0f,
            -0.4f, 0.4f, 0.0f,
            -0.2f, 0.1f, 0.0f,
            -0.0f, 0.5f, 0.0f,
            0.1f, 0.0f, 0.0f,
            0.3f, -0.5f, 0.0f,
            0.4f, -0.2f, 0.0f,
            0.6f, -0.5f, 0.0f,
            0.9f, -0.6f, 0.0f,
            0.9f, -0.9f, 0.0f,
    };

public:
    void surfaceCreated(JNIEnv *env)  {
        OpenGLUtil::surfaceCreated();
    }

    void surfaceChanged(JNIEnv *env, jint width, jint height)  {
        LOGE("PointLine surfaceChanged");
    }

    void drawFrame(JNIEnv *env, jint width, jint height)  {
        OpenGLUtil::drawFrame(width, height);
        GLuint shaderProgram = OpenGLUtil::createProgram(env);
        glUseProgram(shaderProgram);
        GLint positionHandle = glGetAttribLocation(shaderProgram, "aPosition");
        glEnableVertexAttribArray(positionHandle);
        /*FloatBuffer vertexBuffer = OpenGLUtil.createFloatBuffer(TriangleCoords);
        // C function void glVertexAttribPointer ( GLuint indx, GLint size, GLenum type, GLboolean normalized, GLsizei stride, const GLvoid *ptr )
        */
        glVertexAttribPointer(positionHandle, 3, GL_FLOAT,
                              GL_FALSE, 3 * 4, TriangleCoords);
        GLint colorHandle = glGetUniformLocation(shaderProgram, "vColor");
        // 设置颜色
        glUniform4fv(colorHandle, 1, defaultColor);
        // 画点
        glDrawArrays(GL_POINTS, 0, 13);
        // 设置线宽
        glLineWidth(18);
        // 画线，不连续的线，例如：有1,2,3,4四个点，1和2是一条线，3,4是一条线
        glDrawArrays(GL_LINES, 2, 4);
        // 画线，封闭的线，例如：有1,2,3,4四个点，1,2,3,4，1会连接2，2连接3，3连接4，4连接1
        glDrawArrays(GL_LINE_LOOP, 6, 4);
        // 画线，不封闭的线
        glDrawArrays(GL_LINE_STRIP, 10, 4);
        glDisableVertexAttribArray(positionHandle);
    }
};

