package com.sxhxjy.defensemonitor.entity;

import java.io.Serializable;

/**
 * 2016/9/18
 *
 * @author Michael Zhao
 */
public class SimpleItem implements Serializable {
    /**
     * 下拉单选列表实体类
     */
    String id;
    String title;
    boolean checked;

    int color; // to draw line

    String code; // position


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public SimpleItem() {}

    public SimpleItem(String id, String title, boolean checked) {
        this.id = id;
        this.title = title;
        this.checked = checked;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
