uniform mat4 uMVPMatrix;
attribute vec4 aPosition;
attribute vec2 aTextCoords;
varying vec2 TextCoord;
void main() {
    gl_Position = uMVPMatrix * vec4(aPosition.x, aPosition.y, 0.0, 1.0);
    TextCoord = aTextCoords;
}