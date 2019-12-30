uniform mat4 uMVMatrix;
uniform mat4 uMVPMatrix;

attribute vec4 aPosition;
// 法向量
attribute vec3 aNormal;
attribute vec2 aTextCoords;

varying vec3 fragPos;
varying vec3 norm;
varying vec2 TextCoord;

void main() {
    fragPos = vec3(uMVMatrix * aPosition);
    norm = normalize(vec3(uMVMatrix * vec4(aNormal, 0.0)));
    TextCoord = aTextCoords;
    gl_Position = uMVPMatrix * aPosition;
}