#version 320 es
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoords;

out VS_OUT {
    vec2 texCoords;
} vs_out;

uniform mat4 uMVPMatrix;

void main(){
    vs_out.texCoords = aTexCoords;
    gl_Position = uMVPMatrix * vec4(aPos, 1.0);
}