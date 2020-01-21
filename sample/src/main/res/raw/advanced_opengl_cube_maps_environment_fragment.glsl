precision mediump float;

varying vec3 Normal;
varying vec3 Position;
uniform vec3 cameraPos;
uniform samplerCube skybox;
uniform int type;

void main() {
    if (type == 1){
        float ratio = 1.00 / 1.52;
        vec3 I = normalize(Position - cameraPos);
        vec3 R = refract(I, normalize(Normal), ratio);
        gl_FragColor = vec4(textureCube(skybox, R).rgb, 1.0);
    } else {
        vec3 I = normalize(Position - cameraPos);
        vec3 R = reflect(I, normalize(Normal));
        gl_FragColor = vec4(textureCube(skybox, R).rgb, 1.0);
    }
}
