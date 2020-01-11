package com.chends.opengl.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chends.opengl.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @author chends create on 2020/1/11.
 */
public abstract class BaseChangeFragment  extends Fragment {

    private ViewGroup containerLayout;
    private int type = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change, container, false);
        Button button = view.findViewById(R.id.change);
        button.setOnClickListener(new ChangeClick());
        containerLayout = view.findViewById(R.id.container);
        button.performClick();
        return view;
    }

    private class ChangeClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (type > getTypeMax()) {
                type = 0;
            }
            containerLayout.removeAllViews();
            containerLayout.addView(onChangeClick(type), new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            type++;
        }
    }

    /**
     * 返回需要的view
     * @param type type
     * @return view
     */
    protected abstract View onChangeClick(int type);

    /**
     * 有多少种type
     * @return 最大type值
     */
    protected abstract int getTypeMax();
}
