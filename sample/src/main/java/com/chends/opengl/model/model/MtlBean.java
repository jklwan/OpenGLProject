package com.chends.opengl.model.model;

/**
 * 材料/纹理数据
 * @author chends create on 2020/1/2.
 */
public class MtlBean {
    /**
     * 名称
     */
    public String name;
    /**
     * 环境光
     */
    public float[] Ka_Color = {1f, 1f, 1f};
    /**
     * 漫反射
     */
    public float[] Kd_Color = {1f, 1f, 1f};
    /**
     * 镜面光
     */
    public float[] Ks_Color = {1f, 1f, 1f};
    /**
     * 反光度
     */
    public float ns;
    /**
     * 透明度，为0时完全透明，1完全不透明
     */
    public float alpha = 1f;

    /**
     * 环境光贴图
     */
    public String Ka_Texture;
    /**
     * 漫反射贴图 一般和环境光贴图相同
     */
    public String Kd_Texture;
    /**
     * 镜面光贴图
     */
    public String Ks_Texture;
    /**
     * 镜面反光度 贴图
     */
    public String Ns_Texture;
    /**
     * 透明度
     */
    public String alphaTexture;
    public String bumpTexture;
}
