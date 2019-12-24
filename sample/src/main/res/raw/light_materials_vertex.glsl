uniform mat4 uMVMatrix;
uniform mat4 uMVPMatrix;

attribute vec4 aPosition;
// 法向量
attribute vec3 aNormal;
attribute vec3 objectColor;
// 结果
varying vec4 aColor;
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
    vec3 lightColor = vec3(1.0, 1.0, 1.0);
    // 环境光照
    vec3 ambient = material.ambient * light.ambient;
    // 转换坐标
    vec3 fragPos = vec3(uMVMatrix * aPosition);

    // 漫反射光照
    // 归一化法向量
    vec3 norm = normalize(vec3(uMVMatrix * vec4(aNormal, 0.0)));
    // 归一化光源线
    vec3 lightDir = normalize(light.position - fragPos);
    float diff = max(dot(norm, lightDir), 0.1);
    vec3 diffuse = diff * material.diffuse * light.diffuse;

    // 镜面光照
    vec3 viewDir = normalize(-fragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.1), material.shininess);
    vec3 specular = material.specular * spec * light.specular;
    // 结果，使用ambient,diffuse,specular相加则为结合的效果
    vec3 result = (ambient + diffuse + specular) * objectColor;
    aColor = vec4(result, 1.0);

    gl_Position = uMVPMatrix * aPosition;
}