uniform mat4 uMVPMatrix;
attribute vec3 aPosition;
varying vec3 TexCoord;
void main() {
    TexCoord = aPosition;

    vec4 pos = uMVPMatrix * vec4(aPosition, 1.0);
    gl_Position = pos.xyww;
}