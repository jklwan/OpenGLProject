#version 300 es
layout (location = 0) in vec3 aPosition;
layout (location = 2) in vec2 aTexCoords;

out vec2 TexCoords;

uniform mat4 uMVPMatrix;

void main(){
    TexCoords = aTexCoords;
    gl_Position = uMVPMatrix * vec4(aPosition, 1.0f);
}