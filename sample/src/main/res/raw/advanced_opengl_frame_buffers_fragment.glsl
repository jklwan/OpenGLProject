precision mediump float;
uniform sampler2D texture;
varying vec2 TextCoord;
void main() {
    vec3 col = texture2D(texture, TextCoord).rgb;
    gl_FragColor = vec4(col, 1.0);
}
