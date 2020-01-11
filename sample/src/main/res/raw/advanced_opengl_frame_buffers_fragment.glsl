precision mediump float;
uniform sampler2D texture;
varying vec2 TextCoord;
uniform int type;

const float offset = 1.0 / 300.0;

void setOffsets(vec2 offsets[9]);
void setKernel(float kernel[9]);

void main() {
    vec3 tex = texture2D(texture, TextCoord).rgb;
    if (type == 0){
        gl_FragColor = vec4(tex, 1.0);
    } else if (type == 1){
        gl_FragColor = vec4(vec3(1.0 - tex), 1.0);
    } else if (type == 2){
        float average = (tex.r + tex.g + tex.b) / 3.0;
        gl_FragColor = vec4(average, average, average, 1.0);
    } else if (type == 3){
        float average = 0.2126 * tex.r + 0.7152 * tex.g + 0.0722 * tex.b;
        gl_FragColor = vec4(average, average, average, 1.0);
    } else if (type == 4){
        vec2 offsets[9];
        setOffsets(offsets);
        /*= vec2[](
        vec2(-offset, offset), // 左上
        vec2(0.0, offset), // 正上
        vec2(offset, offset), // 右上
        vec2(-offset, 0.0), // 左
        vec2(0.0, 0.0), // 中
        vec2(offset, 0.0), // 右
        vec2(-offset, -offset), // 左下
        vec2(0.0, -offset), // 正下
        vec2(offset, -offset)// 右下
        );*/

        float kernel[9];
        setKernel(kernel);
        /* = float[](
        -1.0, -1.0, -1.0,
        -1.0, 9.0, -1.0,
        -1.0, -1.0, -1.0
        );*/

        vec3 sampleTex[9];
        for (int i = 0; i < 9; i++){
            sampleTex[i] = vec3(texture2D(texture, TextCoord + offsets[i]));
        }
        vec3 col = vec3(0.0);
        for (int i = 0; i < 9; i++){
            col += sampleTex[i] * kernel[i];
        }
        gl_FragColor = vec4(col, 1.0);
    } else if (type == 5){
        vec2 offsets[9];
        setOffsets(offsets);
        /*const vec2 offsets[9] = vec2[](
        vec2(-offset, offset), // 左上
        vec2(0.0, offset), // 正上
        vec2(offset, offset), // 右上
        vec2(-offset, 0.0), // 左
        vec2(0.0, 0.0), // 中
        vec2(offset, 0.0), // 右
        vec2(-offset, -offset), // 左下
        vec2(0.0, -offset), // 正下
        vec2(offset, -offset)// 右下
        );*/

        float kernel[9];
        setKernel(kernel);
        /*const float kernel[9] = float[](
        1.0 / 16.0, 2.0 / 16.0, 1.0 / 16.0,
        2.0 / 16.0, 4.0 / 16.0, 2.0 / 16.0,
        1.0 / 16.0, 2.0 / 16.0, 1.0 / 16.0
        );*/

        vec3 sampleTex[9];
        for (int i = 0; i < 9; i++){
            sampleTex[i] = vec3(texture2D(texture, TextCoord + offsets[i]));
        }
        vec3 col = vec3(0.0);
        for (int i = 0; i < 9; i++){
            col += sampleTex[i] * kernel[i];
        }
        gl_FragColor = vec4(col, 1.0);
    } else if (type == 6){
        vec2 offsets[9];
        setOffsets(offsets);
        /*const vec2 offsets[9] = vec2[](
        vec2(-offset, offset), // 左上
        vec2(0.0, offset), // 正上
        vec2(offset, offset), // 右上
        vec2(-offset, 0.0), // 左
        vec2(0.0, 0.0), // 中
        vec2(offset, 0.0), // 右
        vec2(-offset, -offset), // 左下
        vec2(0.0, -offset), // 正下
        vec2(offset, -offset)// 右下
        );*/

        float kernel[9];
        setKernel(kernel);
        /*const float kernel[9] = float[](
        1.0, 1.0, 1.0,
        1.0, -8.0, 1.0,
        1.0, 1.0, 1.0
        );*/

        vec3 sampleTex[9];
        for (int i = 0; i < 9; i++){
            sampleTex[i] = vec3(texture2D(texture, TextCoord + offsets[i]));
        }
        vec3 col = vec3(0.0);
        for (int i = 0; i < 9; i++){
            col += sampleTex[i] * kernel[i];
        }
        gl_FragColor = vec4(col, 1.0);
    }
}

void setOffsets(vec2 offsets[9]){
    offsets[0] = vec2(-offset, offset); // 左上
    offsets[1] = vec2(0.0, offset); // 正上
    offsets[2] = vec2(offset, offset);// 右上
    offsets[3] = vec2(-offset, 0.0); // 左
    offsets[4] = vec2(0.0, 0.0); // 中
    offsets[5] = vec2(offset, 0.0); // 右
    offsets[6] = vec2(-offset, -offset); // 左下
    offsets[7] = vec2(0.0, -offset); // 正下
    offsets[8] = vec2(offset, -offset);// 右下
}

void setKernel(float kernel[9]){
    if(type == 4){
        kernel[0] = -1.0;
        kernel[1] = -1.0;
        kernel[2] = -1.0;
        kernel[3] = -1.0;
        kernel[4] = 9.0;
        kernel[5] = -1.0;
        kernel[6] = -1.0;
        kernel[7] = -1.0;
        kernel[8] = -1.0;
    } else if(type == 5){
        kernel[0] = 1.0 / 16.0;
        kernel[1] = 2.0 / 16.0;
        kernel[2] = 1.0 / 16.0;
        kernel[3] = 2.0 / 16.0;
        kernel[4] = 4.0 / 16.0;
        kernel[5] = 2.0 / 16.0;
        kernel[6] = 1.0 / 16.0;
        kernel[7] = 2.0 / 16.0;
        kernel[8] = 1.0 / 16.0;
    } else if(type == 6){
        kernel[0] = 1.0;
        kernel[1] = 1.0;
        kernel[2] = 1.0;
        kernel[3] = 1.0;
        kernel[4] = -8.0;
        kernel[5] = 1.0;
        kernel[6] = 1.0;
        kernel[7] = 1.0;
        kernel[8] = 1.0;
    }
}