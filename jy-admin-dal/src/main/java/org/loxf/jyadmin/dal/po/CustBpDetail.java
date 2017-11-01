package org.loxf.jyadmin.dal.po;

import org.loxf.jyadmin.base.bean.BaseModel;

import java.util.Date;

public class CustBpDetail extends BaseModel {
    private String custId;

    private String detailName;

    private Integer type;

    private Long bpBalance;

    private Integer changeBalance;

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId == null ? null : custId.trim();
    }

    public String getDetailName() {
        return detailName;
    }

    public void setDetailName(String detailName) {
        this.detailName = detailName == null ? null : detailName.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getBpBalance() {
        return bpBalance;
    }

    public void setBpBalance(Long bpBalance) {
        this.bpBalance = bpBalance;
    }

    public Integer getChangeBalance() {
        return changeBalance;
    }

    public void setChangeBalance(Integer changeBalance) {
        this.changeBalance = changeBalance;
    }
}