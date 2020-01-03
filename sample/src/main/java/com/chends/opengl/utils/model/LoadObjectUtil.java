package com.chends.opengl.utils.model;

import android.content.res.Resources;
import android.text.TextUtils;

import com.chends.opengl.model.model.MtlBean;
import com.chends.opengl.model.model.ObjectBean;
import com.chends.opengl.utils.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 加载模型
 * @author chends create on 2020/1/2.
 */
public class LoadObjectUtil {


    /**
     * 对应的纹理文件
     */
    private static final String MTLLIB = "mtllib";
    /**
     * 组名称
     */
    private static final String G = "g";
    /**
     * o 对象名称(Object name)
     */
    private static final String O = "o";
    /**
     * 顶点
     */
    private static final String V = "v";
    /**
     * 纹理坐标
     */
    private static final String VT = "vt";
    /**
     * 法向量
     */
    private static final String VN = "vn";
    /**
     * 使用的纹理
     */
    private static final String USEMTL = "usemtl";
    /**
     * v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3(索引起始于1)
     */
    private static final String F = "f";

    /**
     * 读取模型文件信息
     * @param assets assets
     * @return list
     */
    public static List<ObjectBean> loadObject(String assets, Resources res) {
        InputStream stream = null;
        try {
            stream = res.getAssets().open(assets);
        } catch (IOException e) {
            LogUtil.e(e);
        }
        return loadObject(stream, res);
    }

