precision mediump float;
uniform sampler2D texture;
varying vec2 TexCoord;
uniform int type;
void main() {
    vec4 texColor = texture2D(texture, TexCoord);
    if (type == 1){
        if (texColor.a < 0.1){
            discard;
        }
    }
    // 其他类型 什么都不做
    gl_FragColor = texColor;
}

