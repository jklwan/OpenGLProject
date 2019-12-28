uniform mat4 uMVMatrix;
uniform mat4 uMVPMatrix;

attribute vec4 aPosition;
// 法向量
attribute vec3 aNormal;
attribute vec3 objectColor;

varying vec3 fragPos;
varying vec3 norm;
varying vec3 aObjectColor;

void main() {
    fragPos = vec3(uMVMatrix * aPosition);
    norm = normalize(vec3(uMVMatrix * vec4(aNormal, 0.0)));
    aObjectColor = objectColor;
    gl_Position = uMVPMatrix * aPosition;
}