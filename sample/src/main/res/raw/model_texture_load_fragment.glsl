precision mediump float;

varying vec2 TextCoord;
varying vec3 fragPos;
varying vec3 norm;

// 定义材质结构体
struct Material {
    sampler2D ambient;
    sampler2D diffuse;
    int hasSpecular;
    sampler2D specular;

    float shininess;
    float alpha;
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
    vec3 ambient = light.ambient * texture2D(material.ambient, TextCoord).rgb;

    // 漫反射光照
    // 归一化光源线
    vec3 lightDir = normalize(light.position - fragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * light.diffuse * texture2D(material.diffuse, TextCoord).rgb;

    // 镜面光照
    vec3 viewDir = normalize(-fragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = spec * light.specular;
    if (material.hasSpecular == 1){
        specular *=  texture2D(material.specular, TextCoord).rgb;
    } else {
        specular *= vec3(0.0, 0.0, 0.0);
    }
    // 结果
    vec3 result = (ambient + diffuse + specular);
    gl_FragColor = vec4(result, material.alpha);
}