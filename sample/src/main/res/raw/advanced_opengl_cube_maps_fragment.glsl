precision mediump float;
varying vec3 TexCoord; // 代表3D纹理坐标的方向向量
uniform samplerCube skybox; // 立方体贴图的纹理采样器

void main() {
    gl_FragColor = textureCube(skybox, TexCoord);
}
