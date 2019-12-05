package com.chends.opengl;

import android.os.Bundle;
import android.view.ViewGroup;

import com.chends.opengl.model.MenuBean;
import com.chends.opengl.model.MenuItemBean;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * main
 */
public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView menu1, menu2;
    private MenuAdapter mainAdapter;
    private SubAdapter subAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        menu1 = findViewById(R.id.menu1);
        menu2 = findViewById(R.id.menu2);
        setData();
    }

    private void setData() {
        List<MenuBean> list = new ArrayList<>();
        MenuBean window = new MenuBean("窗口");
        window.addItem(new MenuItemBean("创建窗口", null));
        list.add(window);
        MenuBean texture = new MenuBean("纹理");
        list.add(texture);
        menu1.setLayoutManager(new LinearLayoutManager(this));
        menu2.setLayoutManager(new LinearLayoutManager(this));
        subAdapter = new SubAdapter();
        menu2.setAdapter(subAdapter);
        mainAdapter = new MenuAdapter(list);
        menu1.setAdapter(mainAdapter);
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


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    private class SubAdapter extends RecyclerView.Adapter {
        private List<MenuItemBean> list = new ArrayList<>();
        private MenuItemBean select;

        public void setData(List<MenuItemBean> list) {
            if (list != null && !list.isEmpty()) {
                this.list.addAll(list);
                select = list.get(0);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
