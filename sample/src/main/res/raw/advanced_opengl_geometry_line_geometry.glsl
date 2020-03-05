#version 320 es
layout (points) in;
layout (line_strip, max_vertices = 2) out;

in VS_OUT {
    vec3 color;
} gs_in[];

out vec3 fColor;
void build_line(vec4 position);

void main() {
    build_line(gl_in[0].gl_Position);
}

void build_line(vec4 position){
    fColor = gs_in[0].color;
    gl_Position = gl_in[0].gl_Position + vec4(-0.1, 0.0, 0.0, 0.0); // 左顶点
    EmitVertex();

    gl_Position = gl_in[0].gl_Position + vec4( 0.1, 0.0, 0.0, 0.0); // 右顶点
    EmitVertex();
    EndPrimitive();
}