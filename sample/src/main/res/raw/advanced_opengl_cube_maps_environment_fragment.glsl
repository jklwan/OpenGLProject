precision mediump float;

varying vec3 Normal;
varying vec3 Position;
uniform vec3 cameraPos;
uniform samplerCube skybox;

void main() {
    vec3 I = normalize(Position - cameraPos);
    vec3 R = reflect(I, normalize(Normal));
    gl_FragColor = vec4(textureCube(skybox, R).rgb, 1.0);
}
