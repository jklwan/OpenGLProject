precision mediump float;
uniform sampler2D texture;
varying vec2 TexCoord;
uniform int type;

uniform float kernel[9];
uniform vec2 offsets[9];

void main() {
    vec4 tex = texture2D(texture, TexCoord);
    if (type == 1){
        gl_FragColor = vec4(vec3(1.0 - tex.rgb), 1.0);
    } else if (type == 2){
        float average = (tex.r + tex.g + tex.b) / 3.0;
        gl_FragColor = vec4(average, average, average, 1.0);
    } else if (type == 3){
        float average = 0.2126 * tex.r + 0.7152 * tex.g + 0.0722 * tex.b;
        gl_FragColor = vec4(average, average, average, 1.0);
    } else if (type == 4 || type ==5 || type == 6){
        vec4 sum = vec4(0.0);
        for (int i = 0; i < 9; i++){
            vec4 texc = texture2D(texture, TexCoord + offsets[i]);
            sum += texc * kernel[i];
        }
        gl_FragColor = sum;
    } else {
        gl_FragColor = tex;
    }
}
