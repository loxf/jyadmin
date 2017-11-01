package org.loxf.jyadmin.base.bean;

import java.io.Serializable;
import java.util.Date;

public class BaseModel implements Serializable {
    private Long id;

    private Date createdAt;

    private Date updatedAt;

    private String rangDate;

    private String startDate;

    private String endDate;

    private Short isDeleted;

    private Pager pager;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRangDate() {
        return rangDate;
    }

    public void setRangDate(String rangDate) {
        this.rangDate = rangDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Short getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Short isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Pager getPager() {
        if(pager==null){
            Pager pager = new Pager();
            this.pager = pager;
        }
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }
}
