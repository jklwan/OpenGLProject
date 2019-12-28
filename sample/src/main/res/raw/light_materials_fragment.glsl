precision mediump float;

varying vec3 fragPos;
varying vec3 norm;
varying vec3 aObjectColor;

// 定义材质结构体
struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};
uniform Material material;

// 定义光源结构体
struct Light {
    vec3 position;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

uniform Light light;
void main() {
    // 环境光照
    vec3 ambient = material.ambient * light.ambient;

    // 漫反射光照
    // 归一化光源线
    vec3 lightDir = normalize(light.position - fragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * material.diffuse * light.diffuse;

    // 镜面光照
    vec3 viewDir = normalize(-fragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = material.specular * spec * light.specular;
    // 结果
    vec3 result = (ambient + diffuse + specular) * aObjectColor;
    gl_FragColor = vec4(result, 1.0);
}