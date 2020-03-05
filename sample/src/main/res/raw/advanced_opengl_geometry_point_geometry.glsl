#version 320 es
layout (points) in;
layout (points, max_vertices = 4) out;

in VS_OUT {
    vec3 color;
} gs_in[];

out vec3 fColor;
void build_point(vec4 position);

void main() {
    build_point(gl_in[0].gl_Position);
}

void build_point(vec4 position){
    fColor = gs_in[0].color;
    gl_Position = position + vec4(-0.5, 0.5, 0.0, 0.0);// 1:左上角
    gl_PointSize = 20.0;
    EmitVertex();
    gl_Position = position + vec4(0.5, 0.5, 0.0, 0.0);// 2:右上角
    EmitVertex();
    gl_Position = position + vec4(-0.5, -0.5, 0.0, 0.0);// 3:左下角
    gl_PointSize = 10.0;
    EmitVertex();
    gl_Position = position + vec4(0.5, -0.5, 0.0, 0.0);// 4:右下角
    fColor = vec3(1.0, 1.0, 1.0);
    EmitVertex();
    EndPrimitive();
}