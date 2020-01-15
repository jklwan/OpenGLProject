precision mediump float;
varying vec2 TexCoord;
varying vec3 fragPos;
varying vec3 norm;
uniform vec3 aLightPos;
varying vec3 aObjectColor;
void main() {
    float ambientStrength = 0.3;
    vec3 lightColor = vec3(1.0, 1.0, 1.0);
    // 环境光照
    vec3 ambient = ambientStrength * lightColor;
    // 漫反射光照
    // 归一化光源线
    vec3 lightDir = normalize(aLightPos - fragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;

    // 镜面光照
    float specularStrength = 2.5;
    vec3 viewDir = normalize(-fragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 256.0);
    vec3 specular = spec * specularStrength * lightColor;
    // 结果
    vec3 result = (ambient + diffuse + specular) * aObjectColor;

    gl_FragColor = vec4(result, 1.0);
}