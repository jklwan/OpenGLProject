uniform mat4 uMVMatrix;
uniform mat4 uMVPMatrix;
uniform mat4 normalMatrix;
uniform mat4 lightMatrix;

attribute vec4 aPosition;
// 法向量
attribute vec3 aNormal;
attribute vec2 aTexCoords;

varying vec3 fragPos;
varying vec3 norm;
varying vec2 TexCoord;
varying mat3 aLightMatrix;

void main() {
    fragPos = vec3(uMVMatrix * aPosition);
    norm = normalize(mat3(normalMatrix) * aNormal);
    TexCoord = aTexCoords;
    aLightMatrix = mat3(lightMatrix);
    gl_Position = uMVPMatrix * aPosition;
}