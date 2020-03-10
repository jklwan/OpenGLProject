#version 300 es
layout (location = 0) in vec3 aPosition;
layout (location = 2) in vec2 aTexCoords;
layout (location = 3) in mat4 aInstanceMatrix;

out vec2 TexCoords;

uniform mat4 uVPMatrix;

void main(){
    TexCoords = aTexCoords;
    gl_Position = uVPMatrix * aInstanceMatrix * vec4(aPosition, 1.0f);
}