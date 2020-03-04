package com.chends.opengl.view.advanced.opengl;

import android.view.View;

import com.chends.opengl.view.BaseChangeFragment;

/**
 * @author chends create on 2020/1/6.
 */
public class DepthTestingFragment extends BaseChangeFragment {

    @Override
    protected View onChangeClick(int type) {
        return new DepthTestingView(getContext(), type);
    }

    @Override
    protected int getTypeMax() {
        return 3;
    }
}
