uniform mat4 uMVPMatrix;
attribute vec4 aPosition;
attribute vec2 aTextCoords;
varying vec2 TextCoord;
void main() {
    gl_Position = uMVPMatrix * aPosition;
    TextCoord = aTextCoords;
}