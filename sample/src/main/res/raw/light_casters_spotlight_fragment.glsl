precision mediump float;
varying vec2 TexCoord;
varying vec3 fragPos;
varying vec3 norm;
varying mat3 aLightMatrix;

// 定义材质结构体
struct Material {
    sampler2D diffuse;
    sampler2D specular;
    float shininess;
};
uniform Material material;
// 定义光源结构体
struct Light {
    vec3 position;
    vec3 direction;
    float cutOff;
    float outerCutOff;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    float constant;
    float linear;
    float quadratic;
};

uniform Light light;
void main() {
    vec3 lightDir = normalize(light.position - fragPos);

    // 检查当前点与光源的连线是否在聚光灯内
    float theta = dot(lightDir, normalize(-(aLightMatrix * light.direction)));
    float epsilon = light.cutOff - light.outerCutOff;
    float intensity = clamp((theta - light.outerCutOff) / epsilon, 0.0, 1.0);

    // 环境光照
    vec3 ambient = light.ambient * texture2D(material.diffuse, TexCoord).rgb;
    // 漫反射光照
    // 归一化光源线
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = light.diffuse * diff * texture2D(material.diffuse, TexCoord).rgb;

    // 镜面光照
    vec3 viewDir = normalize(-fragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = (spec * light.specular) * texture2D(material.specular, TexCoord).rgb;

    diffuse *= intensity;
    specular *= intensity;

    // 衰减
    float distance = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    ambient *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;
    // 结果
    vec3 result = ambient + diffuse + specular;
    gl_FragColor = vec4(result, 1.0);
}