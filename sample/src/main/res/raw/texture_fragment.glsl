precision mediump float;
uniform sampler2D texture;
varying vec2 TextCoord;
void main() {
    gl_FragColor = texture2D(texture, TextCoord);
}