    /**
     * 读取模型文件信息
     * @param stream stream
     * @return list
     */
    public static List<ObjectBean> loadObject(InputStream stream, Resources res) {
        List<ObjectBean> result = new ArrayList<>();
        // 顶点数据
        ArrayList<Float> vertices = new ArrayList<>();
        // 纹理数据
        ArrayList<Float> texCoords = new ArrayList<>();
        // 法向量数据
        ArrayList<Float> normals = new ArrayList<>();
        // 全部材质列表
        Map<String, MtlBean> mtlMap = null;

        if (stream != null) {
            BufferedReader buffer = null;
            try {
                buffer = new BufferedReader(new InputStreamReader(stream));
                String line, type;
                StringTokenizer parts;
                int numTokens;
                ObjectBean currObj = new ObjectBean();
                // 当前纹理名称
                String currTexName = null;
                // 是否有面数据的标识
                boolean currObjHasFaces = false;
                while ((line = buffer.readLine()) != null) {
                    if (TextUtils.isEmpty(line.trim()) || line.trim().startsWith("#")) {
                        continue;
                    }
                    // 以空格分隔
                    parts = new StringTokenizer(line, " ");
                    numTokens = parts.countTokens();
                    if (numTokens == 0) {
                        continue;
                    }
                    // 开头的字符
                    type = parts.nextToken();
                    switch (type) {
                        case MTLLIB:
                            // 纹理
                            // 读取纹理
                            if (!parts.hasMoreTokens()) {
                                continue;
                            }
                            // 获取纹理信息
                            String mtlPath = parts.nextToken();
                            // 加载材质信息
                            if (!TextUtils.isEmpty(mtlPath)) {
                                mtlMap = LoadMtlUtil.loadMtl(mtlPath, res);
                            }
                            break;
                        case O:
                            // object
                            // 对象名称
                            String objName = parts.hasMoreTokens() ? parts.nextToken() : "def";
                            // 面数据
                            if (currObjHasFaces) {
                                // 添加到数组中
                                result.add(currObj);
                                // 创建新的索引对象
                                currObj = new ObjectBean();
                                currObjHasFaces = false;
                            }
                            currObj.name = objName;
                            // 对应材质
                            if (!TextUtils.isEmpty(currTexName) && mtlMap != null) {
                                currObj.mtl = mtlMap.get(currTexName);
                            }
                            break;
                        case V:
                            // "v" 顶点属性 添加到顶点数组
                            vertices.add(Float.parseFloat(parts.nextToken()));
                            vertices.add(Float.parseFloat(parts.nextToken()));
                            vertices.add(Float.parseFloat(parts.nextToken()));
                            break;
                        case VT:
                            // 纹理
                            // 这里纹理的Y值，需要(Y = 1-Y0)
                            texCoords.add(Float.parseFloat(parts.nextToken()));
                            texCoords.add(1f - Float.parseFloat(parts.nextToken()));
                            break;
                        case VN:
                            // 法向量
                            normals.add(Float.parseFloat(parts.nextToken()));
                            normals.add(Float.parseFloat(parts.nextToken()));
                            normals.add(Float.parseFloat(parts.nextToken()));
                            break;
                        case USEMTL:
                            // mtl名
                            currTexName = parts.nextToken();
                            if (currObjHasFaces) {
                                // 添加到数组中
                                result.add(currObj);
                                // 创建一个index对象
                                currObj = new ObjectBean();
                                currObjHasFaces = false;
                            }
                            // 材质名称
                            if (!TextUtils.isEmpty(currTexName) && mtlMap != null) {
                                currObj.mtl = mtlMap.get(currTexName);
                            }
                            break;
                        case F:
                            // "f"面属性  索引数组
                            // 当前obj对象有面数据
                            currObjHasFaces = true;
                            // 是否为矩形(android 均为三角形，这里暂时先忽略多边形的情况)
                            boolean isQuad = numTokens == 5;
                            int[] quadvids = new int[4];
                            int[] quadtids = new int[4];
                            int[] quadnids = new int[4];

                            // 如果含有"//" 替换
                            boolean emptyVt = line.contains("//");
                            if (emptyVt) {
                                line = line.replace("//", "/");
                            }
                            // "f 103/1/1 104/2/1 113/3/1"以" "分割
                            parts = new StringTokenizer(line);
                            // “f”
                            parts.nextToken();
                            // "103/1/1 104/2/1 113/3/1"再以"/"分割
                            StringTokenizer subParts = new StringTokenizer(parts.nextToken(), "/");
                            int partLength = subParts.countTokens();

                            // 纹理数据
                            boolean hasUV = partLength >= 2 && !emptyVt;
                            // 法向量数据
                            boolean hasN = partLength == 3 || (partLength == 2 && emptyVt);
                            // 索引index
                            int idx;
                            for (int i = 1; i < numTokens; i++) {
                                if (i > 1) {
                                    subParts = new StringTokenizer(parts.nextToken(), "/");
                                }
                                // 顶点索引
                                idx = Integer.parseInt(subParts.nextToken());
                                if (idx < 0) {
                                    idx = (vertices.size() / 3) + idx;
                                } else {
                                    idx -= 1;
                                }
                                if (!isQuad) {
                                    currObj.vertexIndices.add(idx);
                                } else {
                                    quadvids[i - 1] = idx;
                                }
                                // 纹理索引
                                if (hasUV) {
                                    idx = Integer.parseInt(subParts.nextToken());
                                    if (idx < 0) {
                                        idx = (texCoords.size() / 2) + idx;
                                    } else {
                                        idx -= 1;
                                    }
                                    if (!isQuad) {
                                        currObj.texCoordIndices.add(idx);
                                    } else {
                                        quadtids[i - 1] = idx;
                                    }
                                }
                                // 法向量数据
                                if (hasN) {
                                    idx = Integer.parseInt(subParts.nextToken());
                                    if (idx < 0) {
                                        idx = (normals.size() / 3) + idx;
                                    } else {
                                        idx -= 1;
                                    }
                                    if (!isQuad) {
                                        currObj.normalIndices.add(idx);
                                    } else {
                                        quadnids[i - 1] = idx;
                                    }
                                }
                            }
                            if (isQuad) {
                                int[] indices = new int[]{0, 1, 2, 0, 2, 3};
                                for (int i = 0; i < 6; ++i) {
                                    int index = indices[i];
                                    currObj.vertexIndices.add(quadvids[index]);
                                    currObj.texCoordIndices.add(quadtids[index]);
                                    currObj.normalIndices.add(quadnids[index]);
                                }
                            }
                            break;
                    }
                }
                if (currObjHasFaces) {
                    // 添加到数组中
                    result.add(currObj);
                }
                // 循环索引对象列表
                int size = result.size();
                for (int j = 0; j < size; ++j) {
                    ObjectBean objData = result.get(j);

                    int i;
                    // 顶点数据 初始化
                    float[] aVertices = new float[objData.vertexIndices.size() * 3];
                    // 顶点纹理数据 初始化
                    float[] aTexCoords = new float[objData.texCoordIndices.size() * 2];
                    // 顶点法向量数据 初始化
                    float[] aNormals = new float[objData.normalIndices.size() * 3];
                    // 按照索引，重新组织顶点数据
                    for (i = 0; i < objData.vertexIndices.size(); ++i) {
                        // 顶点索引，三个一组做为一个三角形
                        int faceIndex = objData.vertexIndices.get(i) * 3;
                        int vertexIndex = i * 3;
                        try {
                            // 按照索引，重新组织顶点数据
                            aVertices[vertexIndex] = vertices.get(faceIndex);
                            aVertices[vertexIndex + 1] = vertices.get(faceIndex + 1);
                            aVertices[vertexIndex + 2] = vertices.get(faceIndex + 2);
                        } catch (Exception e) {
                            LogUtil.e(e);
                        }
                    }
                    // 按照索引组织 纹理数据
                    if (!texCoords.isEmpty()) {
                        for (i = 0; i < objData.texCoordIndices.size(); ++i) {
                            int texCoordIndex = objData.texCoordIndices.get(i) * 2;
                            int ti = i * 2;
                            aTexCoords[ti] = texCoords.get(texCoordIndex);
                            aTexCoords[ti + 1] = texCoords.get(texCoordIndex + 1);
                        }
                    }
                    // 按照索引组织 法向量数据
                    for (i = 0; i < objData.normalIndices.size(); ++i) {
                        int normalIndex = objData.normalIndices.get(i) * 3;
                        int ni = i * 3;
                        if (normals.isEmpty()) {
                            throw new Exception("There are no normals specified for this model. Please re-export with normals.");
                        }
                        aNormals[ni] = normals.get(normalIndex);
                        aNormals[ni + 1] = normals.get(normalIndex + 1);
                        aNormals[ni + 2] = normals.get(normalIndex + 2);
                    }
                    // 数据设置到oid.targetObj中
                    objData.aVertices = aVertices;
                    objData.aTexCoords = aTexCoords;
                    objData.aNormals = aNormals;
                    objData.vertexIndices.clear();
                    objData.texCoordIndices.clear();
                    objData.normalIndices.clear();
                }

            } catch (Exception e) {
                LogUtil.e(e);
            } finally {
                try {
                    if (buffer != null) {
                        buffer.close();
                    }
                    stream.close();
                } catch (IOException e) {
                    LogUtil.e(e);
                }
            }
        }
        return result;
    }
}
