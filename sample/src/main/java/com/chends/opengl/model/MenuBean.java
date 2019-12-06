package com.chends.opengl.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chends create on 2019/12/5.
 */
public class MenuBean {
    public String title;
    public List<MenuItemBean> list;

    public MenuBean(String title) {
        this.title = title;
    }

    public void addItem(MenuItemBean item){
        if (list == null){
            list = new ArrayList<>();
        }
        list.add(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuBean)) return false;

        MenuBean menuBean = (MenuBean) o;
        return title.equals(menuBean.title);
    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }
}
