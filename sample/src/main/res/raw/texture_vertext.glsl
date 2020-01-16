uniform mat4 uMVPMatrix;
attribute vec3 aPosition;
attribute vec2 aTexCoords;
varying vec2 TexCoord;
void main() {
    gl_Position = uMVPMatrix * vec4(aPosition, 1.0);
    TexCoord = aTexCoords;
}