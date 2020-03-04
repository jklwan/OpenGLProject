#version 320 es
layout (location = 0) in vec2 aPos;
layout (location = 1) in vec3 aColor;
layout (location = 2) in int type;

out VS_OUT {
    vec3 color;
    int type;
} vs_out;

void main(){
    vs_out.color = aColor;
    gl_Position = vec4(aPos.x, aPos.y, 0.0, 1.0);
    gl_PointSize = 20.0;
    vs_out.type = type;
    /*switch (type){
        case 0:
        gl_Position = vec4(aPos.x, aPos.y, 0.0, 0.8);
        break;
        default:
        gl_Position = vec4(aPos.x, aPos.y, 0.0, 1.0);
        break;
    }*/
}