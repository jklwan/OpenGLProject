#version 300 es
layout (location = 0) in vec2 aPosition;
layout (location = 1) in vec3 aColor;
layout (location = 2) in vec2 aOffset;

out vec3 fColor;

uniform vec2 offsets[100];
uniform int type;

void main(){
    switch (type){
        case 0:
        vec2 offset = offsets[gl_InstanceID];
        gl_Position = vec4(aPosition + offset, 0.0, 1.0);
        break;
        case 1:
        gl_Position = vec4(aPosition + aOffset, 0.0, 1.0);
        break;
        case 2:
        float id = float(gl_InstanceID);
        vec2 pos = aPosition * (id / 100.0);
        gl_Position = vec4(pos + aOffset, 0.0, 1.0);
        break;
    }
    fColor = aColor;
}