#version 300 es
out vec4 FragColor;

in vec3 fColor;

void main(){
    FragColor = vec4(fColor, 1.0);
}