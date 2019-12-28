uniform mat4 uMVMatrix;
uniform mat4 uMVPMatrix;
uniform mat4 normalMatrix;

attribute vec4 aPosition;
// 法向量
attribute vec3 aNormal;
attribute vec3 objectColor;

varying vec3 fragPos;
varying vec3 norm;
varying vec3 aObjectColor;

void main() {
    // 转换坐标
    fragPos = vec3(uMVMatrix * aPosition);
    // 归一化法向量
    norm = normalize(mat3(normalMatrix) * aNormal);
    aObjectColor = objectColor;
    gl_Position = uMVPMatrix * aPosition;
}