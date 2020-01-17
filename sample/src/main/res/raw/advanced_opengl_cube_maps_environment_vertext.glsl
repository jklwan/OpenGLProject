uniform mat4 uMVPMatrix;
uniform mat4 modelMatrix;
uniform mat4 normalMatrix;

attribute vec3 aPosition;
attribute vec3 aNormal;

varying vec3 Normal;
varying vec3 Position;
void main() {
    Normal = mat3(normalMatrix) * aNormal;
    Position = vec3(modelMatrix * vec4(aPosition, 1.0));

    gl_Position = uMVPMatrix * vec4(aPosition, 1.0);
}