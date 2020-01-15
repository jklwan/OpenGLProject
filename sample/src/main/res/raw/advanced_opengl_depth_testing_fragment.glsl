precision mediump float;
uniform sampler2D texture;
varying vec2 TexCoord;
uniform int type;
float near = 0.1;
float far = 100.0;
float LinearizeDepth(float depth);
void main() {
    if (type == 1){
        gl_FragColor = vec4(vec3(gl_FragCoord.z), 1.0);
    } else if (type == 2){
        float depth = LinearizeDepth(gl_FragCoord.z) / far;
        gl_FragColor = vec4(vec3(depth), 1.0);
    } else {
        gl_FragColor = texture2D(texture, TexCoord);
    }
}

float LinearizeDepth(float depth){
    float z = depth * 2.0 - 1.0;// back to NDC
    return (2.0 * near * far) / (far + near - z * (far - near));
}
