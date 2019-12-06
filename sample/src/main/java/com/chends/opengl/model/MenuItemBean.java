package com.chends.opengl.model;

/**
 * @author chends create on 2019/12/5.
 */
public class MenuItemBean {
    public String title;
    public Class fCls;

    public MenuItemBean(String title, Class fCls) {
        this.title = title;
        this.fCls = fCls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItemBean)) return false;

        MenuItemBean that = (MenuItemBean) o;

        if (!title.equals(that.title)) return false;
        return fCls != null ? fCls.equals(that.fCls) : that.fCls == null;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + (fCls != null ? fCls.hashCode() : 0);
        return result;
    }
}
