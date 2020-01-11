attribute vec2 aPosition;
attribute vec2 aTextCoords;
varying vec2 TextCoord;
void main() {
    gl_Position = vec4(aPosition.x, aPosition.y, 0.0, 1.0);
    TextCoord = aTextCoords;
}