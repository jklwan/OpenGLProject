package com.chends.opengl.view.advanced.opengl;

import android.view.View;

import com.chends.opengl.view.BaseChangeFragment;

/**
 * 实例化
 * @author chends create on 2020/3/6.
 */
public class InstancingFragment extends BaseChangeFragment {

    private final String[] title = new String[]{"多正方形", "多正方形变换", "小行星带（不使用实例化2000个）", "小行星带（实例化10w个）"};

    @Override
    protected View onChangeClick(int type) {
        return new InstancingView(getContext(), type);
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
