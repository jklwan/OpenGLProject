package com.chends.opengl.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 多类型
 * @author chends create on 2020/1/11.
 */
public class BaseTypeGLView extends BaseGLView {
    protected int type;

    public BaseTypeGLView(Context context, int type) {
        this(context, null, type);
    }

    public BaseTypeGLView(Context context, AttributeSet attrs, int type) {
        super(context, attrs);
        this.type = type;
        init();
    }

    @Override
    protected boolean createInit() {
        return false;
    }

}