package com.chends.opengl.view.advanced.opengl;

import android.view.View;

import com.chends.opengl.view.BaseChangeFragment;

/**
 * 几何着色器
 * @author chends create on 2020/3/3.
 */
public class GeometryShaderFragment extends BaseChangeFragment {

    private final String[] title = new String[]{"点", "线", "房子", "纳米装"};

    @Override
    protected View onChangeClick(int type) {
        return new GeometryShaderView(getContext(), type);
    }

    @Override
    protected int getTypeMax() {
        return title.length;
    }

    @Override
    protected CharSequence getTypeText(int type) {
        return title[type];
    }
}
