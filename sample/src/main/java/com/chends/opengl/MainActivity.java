package com.chends.opengl;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chends.opengl.model.MenuBean;
import com.chends.opengl.model.MenuItemBean;
import com.chends.opengl.utils.DisplayUtil;
import com.chends.opengl.utils.LogUtil;
import com.chends.opengl.utils.OpenGLUtil;
import com.chends.opengl.view.advanced.opengl.BlendingView;
import com.chends.opengl.view.advanced.opengl.DepthTestingFragment;
import com.chends.opengl.view.advanced.opengl.StencilTestingView;
import com.chends.opengl.view.light.LightCastersDirectionalView;
import com.chends.opengl.view.light.LightCastersPointView;
import com.chends.opengl.view.light.LightCastersSpotLightView;
import com.chends.opengl.view.light.LightMapsView;
import com.chends.opengl.view.light.LightMultipleLightsView;
import com.chends.opengl.view.light.LightView;
import com.chends.opengl.view.light.MaterialsView;
import com.chends.opengl.view.light.PhongLightView;
import com.chends.opengl.view.model.LoadModelMaterialView;
import com.chends.opengl.view.model.LoadModelTextureView;
import com.chends.opengl.view.model.LoadModelView;
import com.chends.opengl.view.texture.TextureColorView;
import com.chends.opengl.view.texture.TextureOverView;
import com.chends.opengl.view.texture.TextureView;
import com.chends.opengl.view.window.CubeView;
import com.chends.opengl.view.window.PointLineView;
import com.chends.opengl.view.window.SquareView;
import com.chends.opengl.view.window.TriangleColorView;
import com.chends.opengl.view.window.TriangleMatrixView;
import com.chends.opengl.view.window.TriangleView;
import com.chends.opengl.view.window.WindowView;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * main
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FrameLayout content;
    private ActionBar actionBar;
    private RecyclerView menu1, menu2;
    private SubAdapter subAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        View menu = findViewById(R.id.menuLayout);
        ViewGroup.LayoutParams lp = menu.getLayoutParams();
        lp.width = Math.min(DisplayUtil.dp2px(350), DisplayUtil.screenWidth() * 3 / 4);
        menu.setLayoutParams(lp);
        actionBar = getSupportActionBar();
        content = findViewById(R.id.content);
        menu1 = findViewById(R.id.menu1);
        menu2 = findViewById(R.id.menu2);
        if (!OpenGLUtil.checkOpenGLES20(this)) {
            toast("当前设备不支持OpenGL ES 2.0");
            finish();
            return;
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        setData();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    private void toast(CharSequence s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void setData() {
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                drawerLayout.bringChildToFront(drawerView);
                drawerLayout.requestLayout();
            }
        });
        List<MenuBean> list = new ArrayList<>();
        MenuBean window = new MenuBean("窗口");
        window.addItem(new MenuItemBean("创建窗口", WindowView.class));
        window.addItem(new MenuItemBean("点和线", PointLineView.class));
        window.addItem(new MenuItemBean("创建三角", TriangleView.class));
        window.addItem(new MenuItemBean("矩形", SquareView.class));
        window.addItem(new MenuItemBean("彩色三角形", TriangleColorView.class));
        window.addItem(new MenuItemBean("变换", TriangleMatrixView.class));
        window.addItem(new MenuItemBean("立方体", CubeView.class));
        list.add(window);

        MenuBean texture = new MenuBean("纹理");
        texture.addItem(new MenuItemBean("创建纹理", TextureView.class));
        texture.addItem(new MenuItemBean("纹理颜色", TextureColorView.class));
        texture.addItem(new MenuItemBean("纹理叠加", TextureOverView.class));
        list.add(texture);

        MenuBean light = new MenuBean("光照");
        light.addItem(new MenuItemBean("简单光照", LightView.class));
        light.addItem(new MenuItemBean("Phong光照", PhongLightView.class));
        light.addItem(new MenuItemBean("材质", MaterialsView.class));
        light.addItem(new MenuItemBean("光照贴图", LightMapsView.class));
        light.addItem(new MenuItemBean("投光物-定向光", LightCastersDirectionalView.class));
        light.addItem(new MenuItemBean("投光物-点光源", LightCastersPointView.class));
        light.addItem(new MenuItemBean("投光物-聚光", LightCastersSpotLightView.class));
        light.addItem(new MenuItemBean("多光源", LightMultipleLightsView.class));
        list.add(light);

        MenuBean model = new MenuBean("模型");
        model.addItem(new MenuItemBean("模型加载-基础", LoadModelView.class));
        model.addItem(new MenuItemBean("模型加载-材料", LoadModelMaterialView.class));
        model.addItem(new MenuItemBean("模型加载-纹理", LoadModelTextureView.class));
        list.add(model);

        MenuBean aOpenGL = new MenuBean("高级OpenGL");
        aOpenGL.addItem(new MenuItemBean("深度测试", DepthTestingFragment.class));
        aOpenGL.addItem(new MenuItemBean("模板测试", StencilTestingView.class));
        aOpenGL.addItem(new MenuItemBean("混合", BlendingView.class));
        list.add(aOpenGL);

        menu1.setLayoutManager(new LinearLayoutManager(this));
        menu2.setLayoutManager(new LinearLayoutManager(this));
        subAdapter = new SubAdapter();
        menu2.setAdapter(subAdapter);
        menu1.setAdapter(new MenuAdapter(list));
    }

    public void afterSelect(MenuItemBean item) {
        actionBar.setTitle(item.title);
        List<Fragment> list = getSupportFragmentManager().getFragments();
        if (!list.isEmpty()){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            for (Fragment fragment : list){
                transaction.remove(fragment);
            }
            transaction.commitNowAllowingStateLoss();
        } else {
            content.removeAllViews();
        }
        if (item.fCls == null) return;
        try {
            Constructor<?> viewCons = item.fCls.getConstructor(new Class[]{Context.class});
            Object object = viewCons.newInstance(this);
            if (object instanceof View) {
                content.addView((View) object, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } catch (Exception e) {
            LogUtil.e(e);
            try {
                Constructor<?> fragmentCons = item.fCls.getConstructor(new Class[]{});
                Object object = fragmentCons.newInstance();
                if (object instanceof Fragment) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content, (Fragment) object)
                            .commitNowAllowingStateLoss();
                }
            } catch (Exception ex) {
                LogUtil.e(e);
            }
        }
    }

    private class MenuAdapter extends RecyclerView.Adapter {
        private List<MenuBean> list = new ArrayList<>();
        private MenuBean select;

        public MenuAdapter(List<MenuBean> list) {
            if (list != null && !list.isEmpty()) {
                this.list.addAll(list);
                select = list.get(0);
                subAdapter.setData(select.list);
            }
        }

        public boolean updateSelect(MenuBean bean) {
            if (bean != null) {
                if (!bean.equals(select)) {
                    int old = list.indexOf(select), newP = list.indexOf(bean);
                    select = bean;
                    if (old != -1) {
                        notifyItemChanged(old);
                    }
                    if (newP != -1) {
                        notifyItemChanged(newP);
                    }
                    return true;
                }
            }
            return false;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ItemHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_menu, parent, false));
        }

        public MenuBean getItem(int position) {
            if (position >= 0 && position < list.size()) {
                return list.get(position);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ItemHolder) {
                ((ItemHolder) holder).bind(getItem(position));
            }
        }

        public class ItemHolder extends RecyclerView.ViewHolder {

            private TextView textView;

            public ItemHolder(@NonNull View itemView) {
                super(itemView);
                textView = (TextView) itemView;
            }


            public void bind(MenuBean data) {
                if (data != null) {
                    textView.setText(data.title);
                    textView.setSelected(data.equals(select));
                    textView.setOnClickListener(new ItemClick(data));
                }
            }

            private class ItemClick implements View.OnClickListener {
                private MenuBean data;

                public ItemClick(MenuBean data) {
                    this.data = data;
                }

                @Override
                public void onClick(View v) {
                    if (data != null && updateSelect(data)) {
                        subAdapter.setData(data.list);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private class SubAdapter extends RecyclerView.Adapter {
        private List<MenuItemBean> list = new ArrayList<>();
        private MenuItemBean select;

        public void setData(List<MenuItemBean> list) {
            this.list.clear();
            select = null;
            if (list != null && !list.isEmpty()) {
                this.list.addAll(list);
                updateSelect(list.get(0));
            }
            notifyDataSetChanged();
        }

        public void updateSelect(MenuItemBean item) {
            if (item != null) {
                if (!item.equals(select)) {
                    int old = list.indexOf(select), newP = list.indexOf(item);
                    select = item;
                    afterSelect(select);
                    if (old != -1) {
                        notifyItemChanged(old);
                    }
                    if (newP != -1) {
                        notifyItemChanged(newP);
                    }
                }
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ItemHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_menu, parent, false));
        }

        public MenuItemBean getItem(int position) {
            if (position >= 0 && position < list.size()) {
                return list.get(position);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ItemHolder) {
                ((ItemHolder) holder).bind(getItem(position));
            }
        }


        public class ItemHolder extends RecyclerView.ViewHolder {

            private TextView textView;

            public ItemHolder(@NonNull View itemView) {
                super(itemView);
                textView = (TextView) itemView;
            }


            public void bind(MenuItemBean data) {
                if (data != null) {
                    textView.setText(data.title);
                    textView.setSelected(data.equals(select));
                    textView.setOnClickListener(new ItemClick(data));
                }
            }

            private class ItemClick implements View.OnClickListener {
                private MenuItemBean data;

                public ItemClick(MenuItemBean data) {
                    this.data = data;
                }

                @Override
                public void onClick(View v) {
                    if (data != null) {
                        updateSelect(data);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
