package com.chends.opengl.model.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 模型
 * @author chends create on 2020/1/2.
 */
public class ObjectBean {
    public String name;
    /**
     * 顶点数据
     */
    public float[] aVertices;
    /**
     * 纹理
     */
    public float[] aTexCoords;
    /**
     * 法向量
     */
    public float[] aNormals;

    public MtlBean mtl;
    /**
     * 临时存放顶点数据
     */
    public List<Integer> vertexIndices = new ArrayList<>();
    /**
     * 存放纹理数据
     */
    public List<Integer> texCoordIndices = new ArrayList<>();
    /**
     * 存放法向量数据
     */
    public List<Integer> normalIndices = new ArrayList<>();

    public int ambient = -1;
    public int diffuse = -1;
    public int specular = -1;
}
