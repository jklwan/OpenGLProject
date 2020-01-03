package com.chends.opengl.utils.model;

import android.content.res.Resources;

import com.chends.opengl.model.model.MtlBean;
import com.chends.opengl.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 加载纹理
 * @author chends create on 2020/1/2.
 */
public class LoadMtlUtil {

    /**
     * 读取 mtl
     * @param assets assets
     * @param res res
     */
    public static Map<String, MtlBean> loadMtl(String assets, Resources res){
        InputStream stream;
        try {
            stream = res.getAssets().open(assets);
        } catch (IOException e) {
            stream = null;
            LogUtil.e(e);
        }
        return loadMtl(stream);
    }

    public static Map<String, MtlBean> loadMtl(InputStream in){
        Map<String, MtlBean> result = new HashMap<>();

        return result;
    }
}
