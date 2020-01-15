uniform mat4 uMVPMatrix;
attribute vec4 aPosition;
attribute vec2 aTexCoords;
varying vec2 TexCoord;
void main() {
    gl_Position = uMVPMatrix * aPosition;
    TexCoord = aTexCoords;
}