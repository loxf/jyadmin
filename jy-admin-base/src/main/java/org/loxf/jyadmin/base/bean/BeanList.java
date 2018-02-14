package org.loxf.jyadmin.base.bean;

import java.util.List;

public class BeanList <T> {
    public List<T> list;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
