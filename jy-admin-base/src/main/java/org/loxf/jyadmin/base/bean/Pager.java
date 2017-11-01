package org.loxf.jyadmin.base.bean;

import java.io.Serializable;

public class Pager implements Serializable {
    private int total = 0;
    private int size = 20;
    private int page = 1;
    private int start = 1;
    private boolean isCount = true;

    public Pager() {
    }

    public Pager(int page, int rows) {
        this.page = page;
        this.size = rows;
    }

    public int getStartIndex() {
        return this.getPage() <= 0 ? 0 : this.getSize() * (this.getPage() - 1);
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        if (page <= 0) {
            this.page = 1;
        } else {
            this.page = page;
        }

    }

    public int getStart() {
        if(page<=0) {
            page = 1;
        }
        return (page-1)*size;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getSize() {
        if(this.size<=0){
            this.size = 20;
        }
        return this.size;
    }

    public void setSize(int size) {
        if(this.size<=0){
            this.size = 20;
        } else {
            this.size = size;
        }
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isCount() {
        return this.isCount;
    }

    public void setCount(boolean isCount) {
        this.isCount = isCount;
    }

}
