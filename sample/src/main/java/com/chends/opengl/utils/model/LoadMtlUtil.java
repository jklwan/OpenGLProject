package com.chends.opengl.utils.model;

import android.content.res.Resources;
import android.text.TextUtils;

import com.chends.opengl.model.model.MtlBean;
import com.chends.opengl.utils.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 加载纹理
 * @author chends create on 2020/1/2.
 */
public class LoadMtlUtil {

    /**
     * 材料名
     */
    private static final String NEWMTL = "newmtl";
    /**
     * 环境光（ambient color）
     */
    private static final String KA = "Ka";
    /**
     * 散射光（diffuse color）
     */
    private static final String KD = "Kd";
    /**
     * 镜面光（specular color）
     */
    private static final String KS = "Ks";
    /**
     * 反光度 Shininess
     */
    private static final String NS = "Ns";
    /**
     * 溶解
     */
    private static final String D = "d";
    /**
     * 溶解
     */
    private static final String TR = "Tr";
    /**
     * 环境光贴图
     */
    private static final String MAP_KA = "map_Ka";
    /**
     * 漫反射贴图 一般和环境光贴图相同
     */
    private static final String MAP_KD = "map_Kd";
    /**
     * 镜面光贴图
     */
    private static final String MAP_KS = "map_Ks";
    /**
     * 镜面反光度 贴图
     */
    private static final String MAP_NS = "map_Ns";
    /**
     * 透明度
     */
    private static final String MAP_D = "map_d";
    /**
     * 透明度
     */
    private static final String MAP_TR = "map_Tr";
    private static final String MAP_BUMP = "map_Bump";

    /**
     * 读取 mtl
     * @param assets assets
     * @param res    res
     */
    public static Map<String, MtlBean> loadMtl(String assets, Resources res) {
        InputStream stream;
        try {
            stream = res.getAssets().open(assets);
        } catch (IOException e) {
            stream = null;
            LogUtil.e(e);
        }
        return loadMtl(stream);
    }

    public static Map<String, MtlBean> loadMtl(InputStream stream) {
        Map<String, MtlBean> result = new HashMap<>();
        if (stream != null) {
            BufferedReader buffer = null;
            try {
                buffer = new BufferedReader(new InputStreamReader(stream));
                String line, type;
                StringTokenizer parts;
                MtlBean currMtl = null;
                while ((line = buffer.readLine()) != null) {
                    if (TextUtils.isEmpty(line.trim()) || line.trim().startsWith("#")) {
                        continue;
                    }
                    //
                    parts = new StringTokenizer(line.trim(), " ");
                    int numTokens = parts.countTokens();
                    if (numTokens == 0) {
                        continue;
                    }
                    //
                    type = parts.nextToken();
                    type = type.replaceAll("\\t", "");
                    type = type.replaceAll(" ", "");

                    if (TextUtils.equals(NEWMTL, type)) {
                        String name = parts.hasMoreTokens() ? parts.nextToken() : "def";
                        // 将上一个对象加入到列表中
                        if (currMtl != null) {
                            result.put(currMtl.name, currMtl);
                        }
                        // 创建材质对象
                        currMtl = new MtlBean();
                        // 材质对象名称
                        currMtl.name = name;
                    } else {
                        if (currMtl != null) {
                            switch (type) {
                                case KA:
                                    currMtl.Ka_Color = getColorFromParts(parts);
                                    break;
                                case KD:
                                    currMtl.Kd_Color = getColorFromParts(parts);
                                    break;
                                case KS:
                                    currMtl.Ks_Color = getColorFromParts(parts);
                                    break;
                                case NS:
                                    currMtl.ns = Float.parseFloat(parts.nextToken());
                                    break;
                                case D:
                                    currMtl.alpha = Float.parseFloat(parts.nextToken());
                                    break;
                                case TR:
                                    currMtl.alpha = 1 - Float.parseFloat(parts.nextToken());
                                    break;
                                case MAP_KA:
                                    currMtl.Ka_Texture = parts.nextToken();
                                    break;
                                case MAP_KD:
                                    currMtl.Kd_Texture = parts.nextToken();
                                    break;
                                case MAP_KS:
                                    currMtl.Ks_Texture = parts.nextToken();
                                    break;
                                case MAP_NS:
                                    currMtl.Ns_Texture = parts.nextToken();
                                    break;
                                case MAP_D:
                                case MAP_TR:
                                    currMtl.alphaTexture = parts.nextToken();
                                    break;
                                case MAP_BUMP:
                                    currMtl.bumpTexture = parts.nextToken();
                                    break;
                            }
                        }
                    }

                }
                if (currMtl != null) {
                    result.put(currMtl.name, currMtl);
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

    /**
     * 返回 颜色值
     * @param parts parts
     * @return color
     */
    private static float[] getColorFromParts(StringTokenizer parts) {
        return new float[]{Float.parseFloat(parts.nextToken()),
                Float.parseFloat(parts.nextToken()),
                Float.parseFloat(parts.nextToken())};
    }

}
