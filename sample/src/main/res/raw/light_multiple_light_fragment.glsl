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
// 定向光
struct DirLight {
    vec3 direction;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};
uniform DirLight dirLight;
// 点光源
struct PointLight {
    vec3 position;

    float constant;
    float linear;
    float quadratic;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};
#define NR_POINT_LIGHTS 4
uniform PointLight pointLights[NR_POINT_LIGHTS];

//聚光灯
struct SpotLight {
    vec3 position;
    vec3 direction;
    float cutOff;
    float outerCutOff;

    float constant;
    float linear;
    float quadratic;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};
uniform SpotLight spotLight;
vec3 calcDirLight(DirLight light, vec3 viewDir);
vec3 calcPointLight(PointLight light, vec3 viewDir);
vec3 calcSpotLight(SpotLight light, vec3 viewDir);

void main() {
    vec3 viewDir = normalize(-fragPos);
    // 计算定向光
    vec3 result = calcDirLight(dirLight, viewDir);
    // 计算点光源
    for (int i = 0; i < NR_POINT_LIGHTS; i++){
        result += calcPointLight(pointLights[i], viewDir);
    }
    // 计算聚光
    result += calcSpotLight(spotLight, viewDir);

    gl_FragColor = vec4(result, 1.0);
}
// 计算光照
vec3 calcDirLight(DirLight light, vec3 viewDir) {
    // 环境光照
    vec3 ambient = light.ambient* texture2D(material.diffuse, TexCoord).rgb;
    // 漫反射光照
    // 归一化光源线
    vec3 lightDir = normalize(-(aLightMatrix *light.direction));
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * light.diffuse * texture2D(material.diffuse, TexCoord).rgb;

    // 镜面光照
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    /*if (spec > 0.1){
        spec = 0.1;
    }*/
    vec3 specular = spec * light.specular * texture2D(material.specular, TexCoord).rgb;

    // 结果
    return (ambient + diffuse + specular);
}
// 计算点光源
vec3 calcPointLight(PointLight light, vec3 viewDir){
    // 环境光照
    vec3 ambient = light.ambient * texture2D(material.diffuse, TexCoord).rgb;
    // 漫反射光照
    // 归一化光源线
    vec3 lightDir = normalize(light.position - fragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = light.diffuse * diff * texture2D(material.diffuse, TexCoord).rgb;

    // 镜面光照
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = (spec * light.specular) * texture2D(material.specular, TexCoord).rgb;

    // 衰减
    float distance = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    ambient *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;
    // 结果
    return (ambient + diffuse + specular);
}
// 计算聚光
vec3 calcSpotLight(SpotLight light, vec3 viewDir){
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
    return (ambient + diffuse + specular);
}